package com.konkuk.moru.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.konkuk.moru.presentation.home.screen.HomeScreen
import com.konkuk.moru.presentation.myactivity.screen.ActFabTagScreen
import com.konkuk.moru.presentation.myactivity.screen.ActMainScreen
import com.konkuk.moru.presentation.myactivity.screen.ActProfileScreen
import com.konkuk.moru.presentation.myactivity.screen.ActRecordScreen
import com.konkuk.moru.presentation.myactivity.screen.ActScrabScreen
import com.konkuk.moru.presentation.myactivity.screen.ActSettingScreen
import com.konkuk.moru.presentation.myroutines.screen.MyRoutinesScreen
import com.konkuk.moru.presentation.routinefeed.screen.RoutineFeedScreen

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
                modifier = modifier.padding(innerPadding)
            )
        }

        composable(route = Route.MyRoutine.route) {
            MyRoutinesScreen(
                modifier = modifier.padding(innerPadding)
            )
        }

        composable(route = Route.MyActivity.route) {
            ActMainScreen(
                modifier = modifier.padding(innerPadding),
                navController = navController
            )
        }

        composable(route = Route.ActSetting.route) {
            ActSettingScreen(
                modifier = modifier.padding(innerPadding),
                navController = navController
            )
        }

        composable(route = Route.ActScrab.route) {
            ActScrabScreen(
                modifier = modifier.padding(innerPadding),
                navController = navController
            )
        }

        composable(route = Route.ActFabTag.route){
            ActFabTagScreen(
                modifier = modifier.padding(innerPadding),
                navController = navController
            )
        }

        composable(route = Route.ActRecord.route){
            ActRecordScreen(
                modifier = modifier.padding(innerPadding),
                navController = navController
            )
        }

        composable(route = Route.ActProfile.route){
            ActProfileScreen(
                modifier = modifier.padding(innerPadding),
                navController = navController
            )
        }
    }
}