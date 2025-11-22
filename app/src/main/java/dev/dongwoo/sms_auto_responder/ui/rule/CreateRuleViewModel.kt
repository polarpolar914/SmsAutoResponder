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
        appName: String,
        packageName: String,
        keywords: List<String>,
        phoneNumber: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            ruleRepository.addRule(
                name = "$appName 알림",
                phoneNumber = phoneNumber,
                targetApps = listOf(packageName),
                keywords = keywords
            )
            onSuccess()
        }
    }

    fun updateRule(
        ruleId: Int,
        appName: String,
        packageName: String,
        keywords: List<String>,
        phoneNumber: String,
        isEnabled: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            ruleRepository.updateRule(
                ruleId = ruleId,
                name = "$appName 알림",
                phoneNumber = phoneNumber,
                targetApps = listOf(packageName),
                keywords = keywords,
                isEnabled = isEnabled
            )
            onSuccess()
        }
    }
}
