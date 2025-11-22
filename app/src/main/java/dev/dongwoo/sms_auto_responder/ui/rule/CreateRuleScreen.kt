package dev.dongwoo.sms_auto_responder.ui.rule

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
    var selectedApp by remember { mutableStateOf<AppInfo?>(null) }
    var keywordInput by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var messageTemplate by remember { mutableStateOf("") }

    // Fetch installed apps
    val installedApps = remember {
        val pm = context.packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
        packages.filter {
            pm.getLaunchIntentForPackage(it.packageName) != null
        }.map {
            AppInfo(it.applicationInfo.loadLabel(pm).toString(), it.packageName)
        }.sortedBy { it.name }
    }

    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DeepMidnight,
        topBar = {
            TopAppBar(
                title = { Text("새 규칙 만들기", color = TextHighEmphasisOnDark) },
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
                        modifier = Modifier.menuAnchor().fillMaxWidth()
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

                // Keyword Input
                OutlinedTextField(
                    value = keywordInput,
                    onValueChange = { keywordInput = it },
                    label = { Text("포함할 키워드 (엔터로 추가)") }, // Simplification: just comma separated for now or single line
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
                    modifier = Modifier.fillMaxWidth()
                )
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

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = messageTemplate,
                    onValueChange = { messageTemplate = it },
                    label = { Text("보낼 메시지 내용") },
                    placeholder = { Text("템플릿 변수 사용 가능 (예: {{title}})") },
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
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Text(
                    "사용 가능 변수: {{title}}(제목), {{text}}(내용), {{time}}(시간)",
                    style = Typography.bodyMedium,
                    color = TextMediumEmphasisOnDark,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            CustomButton(
                text = "규칙 저장하기",
                onClick = {
                    if (selectedApp != null && phoneNumber.isNotEmpty() && messageTemplate.isNotEmpty()) {
                        val keywords = keywordInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        viewModel.saveRule(
                            appName = selectedApp!!.name,
                            packageName = selectedApp!!.packageName,
                            keywords = keywords,
                            phoneNumber = phoneNumber,
                            message = messageTemplate,
                            onSuccess = { navController.popBackStack() }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
        }
    }
}
