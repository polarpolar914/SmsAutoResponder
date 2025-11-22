package dev.dongwoo.sms_auto_responder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import dev.dongwoo.sms_auto_responder.service.KeepAliveForegroundService
import dev.dongwoo.sms_auto_responder.ui.screens.AppSelectionScreen
import dev.dongwoo.sms_auto_responder.ui.screens.HistoryScreen
import dev.dongwoo.sms_auto_responder.ui.screens.PhoneNumberDetailScreen
import dev.dongwoo.sms_auto_responder.ui.screens.PhoneNumberListScreen
import dev.dongwoo.sms_auto_responder.ui.screens.SettingsScreen
import dev.dongwoo.sms_auto_responder.ui.theme.SmsAutoResponderTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permission results
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissions()
        startForegroundService()

        setContent {
            SmsAutoResponderTheme {
                AppNavigation()
            }
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(
             Manifest.permission.SEND_SMS,
             Manifest.permission.READ_CONTACTS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        requestPermissionLauncher.launch(permissions.toTypedArray())

        // Check Notification Listener Permission
        if (!isNotificationServiceEnabled()) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivity(intent)
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val pkgName = packageName
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return flat != null && flat.contains(pkgName)
    }

    private fun startForegroundService() {
        val intent = Intent(this, KeepAliveForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "app_selection") {
        composable("app_selection") {
            AppSelectionScreen(
                onContinue = { navController.navigate("phone_number_list") }
            )
        }
        composable("phone_number_list") {
            PhoneNumberListScreen(
                onAddNumber = { navController.navigate("phone_number_detail/0") },
                onEditNumber = { id -> navController.navigate("phone_number_detail/$id") },
                onSettingsClick = { navController.navigate("settings") },
                onHistoryClick = { navController.navigate("history") }
            )
        }
        composable(
            "phone_number_detail/{phoneNumberId}",
            arguments = listOf(navArgument("phoneNumberId") { type = NavType.IntType })
        ) { backStackEntry ->
            val phoneNumberId = backStackEntry.arguments?.getInt("phoneNumberId") ?: 0
            PhoneNumberDetailScreen(
                phoneNumberId = phoneNumberId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("history") {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
