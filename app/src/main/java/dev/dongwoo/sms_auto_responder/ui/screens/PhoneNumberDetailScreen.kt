package dev.dongwoo.sms_auto_responder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dongwoo.sms_auto_responder.data.entity.KeywordEntity
import dev.dongwoo.sms_auto_responder.data.entity.PhoneNumberEntity
import dev.dongwoo.sms_auto_responder.data.repository.AppRepository
import dev.dongwoo.sms_auto_responder.ui.components.AppTopBar
import dev.dongwoo.sms_auto_responder.ui.components.BookingStyleCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneNumberDetailViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _currentStep = MutableStateFlow(0)
    val currentStep = _currentStep.asStateFlow()

    private val _phoneNumber = MutableStateFlow(PhoneNumberEntity(phoneNumber = "", tags = "", template = null))
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _keywords = MutableStateFlow<List<KeywordEntity>>(emptyList())
    val keywords = _keywords.asStateFlow()

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps = _installedApps.asStateFlow()

    private var phoneNumberId: Int = 0

    fun load(id: Int, packageManager: android.content.pm.PackageManager) {
        phoneNumberId = id
        viewModelScope.launch {
            if (id != 0) {
                repository.getPhoneNumberById(id)?.let {
                    _phoneNumber.value = it
                }
                repository.getKeywordsForPhoneNumber(id).collect {
                    _keywords.value = it
                }
            }

            // Load apps for keyword specific targeting
            // Reusing logic from AppSelection (could be in repository or shared usecase)
             val apps = packageManager.getInstalledPackages(0)
                .filter { (it.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0 || (it.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 }
                .map {
                    AppInfo(
                        name = it.applicationInfo.loadLabel(packageManager).toString(),
                        packageName = it.packageName,
                        icon = null // Don't need icon here really
                    )
                }
                .sortedBy { it.name }
            _installedApps.value = apps
        }
    }

    fun updatePhoneNumberField(number: String) {
        _phoneNumber.value = _phoneNumber.value.copy(phoneNumber = number)
    }

    fun updateTags(tags: String) {
        _phoneNumber.value = _phoneNumber.value.copy(tags = tags)
    }

    fun updateTemplate(template: String) {
         _phoneNumber.value = _phoneNumber.value.copy(template = template)
    }

    fun addKeyword(keyword: KeywordEntity) {
        val currentList = _keywords.value.toMutableList()
        currentList.add(keyword)
        _keywords.value = currentList
    }

    fun removeKeyword(keyword: KeywordEntity) {
        val currentList = _keywords.value.toMutableList()
        currentList.remove(keyword)
        _keywords.value = currentList
    }

    fun nextStep() {
        if (_currentStep.value < 3) {
            _currentStep.value += 1
        }
    }

    fun prevStep() {
        if (_currentStep.value > 0) {
            _currentStep.value -= 1
        }
    }

    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val id = if (phoneNumberId == 0) {
                 repository.insertPhoneNumber(_phoneNumber.value).toInt()
            } else {
                 repository.updatePhoneNumber(_phoneNumber.value.copy(id = phoneNumberId))
                 phoneNumberId
            }

            // Sync keywords
            repository.deleteKeywordsByPhoneNumberId(id)
            _keywords.value.forEach {
                repository.insertKeyword(it.copy(phoneNumberId = id))
            }
            onSuccess()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberDetailScreen(
    phoneNumberId: Int,
    onBack: () -> Unit,
    viewModel: PhoneNumberDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(phoneNumberId) {
        viewModel.load(phoneNumberId, context.packageManager)
    }

    val currentStep by viewModel.currentStep.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val keywords by viewModel.keywords.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Configure Number (Step ${currentStep + 1}/4)",
                canNavigateBack = true,
                navigateUp = {
                    if (currentStep > 0) viewModel.prevStep() else onBack()
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 0) {
                    TextButton(onClick = { viewModel.prevStep() }) {
                        Text("Back")
                    }
                } else {
                    Spacer(Modifier.width(8.dp))
                }

                Button(
                    onClick = {
                        if (currentStep < 3) {
                            viewModel.nextStep()
                        } else {
                            viewModel.save(onSuccess = onBack)
                        }
                    }
                ) {
                    Text(if (currentStep == 3) "Save & Apply" else "Next")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            when (currentStep) {
                0 -> Step1PhoneNumber(phoneNumber, viewModel)
                1 -> Step2Keywords(keywords, installedApps, viewModel)
                2 -> Step3Template(phoneNumber, viewModel)
                3 -> Step4Summary(phoneNumber, keywords)
            }
        }
    }
}

@Composable
fun Step1PhoneNumber(phoneNumber: PhoneNumberEntity, viewModel: PhoneNumberDetailViewModel) {
    Column {
        Text("Enter Recipient Details", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.size(16.dp))
        OutlinedTextField(
            value = phoneNumber.phoneNumber,
            onValueChange = { viewModel.updatePhoneNumberField(it) },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.size(8.dp))
        OutlinedTextField(
            value = phoneNumber.tags,
            onValueChange = { viewModel.updateTags(it) },
            label = { Text("Tags (comma separated)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.size(8.dp))
        Text(
            "This number will receive automatic SMS replies when notifications match your rules.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2Keywords(
    keywords: List<KeywordEntity>,
    installedApps: List<AppInfo>,
    viewModel: PhoneNumberDetailViewModel
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Column {
        Text("Keyword Rules", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.size(16.dp))

        Button(onClick = { showBottomSheet = true }) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Add Keyword Rule")
        }

        Spacer(Modifier.size(16.dp))

        FlowRow(modifier = Modifier.fillMaxWidth()) {
            keywords.forEach { keyword ->
                InputChip(
                    selected = false,
                    onClick = {},
                    label = {
                        Text(
                            text = (if(keyword.isExclude) "NOT " else "") +
                                   keyword.keyword +
                                   (if(keyword.targetAppPackage != null) " [App]" else "")
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.removeKeyword(keyword) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                        }
                    }
                )
                Spacer(Modifier.width(8.dp))
            }
        }
    }

    if (showBottomSheet) {
        KeywordBottomSheet(
            installedApps = installedApps,
            onDismiss = { showBottomSheet = false },
            onAdd = { keyword ->
                viewModel.addKeyword(keyword)
                showBottomSheet = false
            }
        )
    }
}

// Helper for FlowRow which is experimental or needs accompaniment in older compose versions,
// but in newer compose BOM it's available as ContextualFlowRow or FlowRow.
// For simplicity using a simple Column if FlowRow is tricky without dependency,
// but let's assume standard Column/Row or simple implementation if needed.
// Actually lets just use a LazyColumn for keywords if many, or standard Row with wrap if possible.
// For now, simple Column of Rows for safety.
@Composable
fun FlowRow(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    // Simple vertical stack fallback since FlowRow might need specific version
    Column(modifier) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordBottomSheet(
    installedApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onAdd: (KeywordEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var keywordText by remember { mutableStateOf("") }
    var isRegex by remember { mutableStateOf(false) }
    var isExclude by remember { mutableStateOf(false) }
    var selectedAppPackage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Add Keyword Rule", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.size(16.dp))

            OutlinedTextField(
                value = keywordText,
                onValueChange = { keywordText = it },
                label = { Text("Keyword") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isRegex, onCheckedChange = { isRegex = it })
                Text("Use Regex")
                Spacer(Modifier.width(16.dp))
                Checkbox(checked = isExclude, onCheckedChange = { isExclude = it })
                Text("Exclude Rule (Don't Send)")
            }

            Text("Target App (Optional):", style = MaterialTheme.typography.bodyMedium)
            LazyColumn(modifier = Modifier.fillMaxWidth().size(150.dp)) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp)) {
                        RadioButton(selected = selectedAppPackage == null, onClick = { selectedAppPackage = null })
                        Text("All Apps")
                    }
                }
                items(installedApps) { app ->
                     Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp)) {
                        RadioButton(selected = selectedAppPackage == app.packageName, onClick = { selectedAppPackage = app.packageName })
                        Text(app.name)
                    }
                }
            }

            Button(
                onClick = {
                    if (keywordText.isNotBlank()) {
                        onAdd(
                            KeywordEntity(
                                phoneNumberId = 0, // Placeholder
                                keyword = keywordText,
                                isRegex = isRegex,
                                isExclude = isExclude,
                                targetAppPackage = selectedAppPackage
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add")
            }
            Spacer(Modifier.size(16.dp))
        }
    }
}

@Composable
fun Step3Template(phoneNumber: PhoneNumberEntity, viewModel: PhoneNumberDetailViewModel) {
    var showVariableDialog by remember { mutableStateOf(false) }

    Column {
        Text("Message Template", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.size(16.dp))

        OutlinedTextField(
            value = phoneNumber.template ?: "",
            onValueChange = { viewModel.updateTemplate(it) },
            label = { Text("SMS Content") },
            modifier = Modifier.fillMaxWidth().size(200.dp),
            placeholder = { Text("Leave empty to use Global Template") }
        )

        Spacer(Modifier.size(8.dp))

        Button(onClick = { showVariableDialog = true }) {
            Text("Insert Variable")
        }
    }

    if (showVariableDialog) {
        Dialog(onDismissRequest = { showVariableDialog = false }) {
            BookingStyleCard {
                Column(Modifier.padding(16.dp)) {
                    Text("Select Variable")
                    Spacer(Modifier.size(8.dp))
                    listOf("{{keyword}}", "{{app}}", "{{notification}}", "{{timestamp}}").forEach { v ->
                        TextButton(onClick = {
                            viewModel.updateTemplate((phoneNumber.template ?: "") + v)
                            showVariableDialog = false
                        }) {
                            Text(v)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Step4Summary(phoneNumber: PhoneNumberEntity, keywords: List<KeywordEntity>) {
    Column {
        Text("Summary", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.size(16.dp))

        BookingStyleCard {
            Column(Modifier.padding(16.dp)) {
                Text("Recipient: ${phoneNumber.phoneNumber}")
                Text("Tags: ${phoneNumber.tags}")
                Text("Rules: ${keywords.size} defined")
                Text("Template: ${if(phoneNumber.template.isNullOrEmpty()) "Global Default" else "Custom"}")
            }
        }
    }
}
