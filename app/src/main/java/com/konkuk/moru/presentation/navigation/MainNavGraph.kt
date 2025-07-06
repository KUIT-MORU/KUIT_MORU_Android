package com.konkuk.moru.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.konkuk.moru.presentation.home.screen.HomeScreen
import com.konkuk.moru.presentation.myactivity.screen.MyActivityScreen
import com.konkuk.moru.presentation.myroutines.screen.MyRoutinesScreen
import com.konkuk.moru.presentation.routinefeed.screen.NotificationScreen
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineFeedScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home.route
    ) {
        composable(route = Route.Home.route) {
            HomeScreen(
                modifier = modifier.padding(innerPadding)
            )
        }

        composable(route = Route.RoutineFeed.route) {
            RoutineFeedScreen(
                modifier = modifier.padding(innerPadding),
                onNavigateToNotification = {
                    navController.navigate(Route.Notification.route)
                }
            )
        }

        composable(route = Route.MyRoutine.route) {
            MyRoutinesScreen(
                modifier = modifier.padding(innerPadding)
            )
        }

        composable(route = Route.MyActivity.route) {
            MyActivityScreen(
                modifier = modifier.padding(innerPadding)
            )
        }

        composable(route = Route.Notification.route) {
            NotificationScreen(
                // NotificationScreen의 뒤로가기 버튼을 누르면 이전 화면으로 돌아가도록 설정합니다.
                onNavigateBack = {
                    navController.popBackStack()

               }
            )
        }
    }
}