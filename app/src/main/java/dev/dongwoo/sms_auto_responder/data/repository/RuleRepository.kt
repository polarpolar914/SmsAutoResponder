package dev.dongwoo.sms_auto_responder.data.repository

import dev.dongwoo.sms_auto_responder.data.dao.RuleDao
import dev.dongwoo.sms_auto_responder.data.dao.RuleWithDetails
import dev.dongwoo.sms_auto_responder.data.entity.RuleEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RuleRepository @Inject constructor(
    private val ruleDao: RuleDao
) {
    val allRules: Flow<List<RuleWithDetails>> = ruleDao.getAllRulesWithDetailsFlow()

    suspend fun addRule(
        name: String,
        phoneNumber: String,
        targetApps: List<String>,
        keywords: List<String>
    ) {
        val rule = RuleEntity(
            ruleName = name,
            isEnabled = true,
            phoneNumber = phoneNumber,
            msgTemplate = "{{text}}", // Always forward full notification text
            createdAt = System.currentTimeMillis()
        )
        // Split keywords into (keyword, type) pairs. Currently spec implies "INCLUDE" logic for entered keywords.
        // "포함할 키워드 (엔터로 추가)" -> type = "INCLUDE"
        val keywordPairs = keywords.map { it to "INCLUDE" }

        ruleDao.insertRuleWithDetails(rule, targetApps, keywordPairs)
    }

    suspend fun getRuleById(ruleId: Int): RuleWithDetails? {
        return ruleDao.getRuleById(ruleId)
    }

    suspend fun updateRule(
        ruleId: Int,
        name: String,
        phoneNumber: String,
        targetApps: List<String>,
        keywords: List<String>,
        isEnabled: Boolean
    ) {
        val rule = RuleEntity(
            ruleId = ruleId,
            ruleName = name,
            isEnabled = isEnabled,
            phoneNumber = phoneNumber,
            msgTemplate = "{{text}}", // Always forward full notification text
            createdAt = System.currentTimeMillis()
        )
        val keywordPairs = keywords.map { it to "INCLUDE" }
        ruleDao.updateRuleWithDetails(rule, targetApps, keywordPairs)
    }

    suspend fun updateRuleStatus(ruleId: Int, isEnabled: Boolean) {
        ruleDao.updateRuleStatus(ruleId, isEnabled)
    }

    suspend fun deleteRule(ruleId: Int) {
        ruleDao.deleteRule(ruleId)
    }

    suspend fun getEnabledRules(): List<RuleWithDetails> {
        return ruleDao.getEnabledRulesWithDetails()
    }
}
