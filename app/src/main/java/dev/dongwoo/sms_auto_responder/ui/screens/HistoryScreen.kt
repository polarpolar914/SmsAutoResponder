package dev.dongwoo.sms_auto_responder.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dongwoo.sms_auto_responder.data.entity.HistoryEntity
import dev.dongwoo.sms_auto_responder.data.repository.AppRepository
import dev.dongwoo.sms_auto_responder.ui.components.AppTopBar
import dev.dongwoo.sms_auto_responder.ui.components.BookingStyleCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val history = repository.allHistory
}

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val history by viewModel.history.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Sent History",
                canNavigateBack = true,
                navigateUp = onBack
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(history) { item ->
                HistoryItem(item)
            }
        }
    }
}

@Composable
fun HistoryItem(history: HistoryEntity) {
    BookingStyleCard {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text("To: ${history.phoneNumber}", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text("App: ${history.appPackage}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(Modifier.size(4.dp))
            Text("Trigger: ${history.triggeredKeyword}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
            Spacer(Modifier.size(4.dp))
            Text("Message: ${history.messageContent}")
            Spacer(Modifier.size(4.dp))
            val date = Date(history.timestamp)
            val format = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
            Text(format.format(date), style = androidx.compose.material3.MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}
