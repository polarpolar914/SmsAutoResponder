package dev.dongwoo.sms_auto_responder.ui.screens

import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dongwoo.sms_auto_responder.data.repository.AppRepository
import dev.dongwoo.sms_auto_responder.ui.components.AppTopBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: android.graphics.drawable.Drawable?
)

@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps = _installedApps.asStateFlow()

    private val _monitoredApps = MutableStateFlow<Set<String>>(emptySet())
    val monitoredApps = _monitoredApps.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    fun loadApps(packageManager: PackageManager) {
        viewModelScope.launch {
            _monitoredApps.value = repository.getMonitoredApps()

            withContext(Dispatchers.IO) {
                val apps = packageManager.getInstalledPackages(0)
                    .mapNotNull { packageInfo ->
                        val appInfo = packageInfo.applicationInfo ?: return@mapNotNull null
                        if ((appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0 || 
                            (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                            AppInfo(
                                name = appInfo.loadLabel(packageManager).toString(),
                                packageName = packageInfo.packageName,
                                icon = appInfo.loadIcon(packageManager)
                            )
                        } else null
                    }
                    .sortedBy { it.name }
                _installedApps.value = apps
                _isLoading.value = false
            }
        }
    }

    fun toggleAppMonitoring(packageName: String, isMonitored: Boolean) {
        val current = _monitoredApps.value.toMutableSet()
        if (isMonitored) {
            current.add(packageName)
        } else {
            current.remove(packageName)
        }
        _monitoredApps.value = current
        repository.setMonitoredApps(current)
    }
}

@Composable
fun AppSelectionScreen(
    onContinue: () -> Unit,
    viewModel: AppSelectionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val installedApps by viewModel.installedApps.collectAsState()
    val monitoredApps by viewModel.monitoredApps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadApps(context.packageManager)
    }

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = { AppTopBar("Select Apps to Monitor") },
        bottomBar = {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Continue")
            }
        }
    ) { paddingValues ->
        if (isLoading) {
             Column(
                 modifier = Modifier.fillMaxSize().padding(paddingValues),
                 verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                 horizontalAlignment = Alignment.CenterHorizontally
             ) {
                 CircularProgressIndicator()
             }
        } else {
            Column(modifier = Modifier.padding(paddingValues)) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Search apps...") }
                )

                LazyColumn {
                    val filteredApps = installedApps.filter {
                        it.name.contains(searchQuery, ignoreCase = true)
                    }

                    items(filteredApps) { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            app.icon?.let {
                                Image(
                                    bitmap = it.toBitmap().asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(app.name)
                                Text(app.packageName, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                            }
                            Switch(
                                checked = monitoredApps.contains(app.packageName),
                                onCheckedChange = { viewModel.toggleAppMonitoring(app.packageName, it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
