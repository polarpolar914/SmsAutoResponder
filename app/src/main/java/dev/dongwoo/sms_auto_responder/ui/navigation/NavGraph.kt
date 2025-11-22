package dev.dongwoo.sms_auto_responder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.dongwoo.sms_auto_responder.ui.history.HistoryScreen
import dev.dongwoo.sms_auto_responder.ui.home.HomeScreen
import dev.dongwoo.sms_auto_responder.ui.rule.CreateRuleScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(
            route = Screen.CreateRule.route,
            arguments = listOf(navArgument("ruleId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            CreateRuleScreen(navController = navController)
        }
        composable(Screen.History.route) {
            HistoryScreen(navController = navController)
        }
    }
}
