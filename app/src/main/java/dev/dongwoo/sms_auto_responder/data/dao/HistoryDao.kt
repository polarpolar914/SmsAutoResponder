package dev.dongwoo.sms_auto_responder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.dongwoo.sms_auto_responder.data.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("SELECT * FROM history ORDER BY sentAt DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT count(*) FROM history WHERE status = 'SUCCESS'")
    fun getSuccessCount(): Flow<Int>
}
