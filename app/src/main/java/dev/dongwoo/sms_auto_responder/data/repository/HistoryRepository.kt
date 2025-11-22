package dev.dongwoo.sms_auto_responder.data.repository

import dev.dongwoo.sms_auto_responder.data.dao.HistoryDao
import dev.dongwoo.sms_auto_responder.data.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    val allHistory: Flow<List<HistoryEntity>> = historyDao.getAllHistory()
    val successCount: Flow<Int> = historyDao.getSuccessCount()

    suspend fun logHistory(
        ruleName: String,
        sentTo: String,
        content: String,
        status: String
    ) {
        val history = HistoryEntity(
            ruleName = ruleName,
            sentTo = sentTo,
            content = content,
            sentAt = System.currentTimeMillis(),
            status = status
        )
        historyDao.insertHistory(history)
    }
}
