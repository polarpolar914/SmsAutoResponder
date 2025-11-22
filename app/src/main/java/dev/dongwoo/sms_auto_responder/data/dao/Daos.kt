package dev.dongwoo.sms_auto_responder.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.dongwoo.sms_auto_responder.data.entity.HistoryEntity
import dev.dongwoo.sms_auto_responder.data.entity.KeywordEntity
import dev.dongwoo.sms_auto_responder.data.entity.PhoneNumberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhoneNumberDao {
    @Query("SELECT * FROM phone_numbers")
    fun getAllPhoneNumbers(): Flow<List<PhoneNumberEntity>>

    @Query("SELECT * FROM phone_numbers WHERE id = :id")
    suspend fun getPhoneNumberById(id: Int): PhoneNumberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoneNumber(phoneNumber: PhoneNumberEntity): Long

    @Update
    suspend fun updatePhoneNumber(phoneNumber: PhoneNumberEntity)

    @Delete
    suspend fun deletePhoneNumber(phoneNumber: PhoneNumberEntity)

    @Query("UPDATE phone_numbers SET lastSentTime = :time WHERE id = :id")
    suspend fun updateLastSentTime(id: Int, time: Long)
}

@Dao
interface KeywordDao {
    @Query("SELECT * FROM keywords WHERE phoneNumberId = :phoneNumberId")
    fun getKeywordsForPhoneNumber(phoneNumberId: Int): Flow<List<KeywordEntity>>

    @Query("SELECT * FROM keywords WHERE phoneNumberId = :phoneNumberId")
    suspend fun getKeywordsForPhoneNumberSync(phoneNumberId: Int): List<KeywordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyword(keyword: KeywordEntity)

    @Delete
    suspend fun deleteKeyword(keyword: KeywordEntity)

    @Query("DELETE FROM keywords WHERE phoneNumberId = :phoneNumberId")
    suspend fun deleteKeywordsByPhoneNumberId(phoneNumberId: Int)
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert
    suspend fun insertHistory(history: HistoryEntity)
}
