package dev.dongwoo.sms_auto_responder.ui.rule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dongwoo.sms_auto_responder.data.repository.RuleRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateRuleViewModel @Inject constructor(
    private val ruleRepository: RuleRepository
) : ViewModel() {

    fun saveRule(
        appName: String, // We might just use package name as name for now if user doesn't specify rule name explicitly, or ask for rule name?
        // Spec "규칙 이름" exists in RuleEntity. Spec UI doesn't show "Rule Name" input field!
        // "규칙 이름 (상단, 진한 네이비): 예) '카카오톡 결제 알림'" in Home Screen spec.
        // But Create Screen spec inputs are: App Dropdown, Keyword, Phone, Message.
        // Inference: Rule Name should be auto-generated (e.g., "App Name + Rule") or I missed a field.
        // Let's assume "App Name + Rule" or just "App Name".
        packageName: String,
        keywords: List<String>,
        phoneNumber: String,
        message: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            ruleRepository.addRule(
                name = "$appName 알림", // Auto-generate name
                phoneNumber = phoneNumber,
                msgTemplate = message,
                targetApps = listOf(packageName),
                keywords = keywords
            )
            onSuccess()
        }
    }
}
