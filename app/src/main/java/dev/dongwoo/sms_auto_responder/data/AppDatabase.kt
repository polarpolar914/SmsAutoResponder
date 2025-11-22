package dev.dongwoo.sms_auto_responder.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.dongwoo.sms_auto_responder.data.dao.HistoryDao
import dev.dongwoo.sms_auto_responder.data.dao.KeywordDao
import dev.dongwoo.sms_auto_responder.data.dao.PhoneNumberDao
import dev.dongwoo.sms_auto_responder.data.entity.HistoryEntity
import dev.dongwoo.sms_auto_responder.data.entity.KeywordEntity
import dev.dongwoo.sms_auto_responder.data.entity.PhoneNumberEntity

@Database(
    entities = [PhoneNumberEntity::class, KeywordEntity::class, HistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun phoneNumberDao(): PhoneNumberDao
    abstract fun keywordDao(): KeywordDao
    abstract fun historyDao(): HistoryDao
}
