package dev.dongwoo.sms_auto_responder.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import dev.dongwoo.sms_auto_responder.data.entity.HistoryEntity
import dev.dongwoo.sms_auto_responder.data.repository.AppRepository
import dev.dongwoo.sms_auto_responder.util.SmsSender
import dev.dongwoo.sms_auto_responder.util.TemplateParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var repository: AppRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val smsSender = SmsSender()

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return

        val packageName = sbn.packageName
        val extras = sbn.notification.extras
        val title = extras.getString("android.title") ?: ""
        val text = extras.getString("android.text") ?: ""
        val content = "$title $text"

        // Basic self-check to avoid loops if needed, though usually SMS app is different
        if (packageName == this.packageName) return

        scope.launch {
            if (!repository.isAppMonitored(packageName)) return@launch

            if (repository.isNightMode()) {
                 // Simple night mode logic: assume 10PM - 7AM, or just skip if enabled for now.
                 // Ideally we'd check current time vs configured quiet hours.
                 // For this MVP let's assume if night mode is ON, we don't send.
                 return@launch
            }

            // Iterate through all configured phone numbers
            repository.allPhoneNumbers.collect { phoneNumbers ->
                phoneNumbers.forEach { phoneNumber ->
                    val keywords = repository.getKeywordsForPhoneNumberSync(phoneNumber.id)
                    var shouldSend = false
                    var triggeredKeyword = ""

                    // Check keywords
                    if (keywords.isEmpty()) {
                        // If no keywords, maybe we don't send? Or send on everything?
                        // Requirement says "conditions match". Implicitly means at least one keyword match or empty list means all?
                        // Let's assume must match at least one keyword if defined.
                        // But wait, "Step 2: Keyword Rules" implies we define rules.
                    }

                    for (keyword in keywords) {
                        // Check if keyword applies to this app
                        if (keyword.targetAppPackage != null && keyword.targetAppPackage != packageName) {
                            continue
                        }

                        val matches = if (keyword.isRegex) {
                            try {
                                Regex(keyword.keyword).containsMatchIn(content)
                            } catch (e: Exception) { false }
                        } else {
                            content.contains(keyword.keyword, ignoreCase = true)
                        }

                        if (matches) {
                            if (keyword.isExclude) {
                                shouldSend = false
                                break // Explicit exclusion overrides everything
                            } else {
                                shouldSend = true
                                triggeredKeyword = keyword.keyword
                            }
                        }
                    }

                    if (shouldSend) {
                        val template = phoneNumber.template ?: repository.getGlobalTemplate()
                        if (template.isNotEmpty()) {
                            val message = TemplateParser.parse(
                                template,
                                triggeredKeyword,
                                packageName, // TODO: Get App Name from package
                                content,
                                System.currentTimeMillis()
                            )

                            smsSender.sendSms(this@MyNotificationListenerService, phoneNumber.phoneNumber, message)

                            // Log history
                            repository.insertHistory(
                                HistoryEntity(
                                    phoneNumber = phoneNumber.phoneNumber,
                                    messageContent = message,
                                    triggeredKeyword = triggeredKeyword,
                                    appPackage = packageName,
                                    timestamp = System.currentTimeMillis()
                                )
                            )

                            repository.updateLastSentTime(phoneNumber.id, System.currentTimeMillis())
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
