package com.konkuk.moru.presentation.navigation

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
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

    val fabOffsetY = remember { mutableStateOf(0f) }
    val todayTabOffsetY = remember { mutableStateOf(0f) }
    val bottomIconCenters = remember { mutableStateListOf<Offset>() }

    // 세션별 홈 온보딩 상태 관리
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
                    Log.d("HomeOnboarding", "Initializing home onboarding")

                    // 기존 hasSeenHomeOnboarding 키는 무시하고 새로운 로직 사용
                    val permanentlyDisabled = sharedPreferences.getBoolean("homeOnboardingPermanentlyDisabled", false)

                    Log.d("HomeOnboarding", "permanentlyDisabled = $permanentlyDisabled")
                    Log.d("HomeOnboarding", "hasShownHomeOnboardingThisSession = $hasShownHomeOnboardingThisSession")

                    // 영구적으로 비활성화되지 않았고, 이번 세션에서 아직 보여주지 않았다면 표시
                    if (!permanentlyDisabled && !hasShownHomeOnboardingThisSession) {
                        Log.d("HomeOnboarding", "Setting showHomeOnboarding = true")
                        showHomeOnboarding = true
                    } else {
                        Log.d("HomeOnboarding", "Not showing onboarding")
                    }
                    hasInitializedHomeOnboarding = true
                }
            }

            // 홈 탭으로 돌아왔을 때의 처리
            LaunchedEffect(currentRoute) {
                Log.d("HomeOnboarding", "currentRoute changed to: $currentRoute")
                when (currentRoute) {
                    Route.Home.route -> {
                        Log.d("HomeOnboarding", "Entered Home route, showHomeOnboarding = $showHomeOnboarding")
                        // 홈 탭 진입 시에는 이미 세션에서 보여준 경우가 아니라면 온보딩 표시 안함
                        // (최초 진입 시에만 위의 LaunchedEffect(Unit)에서 처리)
                    }
                    null -> {
                        // currentRoute가 null일 때는 아무것도 하지 않음 (초기화 중)
                        Log.d("HomeOnboarding", "currentRoute is null (initializing)")
                    }
                    else -> {
                        // 다른 탭으로 이동 시 온보딩 숨김
                        if (showHomeOnboarding) {
                            Log.d("HomeOnboarding", "Hiding onboarding due to route change to: $currentRoute")
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

    Log.d("HomeOnboarding", "Composing AppNavGraph, showHomeOnboarding = $showHomeOnboarding, showOverlay = $showOverlay")

    // 홈 온보딩 화면 표시
    if (showHomeOnboarding) {
        Log.d("HomeOnboarding", "Showing HomeOnboardingScreen")
        HomeOnboardingScreen(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10f),
            onNextClick = {
                Log.d("HomeOnboarding", "onNextClick - hiding onboarding, showing overlay")
                // 세션에서 보여줬다고 표시 (앱 재실행시 다시 보여줌)
                hasShownHomeOnboardingThisSession = true
                showHomeOnboarding = false
                showOverlay = true
            },
            onCloseClick = {
                Log.d("HomeOnboarding", "onCloseClick - permanently disabling onboarding")
                // 닫기를 누르면 영구적으로 비활성화
                sharedPreferences.edit().putBoolean("homeOnboardingPermanentlyDisabled", true).apply()
                hasShownHomeOnboardingThisSession = true
                showHomeOnboarding = false
                showOverlay = false
            }
        )
    } else if (showOverlay) {
        Log.d("HomeOnboarding", "Showing HomeTutorialOverlayContainer")
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
    } else {
        Log.d("HomeOnboarding", "Not showing any overlay (showHomeOnboarding=$showHomeOnboarding, showOverlay=$showOverlay)")
    }
}