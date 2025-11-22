package dev.dongwoo.sms_auto_responder.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dongwoo.sms_auto_responder.data.entity.HistoryEntity
import dev.dongwoo.sms_auto_responder.data.repository.HistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    historyRepository: HistoryRepository
) : ViewModel() {
    val history: StateFlow<List<HistoryEntity>> = historyRepository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
