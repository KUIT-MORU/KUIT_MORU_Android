package com.konkuk.moru.presentation.navigation

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.konkuk.moru.core.component.MoruBottomBar
import com.konkuk.moru.presentation.home.component.HomeTutorialOverlayContainer
import com.konkuk.moru.presentation.home.screen.OnboardingScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("prefs", Context.MODE_PRIVATE) }

    LaunchedEffect(Unit) {
        // 앱을 재시작할 때마다 온보딩을 강제로 띄우고 싶을 경우, 아래 주석을 해제하세요.
        // **주의: 개발 목적으로만 사용하고, 배포 시에는 반드시 제거해야 합니다!**
        sharedPreferences.edit().putBoolean("hasSeenOnboarding", false).apply()
        // Log.d("DEBUG", "hasSeenOnboarding 강제 false 설정됨") // 로그로 확인하고 싶다면 추가
    }

    val fabOffsetY = remember { mutableStateOf(0f) }
    val todayTabOffsetY = remember { mutableStateOf(0f) }
    val bottomIconCenters = remember { mutableStateListOf<Offset>() }

    // ✅ 여기: remember 제거하고 launchEffect로 초기화
    var showOnboarding by rememberSaveable {
        mutableStateOf(!sharedPreferences.getBoolean("hasSeenOnboarding", false))
    }
    var showOverlay by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = Route.Main.route) {
            composable(Route.Main.route) {
                val navControllerForTabs = rememberNavController()
                val navBackStackEntry by navControllerForTabs.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding(),
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
                        onShowOverlay = { showOverlay = true },
                        onDismissOverlay = { showOverlay = false },
                        fabOffsetY = fabOffsetY,
                        todayTabOffsetY = todayTabOffsetY,
                        bottomIconCenters = bottomIconCenters
                    )
                }
            }
        }

        // ✅ 온보딩 화면 → X 또는 다음 눌렀을 때 SharedPreferences와 상태 갱신
        if (showOnboarding) {
            OnboardingScreen(
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
}

