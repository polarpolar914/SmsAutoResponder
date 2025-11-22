package dev.dongwoo.sms_auto_responder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dongwoo.sms_auto_responder.data.repository.AppRepository
import dev.dongwoo.sms_auto_responder.ui.components.AppTopBar
import dev.dongwoo.sms_auto_responder.ui.components.BookingStyleCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _globalTemplate = MutableStateFlow("")
    val globalTemplate = _globalTemplate.asStateFlow()

    private val _nightMode = MutableStateFlow(false)
    val nightMode = _nightMode.asStateFlow()

    fun load() {
        _globalTemplate.value = repository.getGlobalTemplate()
        _nightMode.value = repository.isNightMode()
    }

    fun saveGlobalTemplate(template: String) {
        _globalTemplate.value = template
        repository.setGlobalTemplate(template)
    }

    fun setNightMode(enabled: Boolean) {
        _nightMode.value = enabled
        repository.setNightMode(enabled)
    }
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.load()
    }
    val globalTemplate by viewModel.globalTemplate.collectAsState()
    val nightMode by viewModel.nightMode.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar("Settings", canNavigateBack = true, navigateUp = onBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            BookingStyleCard {
                Column(Modifier.padding(16.dp)) {
                    Text("Global Template", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = globalTemplate,
                        onValueChange = { viewModel.saveGlobalTemplate(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Default SMS Message") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Used when no specific template is set for a recipient.", style = MaterialTheme.typography.bodySmall)
                }
            }

            BookingStyleCard {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Night Mode", style = MaterialTheme.typography.titleMedium)
                        Text("Disable sending during night", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(
                        checked = nightMode,
                        onCheckedChange = { viewModel.setNightMode(it) }
                    )
                }
            }

             BookingStyleCard {
                Column(Modifier.padding(16.dp)) {
                    Text("Backup & Restore", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                         Button(onClick = { /* TODO */ }) { Text("Backup") }
                         Spacer(Modifier.width(16.dp))
                         Button(onClick = { /* TODO */ }) { Text("Restore") }
                    }
                }
            }
        }
    }
}
