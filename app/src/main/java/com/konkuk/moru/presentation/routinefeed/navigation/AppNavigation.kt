package com.konkuk.moru.presentation.routinefeed.navigation // ◀ 새로운 패키지 경로

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.presentation.routinefeed.screen.NotificationScreen
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineFeedScreen

// 화면 경로를 상수로 관리하면 실수를 줄일 수 있습니다.
object AppRoutes {
    const val ROUTINE_FEED = "routine_feed"
    const val NOTIFICATION = "notification"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppRoutes.ROUTINE_FEED) {
        composable(AppRoutes.ROUTINE_FEED) {
            RoutineFeedScreen(
                onNavigateToNotification = {
                    navController.navigate(AppRoutes.NOTIFICATION)
                }
            )
        }
        composable(AppRoutes.NOTIFICATION) {
            NotificationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}