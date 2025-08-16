package com.konkuk.moru.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.konkuk.moru.presentation.home.screen.HomeScreen
import com.konkuk.moru.presentation.home.screen.RoutineFocusIntroScreen
import com.konkuk.moru.presentation.home.screen.RoutineSimpleRunScreen
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
import com.konkuk.moru.presentation.routinefeed.screen.search.TagSearchScreen
import com.konkuk.moru.presentation.routinefeed.screen.userprofile.UserProfileScreen
import com.konkuk.moru.presentation.routinefeed.viewmodel.RoutineFeedViewModel
import com.konkuk.moru.presentation.routinefocus.screen.RoutineFocusScreenContainer
import com.konkuk.moru.presentation.routinefocus.viewmodel.RoutineFocusViewModel
import com.konkuk.moru.presentation.routinefocus.viewmodel.SharedRoutineViewModel
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
                modifier = Modifier.padding(innerPadding),
                fabOffsetY = fabOffsetY,
                todayTabOffsetY = todayTabOffsetY,
                onShowOnboarding = onShowOnboarding,
            )
        }

//        // 루틴 목록의 카드 클릭 시
//        composable(
//            route = "routine_focus_intro/{routineId}",
//            arguments = listOf(navArgument("routineId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val parent = remember(navController.currentBackStackEntry) {
//                navController.getBackStackEntry(Route.Home.route)
//            }
//            val shared = viewModel<SharedRoutineViewModel>(parent)
//
//            val rid = backStackEntry.arguments?.getInt("routineId")
//            if (rid != null) {
//                shared.setSelectedRoutineId(rid)
//            }
//
//            RoutineFocusIntroScreen(
//                sharedViewModel = shared,
//                onStartClick = { selectedSteps, title, hashTag ->
//                    shared.setSelectedSteps(selectedSteps)
//                    shared.setRoutineTitle(title)
//                    shared.setRoutineTags(hashTag.split(" ").map { it.removePrefix("#") })
//                    navController.navigate("routine_focus")
//                },
//                onBackClick = { navController.popBackStack() }
//            )
//        }


        composable(route = Route.RoutineFocusIntro.route) {

            val parentEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(Route.Home.route)
            }
            val sharedViewModel = viewModel<SharedRoutineViewModel>(parentEntry)

            val startNavigation by sharedViewModel.startNavigation.collectAsState()
            val category by sharedViewModel.routineCategory.collectAsState()

            RoutineFocusIntroScreen(
                sharedViewModel = sharedViewModel,
                onStartClick = { selectedSteps, title, hashTag, category, totalDuration ->
                    Log.d("MainNavGraph", "🚀 RoutineFocusIntroScreen에서 시작하기 버튼 클릭!")
                    Log.d("MainNavGraph", "   - 카테고리: $category")
                    Log.d("MainNavGraph", "   - 선택된 스텝: ${selectedSteps.size}개")
                    Log.d("MainNavGraph", "   - 총 소요시간: ${totalDuration}분")
                    Log.d("MainNavGraph", "   - 제목: $title")
                    Log.d("MainNavGraph", "   - 태그: $hashTag")

                    // 루틴 데이터 설정
                    sharedViewModel.setSelectedSteps(selectedSteps)
                    sharedViewModel.setRoutineTitle(title)
                    sharedViewModel.setRoutineTags(hashTag.split(" ").map { it.removePrefix("#") })
                    sharedViewModel.setRoutineCategory(category)
                    sharedViewModel.setTotalDuration(totalDuration)

                    // 실행 화면 이동
                    if (category == "집중") {
                        Log.d("MainNavGraph", "🎯 집중 루틴으로 이동: RoutineFocus")
                        navController.navigate(Route.RoutineFocus.route)
                    } else {
                        Log.d("MainNavGraph", "🎯 간편 루틴으로 이동: RoutineSimpleRun")
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
            val parent = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(Route.Home.route)
            }
            val shared = viewModel<SharedRoutineViewModel>(parent)
            val currentId by shared.selectedRoutineId.collectAsState()
            val originalRoutineId by shared.originalRoutineId.collectAsState()
            val title by shared.routineTitle.collectAsState()
            val category by shared.routineCategory.collectAsState()
            val totalDuration by shared.totalDuration.collectAsState()
            val steps by shared.selectedSteps.collectAsState()

            Log.d("MainNavGraph", "🚀 RoutineSimpleRun 화면 진입!")
            Log.d("MainNavGraph", "   - routineId: $currentId")
            Log.d("MainNavGraph", "   - originalRoutineId: $originalRoutineId")
            Log.d("MainNavGraph", "   - 제목: $title")
            Log.d("MainNavGraph", "   - 카테고리: $category")
            Log.d("MainNavGraph", "   - 총 소요시간: ${totalDuration}분")
            Log.d("MainNavGraph", "   - 선택된 스텝: ${steps.size}개")

            if (currentId != null) {
                RoutineSimpleRunScreen(
                    sharedViewModel = shared,
                    routineId = currentId!!,
                    onDismiss = {
                        // 홈으로 돌아갈 때 "진행중 루틴" 알림
                        navController.getBackStackEntry(Route.Home.route)
                            .savedStateHandle["runningRoutineId"] = currentId!!


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

                    },
                    onFinishConfirmed = { finishedId: String ->
                        Log.d(
                            "MainNavGraph",
                            "🔄 RoutineSimpleRun 완료 처리: originalRoutineId=$originalRoutineId"
                        )
                        navController.getBackStackEntry(Route.Home.route)
                            .savedStateHandle["finishedRoutineId"] = originalRoutineId ?: finishedId
                        Log.d(
                            "MainNavGraph",
                            "✅ finishedRoutineId 설정 완료: ${originalRoutineId ?: finishedId}"
                        )
                        navController.popBackStack(Route.Home.route, false)
                    }
                )
            }
        }


        composable(route = Route.RoutineFocus.route) {
            val routineFocusViewModel: RoutineFocusViewModel = viewModel()

            // Home NavGraph의 ViewModel을 공유
            val parentEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(Route.Home.route)
            }
            val sharedViewModel = viewModel<SharedRoutineViewModel>(parentEntry)
            val title by sharedViewModel.routineTitle.collectAsState()
            val category by sharedViewModel.routineCategory.collectAsState()
            val totalDuration by sharedViewModel.totalDuration.collectAsState()
            val steps by sharedViewModel.selectedSteps.collectAsState()
            val originalRoutineId by sharedViewModel.originalRoutineId.collectAsState()
            val selectedApps by sharedViewModel.selectedApps.collectAsState()

            Log.d("MainNavGraph", "🚀 RoutineFocus 화면 진입!")
            Log.d("MainNavGraph", "   - 제목: $title")
            Log.d("MainNavGraph", "   - 카테고리: $category")
            Log.d("MainNavGraph", "   - 총 소요시간: ${totalDuration}분")
            Log.d("MainNavGraph", "   - 선택된 스텝: ${steps.size}개")
            Log.d("MainNavGraph", "   - originalRoutineId: $originalRoutineId")
            Log.d("MainNavGraph", "   - 선택된 앱: ${selectedApps.size}개")

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
                },
                onFinishConfirmed = { finishedId: String ->
                    Log.d(
                        "MainNavGraph",
                        "🔄 RoutineFocus 완료 처리: originalRoutineId=$originalRoutineId"
                    )
                    navController.getBackStackEntry(Route.Home.route)
                        .savedStateHandle["finishedRoutineId"] = originalRoutineId ?: finishedId
                    Log.d(
                        "MainNavGraph",
                        "✅ finishedRoutineId 설정 완료: ${originalRoutineId ?: finishedId}"
                    )
                    navController.popBackStack(Route.Home.route, false)
                },
                onScreenBlockTrigger = {
                    // 집중 루틴 실행 중 다른 앱으로 이동 시 화면 차단 팝업창 표시
                    if (category == "집중" && selectedApps.isNotEmpty()) {
                        routineFocusViewModel.showScreenBlockPopup(selectedApps)
                    }
                }
            )
        }


        composable(route = Route.RoutineFeed.route) {
            val viewModel: RoutineFeedViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            RoutineFeedScreen(
                navController = navController,
            )
        }

        composable(route = Route.RoutineSearch.route) {
            // 변경: SearchViewModel 주입
            val vm: com.konkuk.moru.presentation.routinefeed.viewmodel.SearchViewModel =
                androidx.hilt.navigation.compose.hiltViewModel()

            // 변경: Host에 vm 전달
            com.konkuk.moru.presentation.routinefeed.screen.search.RoutineSearchHost(
                navController = navController
            )
        }

        /*composable(
            route = Route.TagSearch.route,
            arguments = listOf(
                navArgument("originalQuery") {
                    type = NavType.StringType
                    defaultValue = "" // 없으면 빈 문자열
                }
            )
        ) { backStackEntry ->
            val originalQuery = backStackEntry.arguments?.getString("originalQuery") ?: ""

            // ⚠️ 기존에 사용 중인 TagSearch 화면을 그대로 호출
            com.konkuk.moru.presentation.routinefeed.screen.search.TagSearchScreen(
                originalQuery = originalQuery,
                onNavigateBack = { navController.popBackStack() },
                onTagSelected = { tagName ->
                    // 선택 결과를 이전 스택(MyRoutineDetail)으로 돌려준다
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("picked_tag_name", tagName) // 이름만 전달
                    navController.popBackStack() // 검색 화면 닫기
                }
            )
        }*/

        composable(
            route = "${Route.TagSearch.route}?originalQuery={originalQuery}",
            arguments = listOf(
                navArgument("originalQuery") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val originalQuery = backStackEntry.arguments?.getString("originalQuery").orEmpty()
            TagSearchScreen(
                originalQuery = originalQuery,
                onNavigateBack = { navController.popBackStack() },
                onTagSelected = { nameOrHash ->
                    val normalized = nameOrHash.removePrefix("#")
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selectedTagsResult", listOf(normalized))
                    navController.popBackStack()
                }
            )
        }


        composable(
            route = Route.RoutineFeedDetail.route,
            arguments = listOf(navArgument("routineId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId")
            if (routineId != null) {
                RoutineDetailScreen(
                    routineId = routineId,
                    onBackClick = { navController.navigateUpOrHome() },
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

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Route.RoutineFeed.route)
            }
            val feedViewModel: RoutineFeedViewModel = hiltViewModel(parentEntry) // ✅ [추가]
            val uiState by feedViewModel.uiState.collectAsState()

            val routinesToShow = remember(uiState, title) {
                uiState.routineSections.firstOrNull { it.title == title }?.routines.orEmpty()
            }



            RoutineFeedRec(
                title = title,
                routines = routinesToShow,
                onBack = { navController.navigateUpOrHome() },
                onRoutineClick = { routineId ->
                    routinesToShow.firstOrNull { it.routineId == routineId }?.let { selected ->
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("selectedRoutineJson", Gson().toJson(selected))
                    }
                    navController.navigate(Route.RoutineFeedDetail.createRoute(routineId))
                }
            )
        }

        composable(route = Route.MyRoutine.route) {
            val viewModel: MyRoutinesViewModel = hiltViewModel()

            MyRoutinesScreen(
                viewModel = viewModel,
                onNavigateToRoutineFeed = { navController.navigate(Route.RoutineFeed.route) },
                onNavigateToDetail = { routineId ->
                    val target = Route.MyRoutineDetail.createRoute(routineId)
                    android.util.Log.d("Nav", "navigate -> $target")
                    navController.navigate(target)
                },
                onNavigateToCreateRoutine = { navController.navigate(Route.RoutineCreate.route) }
            )
        }

        // [추가] UserProfileScreen 내비게이션 설정
        composable(
            route = Route.MyRoutineDetail.route,
            arguments = listOf(navArgument(Route.MyRoutineDetail.KEY) { type = NavType.StringType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString(Route.MyRoutineDetail.KEY)
            if (routineId != null) {
                MyRoutineDetailScreen(
                    routineId = routineId,
                    onBackClick = { navController.popBackStack() },
                    navController = navController
                )
            } else {
                navController.popBackStack()
            }
        }


        composable(
            route = Route.UserProfile.route,
            arguments = listOf(navArgument("userId") {
                type = NavType.StringType
                nullable = true
            })
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
                onBackClick = { navController.navigateUpOrHome() },
                selectedTab = selectedTab,
                onUserClick = { userId ->
                    navController.navigate(Route.UserProfile.createRoute(userId))
                },
                navController = navController
            )
        }

        composable(route = Route.MyActivity.route) {
            ActMainScreen(
                navController = navController
            )
        }

        composable(route = Route.ActSetting.route) {
            ActSettingScreen(
                navController = navController
            )
        }

        composable(route = Route.Notification.route) {
            NotificationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                navController = navController
            )
        }

        composable(route = Route.ActScrab.route) {
            ActScrabScreen(
                navController = navController
            )
        }


        composable(route = Route.ActFabTag.route) {
            ActFabTagScreen(
                navController = navController
            )
        }

        composable(route = Route.ActRecord.route) {
            ActRecordScreen(
                navController = navController
            )
        }

        composable(route = Route.ActProfile.route) {
            ActProfileScreen(
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
                navController = navController
            )
        }

        composable(route = Route.ActInsightInfo.route) {
            ActInsightInfoClickScreen(
                navController = navController
            )
        }

        // 루틴 생성 화면
        composable(route = Route.RoutineCreate.route) {
            RoutineCreateScreen(navController)
        }
    }
}