package com.konkuk.moru.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.core.component.MoruBottomBar

@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    val startDestination = Route.Main.route

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Route.Main.route) {
            val navControllerForTabs = rememberNavController()
            val navBackStackEntry by navControllerForTabs.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            Scaffold(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                contentWindowInsets = WindowInsets(0),
                bottomBar = {
                    if (currentRoute !in listOf(
                            Route.ActSetting.route,
                            Route.ActProfile.route,
                            Route.ActFabTag.route,
                            Route.ActRecord.route,
                            Route.ActScrab.route,
                            Route.RoutineFocusIntro.route,
                            Route.RoutineFocus.route,
                            Route.RoutineSimpleRun.route
                        )
                    ) {
                        MoruBottomBar(
                            modifier = Modifier.height(80.dp),
                            selectedRoute = currentRoute ?: Route.Home.route,
                            onItemSelected = { route ->
                                if (currentRoute != route) {
                                    navControllerForTabs.navigate(route) {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            ) { innerPadding ->
                MainNavGraph(
                    navController = navControllerForTabs,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}