package com.konkuk.moru.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import com.konkuk.moru.presentation.home.screen.HomeScreen
import com.konkuk.moru.presentation.home.screen.RoutineFocusIntroScreen
import com.konkuk.moru.presentation.home.screen.RoutineSimpleRunScreen
import com.konkuk.moru.presentation.routinefocus.viewmodel.SharedRoutineViewModel
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
import com.konkuk.moru.presentation.myroutines.viewmodel.MyRoutinesViewModel
import com.konkuk.moru.presentation.routinecreate.screen.RoutineCreateScreen
import com.konkuk.moru.presentation.routinefeed.screen.NotificationScreen
import com.konkuk.moru.presentation.routinefeed.screen.follow.FollowScreen
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineDetailScreen
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineFeedRec
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineFeedScreen
import com.konkuk.moru.presentation.routinefeed.screen.search.RoutineSearchHost
import com.konkuk.moru.presentation.routinefeed.screen.userprofile.UserProfileScreen
import com.konkuk.moru.presentation.routinefeed.viewmodel.RoutineFeedViewModel
import com.konkuk.moru.presentation.routinefocus.screen.RoutineFocusScreenContainer
import com.konkuk.moru.presentation.routinefocus.viewmodel.RoutineFocusViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    innerPadding: PaddingValues = PaddingValues(),
    fabOffsetY: MutableState<Float>,
    todayTabOffsetY: MutableState<Float>,
    onShowOnboarding: () -> Unit,
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
                fabOffsetY = fabOffsetY,
                todayTabOffsetY = todayTabOffsetY,
                onShowOnboarding = onShowOnboarding,
            )
        }

        // 루틴 목록의 카드 클릭 시
        composable(
            route = "routine_focus_intro/{routineId}",
            arguments = listOf(navArgument("routineId") { type = NavType.StringType })
        ) { backStackEntry ->

            val parentEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(Route.Home.route)
            }
            val sharedViewModel = viewModel<SharedRoutineViewModel>(parentEntry)

            RoutineFocusIntroScreen(
                sharedViewModel = sharedViewModel,
                onStartClick = { selectedSteps, title, hashTag ->
                    sharedViewModel.setSelectedSteps(selectedSteps)
                    sharedViewModel.setRoutineTitle(title)
                    sharedViewModel.setRoutineTags(hashTag.split(" ").map { it.removePrefix("#") })
                    navController.navigate("routine_focus")
                },
                onBackClick = { navController.popBackStack() }
            )
        }



        composable(route = Route.RoutineFocusIntro.route) {

            val parentEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(Route.Home.route)
            }
            val sharedViewModel = viewModel<SharedRoutineViewModel>(parentEntry)

            val startNavigation by sharedViewModel.startNavigation.collectAsState()
            val category by sharedViewModel.routineCategory.collectAsState()

            RoutineFocusIntroScreen(
                sharedViewModel = sharedViewModel,
                onStartClick = { selectedSteps, title, hashTag ->
                    // 루틴 데이터 설정
                    sharedViewModel.setSelectedSteps(selectedSteps)
                    sharedViewModel.setRoutineTitle(title)
                    sharedViewModel.setRoutineTags(hashTag.split(" ").map { it.removePrefix("#") })

                    // 실행 화면 이동
                    if (category == "집중") {
                        navController.navigate(Route.RoutineFocus.route)
                    } else {
                        navController.navigate(Route.RoutineSimpleRun.route)
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )

            LaunchedEffect(startNavigation) {
                when (startNavigation) {
                    "집중" -> {
                        navController.navigate(Route.RoutineFocus.route)
                        sharedViewModel.onNavigationHandled()
                    }

                    "간편" -> {
                        navController.navigate(Route.RoutineSimpleRun.route)
                        sharedViewModel.onNavigationHandled()
                    }

                    else -> Unit
                }
            }
        }



        composable(route = Route.RoutineSimpleRun.route) {
            val parentEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(Route.Home.route)
            }
            val sharedViewModel = viewModel<SharedRoutineViewModel>(parentEntry)

            RoutineSimpleRunScreen(
                sharedViewModel = sharedViewModel,
                onDismiss = {
                    navController.popBackStack(
                        Route.Home.route,
                        inclusive = false
                    )
                    if (navController.currentDestination?.route != Route.Home.route) {
                        navController.navigate(Route.Home.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }


        composable(route = Route.RoutineFocus.route) {
            val routineFocusViewModel: RoutineFocusViewModel = viewModel()

            // Home NavGraph의 ViewModel을 공유
            val parentEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(Route.Home.route)
            }
            val sharedViewModel = viewModel<SharedRoutineViewModel>(parentEntry)

            RoutineFocusScreenContainer(
                focusViewModel = routineFocusViewModel,
                sharedViewModel = sharedViewModel,
                onDismiss = {
                    navController.popBackStack(
                        Route.Home.route,
                        inclusive = false
                    )
                    if (navController.currentDestination?.route != Route.Home.route) {
                        navController.navigate(Route.Home.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }


        composable(route = Route.RoutineFeed.route) {
            /*val viewModel: RoutineFeedViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()*/

            RoutineFeedScreen(
                modifier = modifier.padding(innerPadding),
                navController = navController,
                /*uiState = uiState,
                onNotificationClick = {
                    viewModel.onNotificationViewed() // ViewModel의 함수 호출
                    navController.navigate(Route.Notification.route)
                }*/
            )
        }

        composable(route = Route.RoutineSearch.route) {
            RoutineSearchHost(navController = navController)
        }

        composable(
            route = Route.RoutineFeedDetail.route,
            arguments = listOf(navArgument("routineId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId")
            if (routineId != null) {
                RoutineDetailScreen(
                    routineId = routineId,
                    onBackClick = { navController.popBackStack() },
                    navController = navController
                )
            } else {
                navController.popBackStack()
            }
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
                title == "이 루틴과 비슷한 루틴" -> {
                    feedRoutines.filter { it.tags.contains("운동") || it.tags.contains("명상") }
                }

                else -> emptyList()
            }

            RoutineFeedRec(
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

            MyRoutinesScreen(
                modifier = modifier.padding(innerPadding),
                viewModel = viewModel, // ViewModel 인스턴스만 전달
                onNavigateToRoutineFeed = {
                    navController.navigate(Route.RoutineFeed.route)
                },
                onNavigateToDetail = { routineId ->
                    navController.navigate(Route.MyRoutineDetail.createRoute(routineId))
                },
                onNavigateToCreateRoutine = {
                    navController.navigate(Route.RoutineCreate.route) // 루틴 생성 화면으로 이동
                }
            )
        }

        // [추가] UserProfileScreen 내비게이션 설정
        composable(
            route = Route.MyRoutineDetail.route,
            arguments = listOf(navArgument("routineId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId")
            if (routineId != null) {
                MyRoutineDetailScreen(
                    routineId = routineId,
                    // navController = navController,
                    onBackClick = { navController.popBackStack() },
                )
            } else {
                navController.popBackStack()
            }
        }


        composable(
            route = Route.UserProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            // userId는 현재 더미 데이터로만 사용되므로 ViewModel에서 직접 로드합니다.
            // 실제 앱에서는 hiltViewModel에 userId를 전달하여 해당 유저 데이터를 불러옵니다.
            UserProfileScreen(navController = navController)
        }

        // [추가] FollowScreen 내비게이션 설정
        composable(
            route = Route.Follow.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("selectedTab") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedTab = backStackEntry.arguments?.getString("selectedTab")
            FollowScreen(
                onBackClick = { navController.popBackStack() },
                selectedTab = selectedTab,
                onUserClick = { userId ->
                    navController.navigate(Route.UserProfile.createRoute(userId))
                },
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

        // 루틴 생성 화면
        composable(route = Route.RoutineCreate.route) {
            RoutineCreateScreen(navController)
        }
    }
}