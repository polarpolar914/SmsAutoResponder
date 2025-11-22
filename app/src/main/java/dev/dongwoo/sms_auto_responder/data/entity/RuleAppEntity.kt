package dev.dongwoo.sms_auto_responder.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "rule_apps",
    foreignKeys = [
        ForeignKey(
            entity = RuleEntity::class,
            parentColumns = ["ruleId"],
            childColumns = ["ruleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RuleAppEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ruleId: Int,
    val packageName: String
)
