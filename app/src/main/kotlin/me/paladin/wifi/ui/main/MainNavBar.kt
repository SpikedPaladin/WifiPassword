package me.paladin.wifi.ui.main

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun MainNavBar(
    navController: NavHostController
) {
    val screens = listOf(
        MainNavBarScreen.Home,
        MainNavBarScreen.Search
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentRoute = currentRoute,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: MainNavBarScreen,
    currentRoute: String?,
    navController: NavHostController
) {
    NavigationBarItem(
        label = {
            Text(text = screen.title)
        },
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = "Navigation icon" // TODO use translated string
            )
        },
        alwaysShowLabel = false,
        selected = currentRoute == screen.route,

        onClick = {
            navController.navigate(screen.route) {
                navController.graph.startDestinationRoute?.let { route ->
                    popUpTo(route) {
                        saveState = true
                    }
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}

sealed class MainNavBarScreen(
    var route: String,
    var title: String,
    var icon: ImageVector
) {
    data object Home : MainNavBarScreen(
        MainDestinations.HOME_ROUTE,
        "Home", // TODO use translated string
        Icons.Default.Home
    )

    data object Search : MainNavBarScreen(
        MainDestinations.SEARCH_ROUTE,
        "Search", // TODO use translated string
        Icons.Default.Search
    )
}

/**
 * Screens used in [MainDestinations]
 */
private object MainScreens {
    const val MAIN_SCREEN = "main"
    const val HOME_SCREEN = "home"
    const val SEARCH_SCREEN = "search"
}

/**
 * Destinations used in the [MainActivity]
 */
object MainDestinations {
    const val MAIN_ROUTE = MainScreens.MAIN_SCREEN
    const val HOME_ROUTE = MainScreens.HOME_SCREEN
    const val SEARCH_ROUTE = MainScreens.SEARCH_SCREEN
}