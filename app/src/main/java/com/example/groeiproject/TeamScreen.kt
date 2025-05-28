package com.example.groeiproject

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.groeiproject.model.TeamViewModel
import com.example.groeiproject.ui.settings.SettingsScreen
import com.example.groeiproject.ui.theme.AppTheme


data class RouteItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Router() {
    val navController = rememberNavController()
    val teamVm: TeamViewModel = hiltViewModel()

    val selectedTeamId by teamVm.selectedTeamId.collectAsState()
    val driversRoute = "drivers/$selectedTeamId"

    val tabs = listOf(
        RouteItem("Teams", "teams", Icons.Filled.AccountBox),
        RouteItem("Instellingen", "settings", Icons.Filled.Settings),
        RouteItem("Drivers", driversRoute, Icons.Filled.Person)
    )

    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(getCurrentTitle(navController, tabs)) },
                    actions = {
                        // Optionally also include settings icon here
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    val currentRoute =
                        navController.currentBackStackEntryAsState().value?.destination?.route
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    launchSingleTop = true
                                    popUpTo(navController.graph.startDestinationId) {}
                                }
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "teams",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("teams") {
                    TeamViewer(viewModel = teamVm, navController)
                }
                composable("settings") {
                    SettingsScreen(onBack = { navController.popBackStack() })
                }
                composable(
                    "drivers/{teamId}", arguments = listOf(navArgument("teamId") {
                        type = NavType.IntType; defaultValue = -1
                    })
                ) { backStackEntry ->
                    val teamId = backStackEntry.arguments?.getInt("teamId") ?: -1
                    DriversViewer(teamId)
                }
            }
        }
    }
}

@Composable
private fun getCurrentTitle(
    navController: androidx.navigation.NavHostController,
    tabs: List<RouteItem>
): String {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    return when {
        tabs.any { it.route == currentRoute } -> tabs.first { it.route == currentRoute }.label
        currentRoute?.startsWith("drivers") == true -> "Drivers"
        else -> ""
    }
}
