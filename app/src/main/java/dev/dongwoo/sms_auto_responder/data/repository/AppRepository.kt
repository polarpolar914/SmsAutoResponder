package dev.dongwoo.sms_auto_responder.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.dongwoo.sms_auto_responder.data.dao.HistoryDao
import dev.dongwoo.sms_auto_responder.data.dao.KeywordDao
import dev.dongwoo.sms_auto_responder.data.dao.PhoneNumberDao
import dev.dongwoo.sms_auto_responder.data.entity.HistoryEntity
import dev.dongwoo.sms_auto_responder.data.entity.KeywordEntity
import dev.dongwoo.sms_auto_responder.data.entity.PhoneNumberEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val phoneNumberDao: PhoneNumberDao,
    private val keywordDao: KeywordDao,
    private val historyDao: HistoryDao,
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // --- PhoneNumber ---
    val allPhoneNumbers: Flow<List<PhoneNumberEntity>> = phoneNumberDao.getAllPhoneNumbers()

    suspend fun getPhoneNumberById(id: Int) = phoneNumberDao.getPhoneNumberById(id)

    suspend fun insertPhoneNumber(phoneNumber: PhoneNumberEntity) = phoneNumberDao.insertPhoneNumber(phoneNumber)

    suspend fun updatePhoneNumber(phoneNumber: PhoneNumberEntity) = phoneNumberDao.updatePhoneNumber(phoneNumber)

    suspend fun deletePhoneNumber(phoneNumber: PhoneNumberEntity) = phoneNumberDao.deletePhoneNumber(phoneNumber)

    suspend fun updateLastSentTime(id: Int, time: Long) = phoneNumberDao.updateLastSentTime(id, time)

    // --- Keyword ---
    fun getKeywordsForPhoneNumber(phoneNumberId: Int) = keywordDao.getKeywordsForPhoneNumber(phoneNumberId)

    suspend fun getKeywordsForPhoneNumberSync(phoneNumberId: Int) = keywordDao.getKeywordsForPhoneNumberSync(phoneNumberId)

    suspend fun insertKeyword(keyword: KeywordEntity) = keywordDao.insertKeyword(keyword)

    suspend fun deleteKeyword(keyword: KeywordEntity) = keywordDao.deleteKeyword(keyword)

    suspend fun deleteKeywordsByPhoneNumberId(phoneNumberId: Int) = keywordDao.deleteKeywordsByPhoneNumberId(phoneNumberId)

    // --- History ---
    val allHistory: Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    suspend fun insertHistory(history: HistoryEntity) = historyDao.insertHistory(history)

    // --- Preferences (App Monitoring & Settings) ---
    // Storing monitored apps as a Set of package names
    fun getMonitoredApps(): Set<String> {
        return prefs.getStringSet("monitored_apps", emptySet()) ?: emptySet()
    }

    fun setMonitoredApps(apps: Set<String>) {
        prefs.edit().putStringSet("monitored_apps", apps).apply()
    }

    fun isAppMonitored(packageName: String): Boolean {
        return getMonitoredApps().contains(packageName)
    }

    fun getGlobalTemplate(): String {
        return prefs.getString("global_template", "") ?: ""
    }

    fun setGlobalTemplate(template: String) {
        prefs.edit().putString("global_template", template).apply()
    }

    fun isNightMode(): Boolean {
         return prefs.getBoolean("night_mode", false)
    }

    fun setNightMode(enabled: Boolean) {
        prefs.edit().putBoolean("night_mode", enabled).apply()
    }
}
