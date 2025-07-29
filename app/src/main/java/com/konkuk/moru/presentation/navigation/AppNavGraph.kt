package com.konkuk.moru.presentation.navigation

import android.content.Context
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.core.component.MoruBottomBar
import com.konkuk.moru.presentation.auth.AuthCheckScreen
import com.konkuk.moru.presentation.home.component.HomeTutorialOverlayContainer
import com.konkuk.moru.presentation.home.screen.HomeOnboardingScreen
import com.konkuk.moru.presentation.login.LoginScreen
import com.konkuk.moru.presentation.onboarding.OnboardingScreen
import com.konkuk.moru.presentation.signup.SignUpScreen

@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    val startDestination = Route.AuthCheck.route

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("prefs", Context.MODE_PRIVATE) }

    LaunchedEffect(Unit) {
        // 앱을 재시작할 때마다 온보딩을 강제로 띄우고 싶을 경우, 아래 주석을 해제하세요.
        // **주의: 개발 목적으로만 사용하고, 배포 시에는 반드시 제거해야 합니다!**
        sharedPreferences.edit().putBoolean("hasSeenOnboarding", false).apply()
    }

    val fabOffsetY = remember { mutableStateOf(0f) }
    val todayTabOffsetY = remember { mutableStateOf(0f) }
    val bottomIconCenters = remember { mutableStateListOf<Offset>() }

    var showOnboarding by rememberSaveable {
        mutableStateOf(!sharedPreferences.getBoolean("hasSeenOnboarding", false))
    }
    var showOverlay by rememberSaveable { mutableStateOf(false) }

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
                            },
                            onIconMeasured = { idx, _, center ->
                                if (bottomIconCenters.size <= idx) {
                                    repeat(idx - bottomIconCenters.size + 1) {
                                        bottomIconCenters.add(Offset.Zero)
                                    }
                                }
                                bottomIconCenters[idx] = center
                            }
                        )
                    }
                }
            ) { innerPadding ->
                MainNavGraph(
                    navController = navControllerForTabs,
                    modifier = Modifier.padding(innerPadding),
                    onShowOnboarding = { /* 불필요하므로 호출 안 함 */ },
                    fabOffsetY = fabOffsetY,
                    todayTabOffsetY = todayTabOffsetY,
                    bottomIconCenters = bottomIconCenters
                )
            }
        }
    }


    // 온보딩 화면
    if (showOnboarding) {
        HomeOnboardingScreen(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10f),
            onNextClick = {
                sharedPreferences.edit().putBoolean("hasSeenOnboarding", true).apply()
                showOnboarding = false
                showOverlay = true
            },
            onCloseClick = {
                sharedPreferences.edit().putBoolean("hasSeenOnboarding", true).apply()
                showOnboarding = false
                showOverlay = false
            }
        )
    } else if (showOverlay) {
        HomeTutorialOverlayContainer(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10f),
            onDismiss = { showOverlay = false },
            onFabClick = { showOverlay = false },
            fabOffsetY = fabOffsetY.value,
            todayTabOffsetY = todayTabOffsetY.value,
            bottomIconCenters = bottomIconCenters
        )
    }
}

