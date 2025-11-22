package dev.dongwoo.sms_auto_responder.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.dongwoo.sms_auto_responder.data.dao.HistoryDao
import dev.dongwoo.sms_auto_responder.data.dao.RuleDao
import dev.dongwoo.sms_auto_responder.data.entity.HistoryEntity
import dev.dongwoo.sms_auto_responder.data.entity.KeywordEntity
import dev.dongwoo.sms_auto_responder.data.entity.RuleAppEntity
import dev.dongwoo.sms_auto_responder.data.entity.RuleEntity

@Database(
    entities = [RuleEntity::class, RuleAppEntity::class, KeywordEntity::class, HistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ruleDao(): RuleDao
    abstract fun historyDao(): HistoryDao
}
