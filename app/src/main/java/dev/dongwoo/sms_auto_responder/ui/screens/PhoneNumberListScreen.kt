package dev.dongwoo.sms_auto_responder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dongwoo.sms_auto_responder.data.entity.PhoneNumberEntity
import dev.dongwoo.sms_auto_responder.data.repository.AppRepository
import dev.dongwoo.sms_auto_responder.ui.components.AppTopBar
import dev.dongwoo.sms_auto_responder.ui.components.BookingStyleCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneNumberListViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val phoneNumbers = repository.allPhoneNumbers
}

@Composable
fun PhoneNumberListScreen(
    onAddNumber: () -> Unit,
    onEditNumber: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    viewModel: PhoneNumberListViewModel = hiltViewModel()
) {
    val phoneNumbers by viewModel.phoneNumbers.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Recipients",
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Text("History", color = Color.White)
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNumber) {
                Icon(Icons.Filled.Add, contentDescription = "Add Number")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(phoneNumbers) { phoneNumber ->
                PhoneNumberItem(
                    phoneNumber = phoneNumber,
                    onClick = { onEditNumber(phoneNumber.id) }
                )
            }
        }
    }
}

@Composable
fun PhoneNumberItem(
    phoneNumber: PhoneNumberEntity,
    onClick: () -> Unit
) {
    BookingStyleCard(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = phoneNumber.phoneNumber,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (phoneNumber.tags.isNotEmpty()) {
                        phoneNumber.tags.split(",").take(3).forEach { tag ->
                            AssistChip(
                                onClick = {},
                                label = { Text(tag.trim()) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (phoneNumber.lastSentTime > 0) "Last sent: ${java.util.Date(phoneNumber.lastSentTime)}" else "No history",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}
