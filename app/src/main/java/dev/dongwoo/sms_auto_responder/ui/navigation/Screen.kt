package dev.dongwoo.sms_auto_responder.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CreateRule : Screen("create_rule")
    object History : Screen("history")
    object Settings : Screen("settings") // Placeholder as per UI buttons
}
