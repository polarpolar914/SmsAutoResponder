package dev.dongwoo.sms_auto_responder.ui.rule

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.dongwoo.sms_auto_responder.ui.component.CustomButton
import dev.dongwoo.sms_auto_responder.ui.component.InputCard
import dev.dongwoo.sms_auto_responder.ui.theme.*

data class AppInfo(val name: String, val packageName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRuleScreen(
    navController: NavController,
    viewModel: CreateRuleViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val ruleIdString = backStackEntry?.arguments?.getString("ruleId")
    val ruleId = ruleIdString?.toIntOrNull()
    val isEditMode = ruleId != null

    // State
    var selectedApp by remember { mutableStateOf<AppInfo?>(null) }
    val keywords = remember { mutableStateListOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Load existing rule if in edit mode
    val currentRule by viewModel.currentRule.collectAsState()
    
    LaunchedEffect(ruleId) {
        if (ruleId != null) {
            viewModel.loadRule(ruleId)
        }
    }

    // Populate fields when rule loads
    LaunchedEffect(currentRule) {
        currentRule?.let { ruleDetails ->
            phoneNumber = ruleDetails.rule.phoneNumber
            keywords.clear()
            if (ruleDetails.keywords.isNotEmpty()) {
                keywords.addAll(ruleDetails.keywords.map { it.keyword })
            } else {
                keywords.add("")
            }
            // Find the app
            val packageName = ruleDetails.apps.firstOrNull()?.packageName
            if (packageName != null) {
                val pm = context.packageManager
                try {
                    val appInfo = pm.getApplicationInfo(packageName, 0)
                    selectedApp = AppInfo(appInfo.loadLabel(pm).toString(), packageName)
                } catch (e: Exception) {
                    selectedApp = AppInfo(packageName, packageName)
                }
            }
        }
    }

    // Fetch installed apps
    val installedApps = remember {
        val pm = context.packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
        packages.filter {
            pm.getLaunchIntentForPackage(it.packageName) != null
        }.map {
            AppInfo(it.applicationInfo?.loadLabel(pm).toString(), it.packageName)
        }.sortedBy { it.name }
    }

    Scaffold(
        containerColor = DeepMidnight,
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "규칙 수정하기" else "새 규칙 만들기", color = TextHighEmphasisOnDark) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextHighEmphasisOnDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepMidnight)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: Trigger Source
            InputCard(title = "감시할 앱 및 조건 (From App)") {
                // App Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedApp?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("앱을 선택해주세요") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextHighEmphasisOnDark,
                            unfocusedTextColor = TextHighEmphasisOnDark,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedLabelColor = PrimaryAccent,
                            unfocusedLabelColor = TextMediumEmphasisOnDark,
                            focusedBorderColor = PrimaryAccent,
                            unfocusedBorderColor = TextMediumEmphasisOnDark
                        ),
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        installedApps.forEach { app ->
                            DropdownMenuItem(
                                text = { Text(app.name) },
                                onClick = {
                                    selectedApp = app
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Keyword Inputs with + button
                Text("포함할 키워드", style = Typography.labelLarge, color = TextMediumEmphasisOnDark)
                keywords.forEachIndexed { index, keyword ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = keyword,
                            onValueChange = { keywords[index] = it },
                            placeholder = { Text("예: 결제, 입금") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextHighEmphasisOnDark,
                                unfocusedTextColor = TextHighEmphasisOnDark,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedLabelColor = PrimaryAccent,
                                unfocusedLabelColor = TextMediumEmphasisOnDark,
                                focusedBorderColor = PrimaryAccent,
                                unfocusedBorderColor = TextMediumEmphasisOnDark
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Remove button (only if more than one keyword)
                        if (keywords.size > 1) {
                            IconButton(
                                onClick = { keywords.removeAt(index) },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Remove keyword",
                                    tint = TextMediumEmphasisOnDark
                                )
                            }
                        }
                    }
                }
                
                // Add keyword button
                TextButton(
                    onClick = { keywords.add("") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add keyword", tint = PrimaryAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("키워드 추가", color = PrimaryAccent)
                }
            }

            // Connector
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "To",
                    tint = PrimaryAccent,
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceDark, CircleShape)
                        .padding(8.dp)
                )
            }

            // Card 2: Action Target
            InputCard(title = "문자 발송 정보 (To SMS)") {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { if (it.all { char -> char.isDigit() }) phoneNumber = it },
                    label = { Text("수신 전화번호") },
                    placeholder = { Text("- 없이 숫자만 입력") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextHighEmphasisOnDark,
                        unfocusedTextColor = TextHighEmphasisOnDark,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = PrimaryAccent,
                        unfocusedLabelColor = TextMediumEmphasisOnDark,
                        focusedBorderColor = PrimaryAccent,
                        unfocusedBorderColor = TextMediumEmphasisOnDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "알림 내용이 그대로 SMS로 전송됩니다",
                    style = Typography.bodyMedium,
                    color = TextMediumEmphasisOnDark,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            CustomButton(
                text = if (isEditMode) "규칙 수정하기" else "규칙 저장하기",
                onClick = {
                    if (selectedApp != null && phoneNumber.isNotEmpty()) {
                        val filteredKeywords = keywords.filter { it.isNotBlank() }
                        if (isEditMode && ruleId != null) {
                            viewModel.updateRule(
                                ruleId = ruleId,
                                appName = selectedApp!!.name,
                                packageName = selectedApp!!.packageName,
                                keywords = filteredKeywords,
                                phoneNumber = phoneNumber,
                                isEnabled = currentRule?.rule?.isEnabled ?: true,
                                onSuccess = { navController.popBackStack() }
                            )
                        } else {
                            viewModel.saveRule(
                                appName = selectedApp!!.name,
                                packageName = selectedApp!!.packageName,
                                keywords = filteredKeywords,
                                phoneNumber = phoneNumber,
                                onSuccess = { navController.popBackStack() }
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
        }
    }
}
