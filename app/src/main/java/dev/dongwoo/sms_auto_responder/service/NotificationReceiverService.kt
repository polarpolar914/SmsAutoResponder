package dev.dongwoo.sms_auto_responder.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import dev.dongwoo.sms_auto_responder.data.repository.HistoryRepository
import dev.dongwoo.sms_auto_responder.data.repository.RuleRepository
import dev.dongwoo.sms_auto_responder.util.SmsSender
import dev.dongwoo.sms_auto_responder.util.TemplateParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiverService : NotificationListenerService() {

    @Inject
    lateinit var ruleRepository: RuleRepository

    @Inject
    lateinit var historyRepository: HistoryRepository

    @Inject
    lateinit var smsSender: SmsSender

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("NotificationReceiver", "Listener Connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val packageName = sbn.packageName
        val extras = sbn.notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

        Log.d("NotificationReceiver", "Notification from $packageName: $title - $text")

        serviceScope.launch {
            val enabledRules = ruleRepository.getEnabledRules()

            for (ruleWithDetails in enabledRules) {
                // Check App Package
                val isAppMatch = ruleWithDetails.apps.any { it.packageName == packageName }
                if (!isAppMatch) continue

                // Check Keywords
                val keywords = ruleWithDetails.keywords
                val isKeywordMatch = if (keywords.isEmpty()) {
                    true
                } else {
                    keywords.any { k ->
                        if (k.type == "INCLUDE") {
                            title.contains(k.keyword, ignoreCase = true) || text.contains(k.keyword, ignoreCase = true)
                        } else {
                            false
                        }
                    }
                }

                if (isKeywordMatch) {
                    // Send SMS
                    val message = TemplateParser.parse(ruleWithDetails.rule.msgTemplate, title, text)
                    val sent = smsSender.sendSms(ruleWithDetails.rule.phoneNumber, message)

                    // Log History
                    historyRepository.logHistory(
                        ruleName = ruleWithDetails.rule.ruleName,
                        sentTo = ruleWithDetails.rule.phoneNumber,
                        content = message,
                        status = if (sent) "SUCCESS" else "FAIL"
                    )
                }
            }
        }
    }
}
