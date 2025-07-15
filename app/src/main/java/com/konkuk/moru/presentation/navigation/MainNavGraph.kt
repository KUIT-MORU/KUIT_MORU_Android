package com.konkuk.moru.presentation.navigation

import DummyData.dummyRoutines
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.konkuk.moru.presentation.home.screen.HomeScreen
import com.konkuk.moru.presentation.myactivity.screen.ActFabTagScreen
import com.konkuk.moru.presentation.myactivity.screen.ActMainScreen
import com.konkuk.moru.presentation.myactivity.screen.ActProfileScreen
import com.konkuk.moru.presentation.myactivity.screen.ActRecordScreen
import com.konkuk.moru.presentation.myactivity.screen.ActScrabScreen
import com.konkuk.moru.presentation.myactivity.screen.ActSettingScreen
import com.konkuk.moru.presentation.myroutines.screen.MyRoutinesScreen
import com.konkuk.moru.presentation.myroutines.screen.MyRoutinesViewModel
import com.konkuk.moru.presentation.routinefeed.screen.NotificationScreen
import com.konkuk.moru.presentation.routinefeed.screen.main.HotRoutineListScreen
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineDetailScreen
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineFeedScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.DayOfWeek

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    innerPadding: PaddingValues = PaddingValues()
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
                navController = navController,
                onNavigateToNotification = {
                    navController.navigate(Route.Notification.route)
                }
            )
        }

        composable(
            route = Route.RoutineFeedDetail.route,
            arguments = listOf(navArgument("routineId") { type = NavType.IntType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getInt("routineId")
            dummyRoutines.find { it.id == routineId }?.let { routine ->
                RoutineDetailScreen(
                    routine = routine,
                    onBackClick = { navController.popBackStack() }
                )
            } ?: navController.popBackStack()
        }

        composable(
            route = Route.RoutineFeedRec.route,
            arguments = listOf(navArgument("title") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedTitle = backStackEntry.arguments?.getString("title") ?: ""
            val title = URLDecoder.decode(encodedTitle, StandardCharsets.UTF_8.toString())

            val routinesToShow = when {
                title.startsWith("#") -> {
                    val tags = title.removePrefix("#").split("#").filter { it.isNotEmpty() }
                    dummyRoutines.filter { routine -> tags.all { tagToFind -> routine.tags.contains(tagToFind) } }
                }
                title == "지금 가장 핫한 루틴은?" -> dummyRoutines.filter { it.likes > 70 }
                title == "MORU님과 딱 맞는 루틴" -> dummyRoutines.filter { it.authorName == "MORU" }
                else -> emptyList()
            }

            HotRoutineListScreen(
                title = title,
                routines = routinesToShow,
                onBack = { navController.popBackStack() },
                onRoutineClick = { routineId ->
                    navController.navigate(Route.RoutineFeedDetail.createRoute(routineId))
                }
            )
        }




        composable(route = Route.MyRoutine.route) {
            val viewModel: MyRoutinesViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()
            val routinesToDisplay by viewModel.routinesToDisplay.collectAsState()

            MyRoutinesScreen(
                modifier = modifier.padding(innerPadding),
                uiState = uiState,
                routinesToDisplay = routinesToDisplay,
                onSortOptionSelected = viewModel::onSortOptionSelected,
                onDaySelected = { day: DayOfWeek? -> viewModel.onDaySelected(day) },
                onTrashClick = viewModel::onTrashClick,
                onCheckRoutine = viewModel::onCheckRoutine,
                onDeleteClick = viewModel::showDeleteDialog,
                onDismissDeleteDialog = viewModel::dismissDeleteDialog,
                onConfirmDelete = viewModel::deleteCheckedRoutines,
                onOpenTimePicker = viewModel::openTimePicker,
                onCloseTimePicker = viewModel::closeTimePicker,
                onConfirmTimeSet = viewModel::onConfirmTimeSet,
                onLikeClick = viewModel::onLikeClick,
                onShowInfoTooltip = viewModel::onShowInfoTooltip,
                onDismissInfoTooltip = viewModel::onDismissInfoTooltip,
                onNavigateToCreateRoutine = { /* TODO: 루틴 생성 화면으로 이동 */ },
                onNavigateToRoutineFeed = {
                    navController.navigate(Route.RoutineFeed.route)
                },
                onNavigateToDetail = { routineId ->
                    navController.navigate(Route.RoutineFeedDetail.createRoute(routineId))
                },
                onDismissDeleteSuccessDialog = viewModel::dismissDeleteSuccessDialog
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

        composable(route = Route.Notification.route) {
            NotificationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Route.ActScrab.route) {
            ActScrabScreen(
                modifier = modifier.padding(innerPadding),
                navController = navController
            )
        }

        composable(route = Route.ActFabTag.route) {
            ActFabTagScreen(
                modifier = modifier.padding(innerPadding),
                navController = navController
            )
        }

        composable(route = Route.ActRecord.route) {
            ActRecordScreen(
                modifier = modifier.padding(innerPadding),
                navController = navController
            )
        }

        composable(route = Route.ActProfile.route) {
            ActProfileScreen(
                modifier = modifier.padding(innerPadding),
                navController = navController
            )
        }
    }
}