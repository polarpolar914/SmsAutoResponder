package dev.dongwoo.sms_auto_responder.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dongwoo.sms_auto_responder.data.dao.RuleWithDetails
import dev.dongwoo.sms_auto_responder.data.repository.HistoryRepository
import dev.dongwoo.sms_auto_responder.data.repository.RuleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ruleRepository: RuleRepository,
    historyRepository: HistoryRepository
) : ViewModel() {

    val rules: StateFlow<List<RuleWithDetails>> = ruleRepository.allRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val successCount: StateFlow<Int> = historyRepository.successCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun toggleRule(ruleId: Int, isEnabled: Boolean) {
        viewModelScope.launch {
            ruleRepository.updateRuleStatus(ruleId, isEnabled)
        }
    }
}
