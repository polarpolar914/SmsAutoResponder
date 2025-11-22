package dev.dongwoo.sms_auto_responder.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phone_numbers")
data class PhoneNumberEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phoneNumber: String,
    val tags: String, // Comma separated tags
    val template: String?, // Custom template for this number, null means use global
    val lastSentTime: Long = 0
)

@Entity(tableName = "keywords")
data class KeywordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phoneNumberId: Int, // Foreign key to PhoneNumberEntity
    val keyword: String,
    val isRegex: Boolean = false,
    val isExclude: Boolean = false, // If true, this is an exclusion rule
    val targetAppPackage: String? = null // If null, applies to all apps
)

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phoneNumber: String,
    val messageContent: String,
    val triggeredKeyword: String,
    val appPackage: String,
    val timestamp: Long
)
