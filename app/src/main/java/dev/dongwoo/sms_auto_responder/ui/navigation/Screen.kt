package dev.dongwoo.sms_auto_responder.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CreateRule : Screen("create_rule?ruleId={ruleId}") {
        fun createRoute(ruleId: Int? = null): String {
            return if (ruleId != null) "create_rule?ruleId=$ruleId" else "create_rule"
        }
    }
    object History : Screen("history")
    object Settings : Screen("settings")
}
