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

    @Transaction
    @Query("SELECT * FROM rules WHERE ruleId = :ruleId")
    suspend fun getRuleById(ruleId: Int): RuleWithDetails?

    @Transaction
    suspend fun updateRuleWithDetails(rule: RuleEntity, apps: List<String>, keywords: List<Pair<String, String>>) {
        updateRule(rule)
        // Delete existing apps and keywords
        deleteRuleApps(rule.ruleId)
        deleteRuleKeywords(rule.ruleId)
        // Insert new ones
        val ruleAppEntities = apps.map { RuleAppEntity(ruleId = rule.ruleId, packageName = it) }
        val keywordEntities = keywords.map { KeywordEntity(ruleId = rule.ruleId, keyword = it.first, type = it.second) }
        insertRuleApps(ruleAppEntities)
        insertKeywords(keywordEntities)
    }

    @Query("DELETE FROM rule_apps WHERE ruleId = :ruleId")
    suspend fun deleteRuleApps(ruleId: Int)

    @Query("DELETE FROM keywords WHERE ruleId = :ruleId")
    suspend fun deleteRuleKeywords(ruleId: Int)

    @Update
    suspend fun updateRule(rule: RuleEntity)

    @Query("UPDATE rules SET isEnabled = :isEnabled WHERE ruleId = :ruleId")
    suspend fun updateRuleStatus(ruleId: Int, isEnabled: Boolean)

    @Query("DELETE FROM rules WHERE ruleId = :ruleId")
    suspend fun deleteRule(ruleId: Int)
}
