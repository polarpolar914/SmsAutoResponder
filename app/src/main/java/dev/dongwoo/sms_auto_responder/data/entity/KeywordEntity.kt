package dev.dongwoo.sms_auto_responder.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "keywords",
    foreignKeys = [
        ForeignKey(
            entity = RuleEntity::class,
            parentColumns = ["ruleId"],
            childColumns = ["ruleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class KeywordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ruleId: Int,
    val keyword: String,
    val type: String // "INCLUDE" or "EXCLUDE"
)
