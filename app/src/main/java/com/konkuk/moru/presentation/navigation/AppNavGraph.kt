package com.konkuk.moru.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.core.component.MoruBottomBar
import com.konkuk.moru.presentation.home.component.HomeTutorialOverlayContainer
import com.konkuk.moru.presentation.home.screen.OnboardingScreen
import com.konkuk.moru.R
import com.konkuk.moru.presentation.auth.AuthCheckScreen
import com.konkuk.moru.presentation.login.LoginScreen
import com.konkuk.moru.presentation.onboarding.OnboardingScreen
import com.konkuk.moru.presentation.signup.SignUpScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
) {
    // FAB 위치
    val fabOffsetY = remember { mutableStateOf(0f) }

    // 오늘/이번주 탭 위치
    val todayTabOffsetY = remember { mutableStateOf(0f) }

    val startDestination = Route.Main.route
    val navControllerForTabs = rememberNavController()

    // 🔹 1. 오버레이 상태 외부로 분리
    var showOverlay by remember { mutableStateOf(false) }
    var showOnboarding by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            contentWindowInsets = WindowInsets(0),
            bottomBar = {
                val navBackStackEntry by navControllerForTabs.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

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
                modifier = Modifier.padding(innerPadding),
                onShowOnboarding = { showOnboarding = true },
                onShowOverlay = { showOverlay = true },
                onDismissOverlay = { showOverlay = false },
                fabOffsetY = fabOffsetY,
                todayTabOffsetY = todayTabOffsetY
            )
        }

        // 🔹 2. 실제 오버레이 레이어 (bottomBar 포함해 전부 덮음)
        if (showOnboarding) {
            OnboardingScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(10f),
                onNextClick = {
                    showOnboarding = false
                    showOverlay = true
                },
                onCloseClick = {
                    showOnboarding = false
                    showOverlay = false
                }
            )
        } else if (showOverlay) {
            HomeTutorialOverlayContainer(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(10f),
                onDismiss = {
                    showOverlay = false
                },
                onFabClick = {
                    showOverlay = false
                },
                fabOffsetY = fabOffsetY.value,
                todayTabOffsetY = todayTabOffsetY.value
            )
        }
    }
}