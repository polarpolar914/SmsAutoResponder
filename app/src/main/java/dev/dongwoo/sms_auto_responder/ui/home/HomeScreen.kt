package dev.dongwoo.sms_auto_responder.ui.home

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.dongwoo.sms_auto_responder.data.dao.RuleWithDetails
import dev.dongwoo.sms_auto_responder.ui.component.CustomButton
import dev.dongwoo.sms_auto_responder.ui.component.CustomIconButton
import dev.dongwoo.sms_auto_responder.ui.navigation.Screen
import dev.dongwoo.sms_auto_responder.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val rules by viewModel.rules.collectAsState()
    val successCount by viewModel.successCount.collectAsState()

    // Permission Request Logic
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permission results if needed
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS
        )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    // Check Notification Access
    val isNotificationListenerEnabled = remember(Unit) {
        NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
    }

    if (!isNotificationListenerEnabled) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("알림 접근 권한 필요") },
            text = { Text("앱이 정상적으로 동작하려면 알림 접근 권한이 필요합니다.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        context.startActivity(intent)
                    }
                ) {
                    Text("설정으로 이동")
                }
            },
            containerColor = SurfaceDark,
            titleContentColor = TextHighEmphasisOnDark,
            textContentColor = TextMediumEmphasisOnDark
        )
    }

    Scaffold(
        containerColor = DeepMidnight,
        bottomBar = {
             // Custom Bottom Bar as per spec
             BottomAppBar(
                 containerColor = DeepMidnight,
                 actions = {
                     IconButton(onClick = { /* Stay on Home */ }) {
                         Icon(Icons.Default.Add, contentDescription = "Home", tint = PrimaryAccent)
                     }
                     Spacer(modifier = Modifier.weight(1f))
                     IconButton(onClick = { navController.navigate(Screen.History.route) }) {
                         Icon(Icons.Default.History, contentDescription = "History", tint = TextMediumEmphasisOnDark)
                     }
                 },
                 floatingActionButton = {
                     CustomIconButton(
                         onClick = { navController.navigate(Screen.CreateRule.createRoute()) },
                         modifier = Modifier.size(64.dp)
                     ) {
                         Icon(Icons.Default.Add, contentDescription = "Add Rule", tint = TextHighEmphasisOnDark, modifier = Modifier.size(32.dp))
                     }
                 }
             )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Header Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text("총 발송 횟수", style = Typography.titleMedium, color = TextMediumEmphasisOnDark)
                Text("${successCount} 건", style = Typography.displayLarge, color = TextHighEmphasisOnDark)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickActionButton(icon = Icons.Default.Add, label = "규칙 추가") {
                        navController.navigate(Screen.CreateRule.createRoute())
                    }
                    QuickActionButton(icon = Icons.Default.History, label = "발송 기록") {
                        navController.navigate(Screen.History.route)
                    }
                }
            }

            // Rule List Area
            Column(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(SurfaceLight)
                    .padding(16.dp)
            ) {
                Text(
                    "나의 규칙",
                    style = Typography.titleLarge,
                    color = TextHighEmphasisOnLight,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(rules) { ruleDetails ->
                        RuleItem(
                            ruleDetails = ruleDetails,
                            onClick = { navController.navigate(Screen.CreateRule.createRoute(ruleDetails.rule.ruleId)) },
                            onToggle = { isEnabled ->
                                viewModel.toggleRule(ruleDetails.rule.ruleId, isEnabled)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            colors = IconButtonDefaults.iconButtonColors(containerColor = SurfaceDark)
        ) {
            Icon(icon, contentDescription = label, tint = PrimaryAccent)
        }
        Text(label, style = Typography.labelLarge, color = TextMediumEmphasisOnDark)
    }
}

@Composable
fun RuleItem(ruleDetails: RuleWithDetails, onClick: () -> Unit, onToggle: (Boolean) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(ruleDetails.rule.ruleName, style = Typography.titleMedium, color = TextHighEmphasisOnLight, fontWeight = FontWeight.Bold)
                Text("수신: ${ruleDetails.rule.phoneNumber}", style = Typography.bodyMedium, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (ruleDetails.rule.isEnabled) "활성" else "비활성",
                    color = if (ruleDetails.rule.isEnabled) PrimaryAccent else Color.Gray,
                    style = Typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = ruleDetails.rule.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryAccent,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray
                    )
                )
            }
        }
    }
}
