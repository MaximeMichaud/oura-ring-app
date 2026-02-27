package com.oura.ring.ui.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.oura.ring.ui.screens.activity.ActivityScreen
import com.oura.ring.ui.screens.body.BodyScreen
import com.oura.ring.ui.screens.overview.OverviewScreen
import com.oura.ring.ui.screens.readiness.ReadinessScreen
import com.oura.ring.ui.screens.settings.SettingsScreen
import com.oura.ring.ui.screens.sleep.SleepScreen

data class NavItem(
    val screen: Screen,
    val icon: ImageVector,
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        NavItem(Screen.Overview, Icons.Default.Home),
        NavItem(Screen.Sleep, Icons.Default.Nightlight),
        NavItem(Screen.Readiness, Icons.Default.FavoriteBorder),
        NavItem(Screen.Activity, Icons.AutoMirrored.Filled.DirectionsRun),
        NavItem(Screen.Body, Icons.Default.Person),
        NavItem(Screen.Settings, Icons.Default.Settings),
    )

    fun isSelected(item: NavItem) =
        currentDestination?.hierarchy?.any { it.route == item.screen.route } == true

    fun onNav(item: NavItem) {
        navController.navigate(item.screen.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navHost: @Composable (Modifier) -> Unit = { modifier ->
        NavHost(
            navController = navController,
            startDestination = Screen.Overview.route,
            modifier = modifier,
        ) {
            composable(Screen.Overview.route) { OverviewScreen() }
            composable(Screen.Sleep.route) { SleepScreen() }
            composable(Screen.Readiness.route) { ReadinessScreen() }
            composable(Screen.Activity.route) { ActivityScreen() }
            composable(Screen.Body.route) { BodyScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val useRail = maxWidth > 600.dp

        if (useRail) {
            // Wide screen: side NavigationRail + content
            Row(Modifier.fillMaxSize()) {
                NavigationRail(Modifier.fillMaxHeight()) {
                    items.forEach { item ->
                        NavigationRailItem(
                            icon = { Icon(item.icon, contentDescription = item.screen.title) },
                            label = { Text(item.screen.title) },
                            selected = isSelected(item),
                            onClick = { onNav(item) },
                        )
                    }
                }
                // Constrain content width for readability on ultra-wide screens
                navHost(
                    Modifier
                        .fillMaxSize()
                        .widthIn(max = 900.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        } else {
            // Narrow screen: bottom navigation bar
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        items.forEach { item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.screen.title) },
                                label = { Text(item.screen.title) },
                                selected = isSelected(item),
                                onClick = { onNav(item) },
                            )
                        }
                    }
                },
            ) { padding ->
                navHost(Modifier.padding(padding))
            }
        }
    }
}
