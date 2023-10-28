package me.paladin.wifi.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.paladin.wifi.ui.settings.SettingsScreen

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = MainDestinations.MAIN_ROUTE,
        modifier = modifier
    ) {
        composable(MainDestinations.MAIN_ROUTE) {
            MainScreen {
                navController.navigate("settings")
            }
        }

        composable("settings") {
            SettingsScreen()
        }
    }
}