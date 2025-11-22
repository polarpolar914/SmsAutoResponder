package dev.dongwoo.sms_auto_responder.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val historyId: Int = 0,
    val ruleName: String,
    val sentTo: String,
    val content: String,
    val sentAt: Long,
    val status: String // "SUCCESS" / "FAIL"
)
