package com.konkuk.moru.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.konkuk.moru.data.model.DummyData.feedRoutines
import com.konkuk.moru.presentation.home.FocusType
import com.konkuk.moru.presentation.home.screen.HomeScreen
import com.konkuk.moru.presentation.home.screen.RoutineFocusIntroScreen
import com.konkuk.moru.presentation.home.screen.RoutineSimpleRunScreen
import com.konkuk.moru.presentation.home.screen.sampleSteps
import com.konkuk.moru.presentation.home.viewmodel.SharedRoutineViewModel
import com.konkuk.moru.presentation.myactivity.screen.ActFabTagScreen
import com.konkuk.moru.presentation.myactivity.screen.ActInsightInfoClickScreen
import com.konkuk.moru.presentation.myactivity.screen.ActMainScreen
import com.konkuk.moru.presentation.myactivity.screen.ActProfileScreen
import com.konkuk.moru.presentation.myactivity.screen.ActRecordDetailScreen
import com.konkuk.moru.presentation.myactivity.screen.ActRecordScreen
import com.konkuk.moru.presentation.myactivity.screen.ActScrabScreen
import com.konkuk.moru.presentation.myactivity.screen.ActSettingScreen
import com.konkuk.moru.presentation.myroutines.screen.MyRoutineDetailScreen
import com.konkuk.moru.presentation.myroutines.screen.MyRoutinesScreen
import com.konkuk.moru.presentation.myroutines.screen.MyRoutinesViewModel
import com.konkuk.moru.presentation.routinefeed.screen.NotificationScreen
import com.konkuk.moru.presentation.routinefeed.screen.main.HotRoutineListScreen
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineDetailScreen
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineFeedScreen
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineFeedViewModel
import com.konkuk.moru.presentation.routinefeed.screen.search.RoutineSearchHost
import com.konkuk.moru.presentation.routinefeed.screen.userprofile.UserProfileScreen
import com.konkuk.moru.presentation.routinefocus.screen.RoutineFocusScreenContainer
import com.konkuk.moru.presentation.routinefocus.viewmodel.RoutineFocusViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.DayOfWeek

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    innerPadding: PaddingValues = PaddingValues(),
    fabOffsetY: MutableState<Float>,
    todayTabOffsetY: MutableState<Float>,
    onShowOnboarding: () -> Unit,
    onShowOverlay: () -> Unit,
    onDismissOverlay: () -> Unit,
    bottomIconCenters: SnapshotStateList<Offset>
) {

    NavHost(
        navController = navController,
        startDestination = Route.Home.route
    ) {
        composable(route = Route.Home.route) {
            val sharedViewModel: SharedRoutineViewModel = viewModel()
            HomeScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                modifier = modifier.padding(innerPadding),
                fabOffsetY = fabOffsetY, // MainNavGraph가 받은 인자를 HomeScreen으로 전달
                todayTabOffsetY = todayTabOffsetY, // MainNavGraph가 받은 인자를 HomeScreen으로 전달
                onShowOnboarding = onShowOnboarding, // MainNavGraph가 받은 인자를 HomeScreen으로 전달
                bottomIconCenters = bottomIconCenters
            )
        }

        composable(route = Route.RoutineFocusIntro.route) {

            val parentEntry = remember(navController) {
                navController.getBackStackEntry(Route.Home.route)
            }
            val sharedViewModel = viewModel<SharedRoutineViewModel>(parentEntry)

            val startNavigation by sharedViewModel.startNavigation.collectAsState()
            val focusType by sharedViewModel.focusType.collectAsState()

            RoutineFocusIntroScreen(
                focusType = focusType,
                onStartClick = {
                    sharedViewModel.onStartClick()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )

            LaunchedEffect(startNavigation) {
                when (startNavigation) {
                    FocusType.FOCUS -> {
                        navController.navigate(Route.RoutineFocus.route)
                        sharedViewModel.onNavigationHandled()
                    }

                    FocusType.SIMPLE -> {
                        navController.navigate(Route.RoutineSimpleRun.route)
                        sharedViewModel.onNavigationHandled()
                    }

                    else -> Unit
                }
            }
        }

        composable(route = Route.RoutineSimpleRun.route) {
            RoutineSimpleRunScreen(
                routineTitle = "주말 아침 루틴",
                hashTag = "#태그 #태그",
                steps = sampleSteps,
                onFinishClick = { /* 팝업 열기용 */ },
                onFinishConfirm = { /* 종료 로직 */ },
                onDismiss = {
                    navController.popBackStack(
                        Route.Home.route,
                        inclusive = false
                    )
                    if (navController.currentDestination?.route != Route.Home.route) {
                        // 스택에 없으면 새로 넣고 나머지는 모두 제거
                        navController.navigate(Route.Home.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(route = Route.RoutineFocus.route) {
            // RoutineFocusViewModel 인스턴스 생성
            val routineFocusViewModel: RoutineFocusViewModel = viewModel()

            RoutineFocusScreenContainer(
                viewModel = routineFocusViewModel, // ViewModel 전달
                onDismiss = {
                    navController.popBackStack(
                        Route.Home.route,
                        inclusive = false
                    )
                    if (navController.currentDestination?.route != Route.Home.route) {
                        // 스택에 없으면 새로 넣고 나머지는 모두 제거
                        navController.navigate(Route.Home.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                routineItems = listOf( // 기존과 동일하게 routineItems 전달
                    "샤워하기" to "15m",
                    "청소하기" to "10m",
                    "밥먹기" to "30m",
                    "옷갈아입기" to "8m"
                )
            )
        }

        composable(route = Route.RoutineFeed.route) {
            val viewModel: RoutineFeedViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            RoutineFeedScreen(
                modifier = modifier.padding(innerPadding),
                navController = navController,
                uiState = uiState,
                onNotificationClick = {
                    viewModel.onNotificationViewed()
                    navController.navigate(Route.Notification.route)
                }
            )
        }

        composable(route = Route.RoutineSearch.route) {
            RoutineSearchHost(navController = navController)
        }

        composable(
            route = Route.RoutineFeedDetail.route,
            arguments = listOf(navArgument("routineId") { type = NavType.IntType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getInt("routineId")
            feedRoutines.find { it.routineId == routineId }?.let { routine ->
                RoutineDetailScreen(
                    routine = routine,
                    onBackClick = { navController.popBackStack() },
                    navController = navController
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
                    feedRoutines.filter { routine ->
                        tags.all { tagToFind ->
                            routine.tags.contains(
                                tagToFind
                            )
                        }
                    }
                }

                title == "지금 가장 핫한 루틴은?" -> feedRoutines.filter { it.likes > 70 }
                title == "MORU님과 딱 맞는 루틴" -> feedRoutines.filter { it.authorName == "MORU" }
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
                    navController.navigate(Route.MyRoutineDetail.createRoute(routineId))
                },
                onDismissDeleteSuccessDialog = viewModel::dismissDeleteSuccessDialog
            )
        }

        composable(
            route = Route.MyRoutineDetail.route,
            arguments = listOf(navArgument("routineId") { type = NavType.IntType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getInt("routineId")
            if (routineId != null) {
                MyRoutineDetailScreen(
                    routineId = routineId,
                    navController = navController,
                    onBackClick = { navController.popBackStack() },
                )
            } else {
                navController.popBackStack()
            }
        }


        composable(
            route = Route.UserProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            UserProfileScreen(navController = navController)
        }

//        composable(
//            route = Route.Follow.route,
//            arguments = listOf(
//                navArgument("userId") { type = NavType.IntType },
//                navArgument("selectedTab") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val selectedTab = backStackEntry.arguments?.getString("selectedTab")
//            FollowScreen(
//                onBackClick = { navController.popBackStack() },
//                selectedTab = selectedTab
//            )
//        }

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

        composable(
            route = Route.ActRecordDetail.route,
            arguments = listOf(navArgument("routineTitle") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val encodedTitle = backStackEntry.arguments?.getString("routineTitle") ?: ""
            val decodedTitle = URLDecoder.decode(encodedTitle, StandardCharsets.UTF_8.toString())

            ActRecordDetailScreen(
                title = decodedTitle,
                navController = navController,
                modifier.padding(innerPadding)
            )
        }

        composable(route = Route.ActInsightInfo.route) {
            ActInsightInfoClickScreen(
                modifier = modifier.padding(innerPadding),
                navController = navController
            )
        }
    }
}