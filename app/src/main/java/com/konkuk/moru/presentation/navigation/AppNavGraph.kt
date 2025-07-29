package com.konkuk.moru.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.core.component.MoruBottomBar
import com.konkuk.moru.presentation.home.component.HomeTutorialOverlayContainer
import com.konkuk.moru.presentation.home.screen.OnboardingScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    val fabOffsetY = remember { mutableStateOf(0f) }
    val todayTabOffsetY = remember { mutableStateOf(0f) }

    var showOverlay by remember { mutableStateOf(false) }
    var showOnboarding by remember { mutableStateOf(true) }

    // 바텀바에서 받은 좌표
    val bottomIconCenters = remember { mutableStateListOf<Offset>() }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = Route.Main.route) {
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
                                onIconMeasured = { idx, _,center ->
                                    if(bottomIconCenters.size <= idx){
                                        repeat(idx - bottomIconCenters.size + 1){ bottomIconCenters.add(Offset.Zero) }
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
                        onShowOnboarding = { showOnboarding = true },
                        onShowOverlay = { showOverlay = true },
                        onDismissOverlay = { showOverlay = false },
                        fabOffsetY = fabOffsetY,
                        todayTabOffsetY = todayTabOffsetY,
                        bottomIconCenters = bottomIconCenters
                    )
                }
            }
        }

        // 온보딩 → 튜토리얼 오버레이
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
                onDismiss = { showOverlay = false },
                onFabClick = { showOverlay = false },
                fabOffsetY = fabOffsetY.value,
                todayTabOffsetY = todayTabOffsetY.value,
                bottomIconCenters = bottomIconCenters
            )
        }
    }
}
