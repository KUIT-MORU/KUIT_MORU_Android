package com.konkuk.moru.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.R
import com.konkuk.moru.presentation.auth.AuthCheckScreen
import com.konkuk.moru.presentation.login.LoginScreen
import com.konkuk.moru.presentation.onboarding.OnboardingScreen
import com.konkuk.moru.presentation.signup.SignUpScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
) {
    val startDestination = Route.AuthCheck.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Route.AuthCheck.route) {
            AuthCheckScreen(navController)
        }

        composable(Route.Login.route) {
            LoginScreen(navController)
        }

        composable(Route.SignUp.route) {
            SignUpScreen(navController)
        }

        composable(Route.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Route.Main.route) {
                        popUpTo(Route.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Main.route) {
            val navControllerForTabs = rememberNavController()
            val navBackStackEntry by navControllerForTabs.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val bottomNavItems = listOf(
                BottomNavItem(Route.Home.route, R.drawable.ic_home),
                BottomNavItem(Route.RoutineFeed.route, R.drawable.ic_routine_feed),
                BottomNavItem(Route.MyRoutine.route, R.drawable.ic_my_routine),
                BottomNavItem(Route.MyActivity.route, R.drawable.ic_my_activity)
            )

            Scaffold(
                modifier = Modifier.systemBarsPadding(),
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    if (currentRoute !in listOf(
                            Route.ActSetting.route,
                            Route.ActProfile.route,
                            Route.ActFabTag.route,
                            Route.ActRecord.route,
                            Route.ActScrab.route, //navbar 숨기고 싶은 route 추가
                            Route.RoutineSearch.route,
                            Route.MyRoutineDetail.route
                        )
                    ) {
                        NavigationBar(
                            modifier = Modifier.height(41.dp),
                            containerColor = Color.White
                        ) {
                            bottomNavItems.forEach { item ->
                                NavigationBarItem(
                                    selected = currentRoute == item.route,
                                    onClick = {
                                        if (currentRoute != item.route) {
                                            navControllerForTabs.navigate(item.route) {
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = item.icon),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = Color.Black
                                        )
                                    }
                                )
                            }
                        }
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