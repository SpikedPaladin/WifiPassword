package me.paladin.wifi.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.paladin.wifi.ui.home.HomeScreen
import me.paladin.wifi.ui.tools.ToolsScreen

@Composable
fun BottomBarNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    settingsAction: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = MainDestinations.HOME_ROUTE,
        modifier = modifier
    ) {
        composable(MainDestinations.HOME_ROUTE) {
            HomeScreen(settingsAction = settingsAction)
        }

        composable(MainDestinations.SEARCH_ROUTE) {
            ToolsScreen()
        }
    }
}