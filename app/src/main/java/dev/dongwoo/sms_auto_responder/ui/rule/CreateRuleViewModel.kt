package dev.dongwoo.sms_auto_responder.ui.rule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dongwoo.sms_auto_responder.data.dao.RuleWithDetails
import dev.dongwoo.sms_auto_responder.data.repository.RuleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dev.dongwoo.sms_auto_responder.ui.rule.AppInfo

@HiltViewModel
class CreateRuleViewModel @Inject constructor(
    private val ruleRepository: RuleRepository
) : ViewModel() {

    private val _currentRule = MutableStateFlow<RuleWithDetails?>(null)
    val currentRule: StateFlow<RuleWithDetails?> = _currentRule.asStateFlow()

    fun loadRule(ruleId: Int) {
        viewModelScope.launch {
            _currentRule.value = ruleRepository.getRuleById(ruleId)
        }
    }

    fun saveRule(
        apps: List<AppInfo>,
        keywords: List<String>,
        phoneNumber: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            ruleRepository.addRule(
                name = buildRuleName(apps),
                phoneNumber = phoneNumber,
                targetApps = apps.map { it.packageName },
                keywords = keywords
            )
            onSuccess()
        }
    }

    fun updateRule(
        ruleId: Int,
        apps: List<AppInfo>,
        keywords: List<String>,
        phoneNumber: String,
        isEnabled: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            ruleRepository.updateRule(
                ruleId = ruleId,
                name = buildRuleName(apps),
                phoneNumber = phoneNumber,
                targetApps = apps.map { it.packageName },
                keywords = keywords,
                isEnabled = isEnabled
            )
            onSuccess()
        }
    }

    private fun buildRuleName(apps: List<AppInfo>): String {
        if (apps.isEmpty()) return "새 규칙"
        val first = apps.first().name
        return if (apps.size == 1) "$first 알림" else "$first 외 ${apps.size - 1}개 앱 알림"
    }
}
