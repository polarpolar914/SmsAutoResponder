package dev.dongwoo.sms_auto_responder.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rules")
data class RuleEntity(
    @PrimaryKey(autoGenerate = true) val ruleId: Int = 0,
    val ruleName: String,
    val isEnabled: Boolean,
    val phoneNumber: String,
    val msgTemplate: String,
    val createdAt: Long
)
