package com.konkuk.moru.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex // zIndex 임포트 추가
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.R
import com.konkuk.moru.presentation.auth.AuthCheckScreen
import com.konkuk.moru.presentation.home.component.HomeTutorialOverlayContainer // HomeTutorialOverlayContainer 임포트 추가
import com.konkuk.moru.presentation.home.screen.OnboardingScreen // OnboardingScreen 임포트 추가
import com.konkuk.moru.presentation.login.LoginScreen
import com.konkuk.moru.presentation.signup.SignUpScreen
import com.konkuk.moru.ui.theme.MORUTheme.colors

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

            val bottomNavItems = listOf(
                BottomNavItem("홈", Route.Home.route, R.drawable.ic_home, R.drawable.ic_home),
                BottomNavItem("루틴 피드", Route.RoutineFeed.route, R.drawable.ic_routine_feed, R.drawable.ic_routine_feed),
                BottomNavItem("내 루틴", Route.MyRoutine.route, R.drawable.ic_my_routine, R.drawable.ic_my_routine),
                BottomNavItem("내 활동", Route.MyActivity.route, R.drawable.ic_my_activity, R.drawable.ic_my_activity)
            )

            // FAB와 TodayTab의 Y 오프셋 상태를 AppNavGraph에서 관리
            val fabOffsetY = remember { mutableStateOf(0f) }
            val todayTabOffsetY = remember { mutableStateOf(0f) }

            // 온보딩/오버레이 상태를 AppNavGraph에서 관리
            var showOnboarding by remember { mutableStateOf(true) }
            var showOverlay by remember { mutableStateOf(false) }

            Scaffold(
                modifier = Modifier.systemBarsPadding(),
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    if (currentRoute !in listOf(
                            Route.ActSetting.route,
                            Route.ActProfile.route,
                            Route.ActFabTag.route,
                            Route.ActRecord.route,
                            Route.ActScrab.route
                        )
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            color = Color.White,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                bottomNavItems.forEach { item ->
                                    val isSelected = currentRoute == item.route

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = ripple(bounded = false, radius = 24.dp)
                                            ) {
                                                if (currentRoute != item.route) {
                                                    navControllerForTabs.navigate(item.route) {
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(
                                                    id = if (isSelected) item.selectedIconResId else item.iconResId
                                                ),
                                                contentDescription = item.title,
                                                modifier = Modifier.size(20.dp),
                                                tint = if (isSelected) colors.black else colors.lightGray
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = item.title,
                                                fontSize = 9.sp,
                                                color = if (isSelected) colors.black else colors.lightGray,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                // MainNavGraph에 필요한 인자들을 전달
                MainNavGraph(
                    navController = navControllerForTabs,
                    modifier = Modifier.padding(innerPadding),
                    fabOffsetY = fabOffsetY, // 추가
                    todayTabOffsetY = todayTabOffsetY, // 추가
                    onShowOnboarding = { showOnboarding = true }, // 추가
                    onShowOverlay = { showOverlay = true }, // 추가
                    onDismissOverlay = { showOverlay = false } // 추가
                )

                // 온보딩 및 튜토리얼 오버레이 로직
                when {
                    showOnboarding -> {
                        OnboardingScreen(
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(2f), // 다른 UI 위에 오도록 zIndex 조정
                            onNextClick = {
                                showOnboarding = false
                                showOverlay = true
                            },
                            onCloseClick = {
                                showOnboarding = false
                                showOverlay = false
                            }
                        )
                    }

                    showOverlay -> {
                        HomeTutorialOverlayContainer(
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(2f), // 다른 UI 위에 오도록 zIndex 조정
                            onDismiss = {
                                showOverlay = false
                            },
                            onFabClick = {
                                showOverlay = false
                            },
                            fabOffsetY = fabOffsetY.value, // FAB의 실제 Y 오프셋 값 전달
                            todayTabOffsetY = todayTabOffsetY.value // TodayTab의 실제 Y 오프셋 값 전달
                        )
                    }
                }
            }
        }
    }
}