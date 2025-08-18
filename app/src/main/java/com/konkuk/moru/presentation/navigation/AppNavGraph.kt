package com.konkuk.moru.presentation.navigation

import android.content.Context
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Scaffold
import android.util.Log
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
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.presentation.onboarding.OnboardingScreen
import com.konkuk.moru.presentation.routinefocus.component.ScreenBlockOverlay
import com.konkuk.moru.presentation.routinefocus.viewmodel.RoutineFocusViewModel
import com.konkuk.moru.presentation.signup.SignUpScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    routineFocusViewModel: RoutineFocusViewModel? = null
) {
    val startDestination = Route.AuthCheck.route

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("prefs", Context.MODE_PRIVATE) }

    val fabOffsetY = remember { mutableStateOf(0f) }
    val todayTabOffsetY = remember { mutableStateOf(0f) }
    val bottomIconCenters = remember { mutableStateListOf<Offset>() }

    // 홈 온보딩 상태 관리
    var hasShownHomeOnboardingThisSession by remember { mutableStateOf(false) }
    var showHomeOnboarding by remember { mutableStateOf(false) }
    var hasInitializedHomeOnboarding by remember { mutableStateOf(false) }

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
            // 홈 탭 전용 NavController
            val navControllerForTabs = rememberNavController()
            val navBackStackEntry by navControllerForTabs.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Route.Main.route 최초 진입 시에만 온보딩 체크
            LaunchedEffect(Unit) {
                if (!hasInitializedHomeOnboarding) {
                    // SharedPreferences에서 홈 온보딩을 본 적이 있는지 확인
                    val hasSeenHomeOnboarding =
                        sharedPreferences.getBoolean("hasSeenHomeOnboarding", false)

                    println("DEBUG: hasSeenHomeOnboarding = $hasSeenHomeOnboarding")
                    println("DEBUG: hasShownHomeOnboardingThisSession = $hasShownHomeOnboardingThisSession")

                    // 한 번도 본 적이 없고, 이번 세션에서도 아직 보여주지 않았다면 표시
                    if (!hasSeenHomeOnboarding && !hasShownHomeOnboardingThisSession) {
                        println("DEBUG: Setting showHomeOnboarding = true")
                        showHomeOnboarding = true
                    } else {
                        println("DEBUG: Not showing onboarding")
                    }
                    hasInitializedHomeOnboarding = true
                }
            }

            // 홈 탭으로 돌아왔을 때의 처리
            LaunchedEffect(currentRoute) {
                when (currentRoute) {
                    Route.Home.route -> {
                        // 홈 탭 진입 시에는 이미 세션에서 보여준 경우가 아니라면 온보딩 표시 안함
                        // (최초 진입 시에만 위의 LaunchedEffect(Unit)에서 처리)
                    }

                    null -> {
                        // currentRoute가 null일 때는 아무것도 하지 않음 (초기화 중)
                    }

                    else -> {
                        // 다른 탭으로 이동 시 온보딩 숨김
                        if (showHomeOnboarding) {
                            showHomeOnboarding = false

                        }
                    }
                }
            }

            Scaffold(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                contentWindowInsets = WindowInsets(0),
                bottomBar = {
                    if (
                        currentRoute != null &&
                        !listOf(
                            Route.ActSetting.route,
                            Route.ActProfile.route,
                            Route.ActFabTag.route,
                            Route.ActRecord.route,
                            Route.ActScrab.route,
                            Route.ActRecordDetail.route,
                            Route.ActInsightInfo.route,
                            Route.ActPolicy.route,
                            Route.RoutineFocusIntro.route,
                            Route.RoutineFocus.route,
                            Route.RoutineSimpleRun.route,
                            Route.RoutineCreate.route,
                            Route.MyRoutineDetail.route
                        ).any { routePrefix -> currentRoute.startsWith(routePrefix) }
                    ) {
                        MoruBottomBar(
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
                    routineFocusViewModel = routineFocusViewModel
                )
            }
        }
    }

    // 홈 온보딩 화면 표시
    if (showHomeOnboarding) {
        HomeOnboardingScreen(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10f),
            onNextClick = {
                // 홈 온보딩을 봤다고 영구적으로 저장
                sharedPreferences.edit().putBoolean("hasSeenHomeOnboarding", true).apply()
                hasShownHomeOnboardingThisSession = true
                showHomeOnboarding = false
                showOverlay = true
            },
            onCloseClick = {
                // 홈 온보딩을 봤다고 영구적으로 저장
                sharedPreferences.edit().putBoolean("hasSeenHomeOnboarding", true).apply()
                hasShownHomeOnboardingThisSession = true
                showHomeOnboarding = false
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
            onCreateImmediatelyClick = {
                showOverlay = false
                navController.navigate(Route.RoutineCreate.route)
            },
            fabOffsetY = fabOffsetY.value,
            todayTabOffsetY = todayTabOffsetY.value,
            bottomIconCenters = bottomIconCenters
        )
    }
    
    // 화면 차단 오버레이 표시
    routineFocusViewModel?.let { viewModel ->
        Log.d("AppNavGraph", "🛡️ 화면 차단 오버레이 체크: isScreenBlockOverlayVisible=${viewModel.isScreenBlockOverlayVisible}")
        if (viewModel.isScreenBlockOverlayVisible) {
            Log.d("AppNavGraph", "🛡️ ScreenBlockOverlay 표시")
            ScreenBlockOverlay(
                selectedApps = viewModel.selectedApps,
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1000f),
                onDismiss = { viewModel.hideScreenBlockOverlay() }
            )
        }
    }
}