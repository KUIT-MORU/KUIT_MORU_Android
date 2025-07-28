package com.konkuk.moru.presentation.navigation

import FollowScreen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import com.konkuk.moru.presentation.myactivity.screen.ActMainScreen
import com.konkuk.moru.presentation.myactivity.screen.ActProfileScreen
import com.konkuk.moru.presentation.myactivity.screen.ActRecordDetailScreen
import com.konkuk.moru.presentation.myactivity.screen.ActRecordScreen
import com.konkuk.moru.presentation.myactivity.screen.ActScrabScreen
import com.konkuk.moru.presentation.myactivity.screen.ActSettingScreen
import com.konkuk.moru.presentation.myroutines.screen.MyRoutinesScreen
import com.konkuk.moru.presentation.myroutines.screen.MyRoutinesViewModel
import com.konkuk.moru.presentation.routinefeed.screen.NotificationScreen
import com.konkuk.moru.presentation.routinefeed.screen.main.HotRoutineListScreen
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineDetailScreen
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineFeedScreen
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineFeedViewModel
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
    innerPadding: PaddingValues = PaddingValues()
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
                modifier = modifier.padding(innerPadding)
            )
        }

        composable(route = Route.RoutineFocusIntro.route) {
            // ✅ Home의 백스택 엔트리 기준으로 viewModel을 가져온다
            val parentEntry = remember(navController) {
                navController.getBackStackEntry(Route.Home.route)
            }
            val sharedViewModel = viewModel<SharedRoutineViewModel>(parentEntry)

            val startNavigation by sharedViewModel.startNavigation.collectAsState()
            val focusType by sharedViewModel.focusType.collectAsState()

            RoutineFocusIntroScreen(
                focusType = focusType,
                onStartClick = { selectedSteps ->
                    sharedViewModel.setSelectedSteps(selectedSteps) // ⭐ ViewModel에 저장
                    navController.navigate(Route.RoutineFocus.route) // 그냥 라우트만 넘김
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

        // 간편 루틴 실행 화면
        composable(route = Route.RoutineSimpleRun.route) {
            RoutineSimpleRunScreen(
                routineTitle = "주말 아침 루틴",
                hashTag = "#태그 #태그",
                steps = sampleSteps, // 실제로는 전달받은 데이터로
                onFinishClick = { /* 팝업 열기용 */ },
                onFinishConfirm = { /* 종료 로직 */ },
                onDismiss = { navController.popBackStack(Route.Home.route, inclusive = false) }
            )
        }

        // 집중 루틴 실행 화면 (몰입화면)
        composable(route = Route.RoutineFocus.route) {
            val parentEntry = remember(navController) {
                navController.getBackStackEntry(Route.Home.route)
            }
            val sharedViewModel = viewModel<SharedRoutineViewModel>(parentEntry)
            val selectedSteps by sharedViewModel.selectedSteps.collectAsState()

            val focusViewModel: RoutineFocusViewModel = viewModel()


            RoutineFocusScreenContainer(
                viewModel = focusViewModel,
                onDismiss = {
                    navController.popBackStack(Route.Home.route, inclusive = false)
                },
                routineItems = selectedSteps.map { it.name to "${it.duration}m" },
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
                    viewModel.onNotificationViewed() // ViewModel의 함수 호출
                    navController.navigate(Route.Notification.route)
                }
            )
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
                    navController.navigate(Route.RoutineFeedDetail.createRoute(routineId))
                },
                onDismissDeleteSuccessDialog = viewModel::dismissDeleteSuccessDialog
            )
        }


        // [추가] UserProfileScreen 내비게이션 설정
        composable(
            route = Route.UserProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            // userId는 현재 더미 데이터로만 사용되므로 ViewModel에서 직접 로드합니다.
            // 실제 앱에서는 hiltViewModel에 userId를 전달하여 해당 유저 데이터를 불러옵니다.
            UserProfileScreen(navController = navController)
        }

        // [추가] FollowScreen 내비게이션 설정
        composable(
            route = Route.Follow.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("selectedTab") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedTab = backStackEntry.arguments?.getString("selectedTab")
            FollowScreen(
                onBackClick = { navController.popBackStack() },
                selectedTab = selectedTab
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

        composable(
            route = Route.ActRecordDetail.route,
            arguments = listOf(navArgument("routineTitle") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val encodedTitle = backStackEntry.arguments?.getString("routineTitle") ?: ""
            val decodedTitle = URLDecoder.decode(encodedTitle, StandardCharsets.UTF_8.toString())

            ActRecordDetailScreen(title = decodedTitle, navController = navController)
        }
    }
}