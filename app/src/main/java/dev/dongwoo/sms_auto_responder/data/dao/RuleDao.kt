package dev.dongwoo.sms_auto_responder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Relation
import androidx.room.Embedded
import dev.dongwoo.sms_auto_responder.data.entity.KeywordEntity
import dev.dongwoo.sms_auto_responder.data.entity.RuleAppEntity
import dev.dongwoo.sms_auto_responder.data.entity.RuleEntity
import kotlinx.coroutines.flow.Flow

data class RuleWithDetails(
    @Embedded val rule: RuleEntity,
    @Relation(
        parentColumn = "ruleId",
        entityColumn = "ruleId"
    )
    val apps: List<RuleAppEntity>,
    @Relation(
        parentColumn = "ruleId",
        entityColumn = "ruleId"
    )
    val keywords: List<KeywordEntity>
)

@Dao
interface RuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: RuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRuleApps(apps: List<RuleAppEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeywords(keywords: List<KeywordEntity>)

    @Transaction
    suspend fun insertRuleWithDetails(rule: RuleEntity, apps: List<String>, keywords: List<Pair<String, String>>) {
        val ruleId = insertRule(rule).toInt()
        val ruleAppEntities = apps.map { RuleAppEntity(ruleId = ruleId, packageName = it) }
        val keywordEntities = keywords.map { KeywordEntity(ruleId = ruleId, keyword = it.first, type = it.second) }
        insertRuleApps(ruleAppEntities)
        insertKeywords(keywordEntities)
    }

    @Query("SELECT * FROM rules")
    fun getAllRules(): Flow<List<RuleEntity>>

    @Transaction
    @Query("SELECT * FROM rules ORDER BY createdAt DESC")
    fun getAllRulesWithDetailsFlow(): Flow<List<RuleWithDetails>>

    @Transaction
    @Query("SELECT * FROM rules WHERE isEnabled = 1")
    suspend fun getEnabledRulesWithDetails(): List<RuleWithDetails>

    @Update
    suspend fun updateRule(rule: RuleEntity)

    @Query("UPDATE rules SET isEnabled = :isEnabled WHERE ruleId = :ruleId")
    suspend fun updateRuleStatus(ruleId: Int, isEnabled: Boolean)

    @Query("DELETE FROM rules WHERE ruleId = :ruleId")
    suspend fun deleteRule(ruleId: Int)
}
