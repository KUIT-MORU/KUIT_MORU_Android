package com.konkuk.moru.presentation.routinefeed.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.presentation.routinefeed.component.Routine.MoruLiveSection
import com.konkuk.moru.presentation.routinefeed.component.Routine.TitledRoutineSection
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.HomeTopAppBar
import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo
import com.konkuk.moru.presentation.routinefeed.viewmodel.MainViewModel
import com.konkuk.moru.presentation.routinefeed.viewmodel.RoutineFeedUiState
import com.konkuk.moru.presentation.routinefeed.viewmodel.RoutineFeedViewModel

data class RoutineFeedSectionModel(
    val title: String,
    val routines: List<Routine>
)

@Composable
fun RoutineFeedScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: RoutineFeedViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val hasNotification by mainViewModel.hasUnreadNotification.collectAsState() // [추가]

    Scaffold(
        containerColor = Color.White,
        topBar = {
            HomeTopAppBar(
                onSearchClick = { navController.navigate(Route.RoutineSearch.route) },
                hasNotification = hasNotification, // [수정]
                onNotificationClick = {
                    mainViewModel.onNotificationIconClicked() // [수정]
                    navController.navigate(Route.Notification.route)
                },
                onLogoClick = {}
            )
        }
    ) { paddingValues ->
        RoutineFeedContent(
            modifier = modifier.padding(paddingValues),
            navController = navController,
            liveUsers = uiState.liveUsers,
            routineSections = uiState.routineSections, // ✅ ViewModel의 데이터를 그대로 사용
            // ✅ onLikeClick 이벤트가 발생하면 ViewModel의 함수를 호출하도록 변경
            onLikeClick = { routineId ->
                viewModel.toggleLike(routineId)
            }
        )
    }
}

@Composable
private fun RoutineFeedContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    liveUsers: List<LiveUserInfo>,
    routineSections: List<RoutineFeedSectionModel>,
    onLikeClick: (String) -> Unit
) {
    Surface(modifier = modifier.fillMaxSize(), color = Color.White) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            item {
                MoruLiveSection(
                    liveUsers = liveUsers,
                    onUserClick = { userId ->
                        navController.navigate(Route.UserProfile.createRoute(userId))
                    },
                    onTitleClick = { println("Live title clicked") }
                )
            }
            item { Spacer(modifier = Modifier.height(14.dp)) }
            items(routineSections) { section ->
                TitledRoutineSection(
                    title = section.title,
                    routines = section.routines,
                    onRoutineClick = { routineId ->
                        section.routines.firstOrNull { it.routineId == routineId }?.let { selected ->
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("selectedRoutineJson", Gson().toJson(selected)) // [수정]
                        }
                        navController.navigate(Route.RoutineFeedDetail.createRoute(routineId))
                    },
                    onMoreClick = { title ->
                        navController.navigate(Route.RoutineFeedRec.createRoute(title))
                    },
                    onLikeClick = onLikeClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoutineFeedScreenPreview() {
    // 1. 미리보기에 사용할 더미 섹션 데이터를 생성합니다.
    val previewSections = listOf(
        RoutineFeedSectionModel(
            title = "지금 가장 핫한 루틴은?",
            routines = DummyData.feedRoutines.filter { it.likes > 100 }.take(5)
        ),
        RoutineFeedSectionModel(
            "MORU님과 딱 맞는 루틴",
            routines = DummyData.feedRoutines.filter { it.authorId == DummyData.MY_USER_ID }.take(5)
        ),
        RoutineFeedSectionModel(
            "#개발 #TIL",
            routines = DummyData.feedRoutines.filter {
                it.tags.containsAll(listOf("개발", "TIL"))
            }.take(5)
        )
    )

    MaterialTheme {
        // 2. 미리보기용 UI State에 생성한 더미 섹션을 포함합니다.
        val dummyUiState = RoutineFeedUiState(
            hasNotification = true,
            liveUsers = DummyData.dummyLiveUsers,
            routineSections = previewSections // 👈 생성한 더미 섹션 할당
        )

        Scaffold(
            containerColor = Color.White,
            topBar = {
                HomeTopAppBar(
                    onSearchClick = { },
                    hasNotification = dummyUiState.hasNotification,
                    onNotificationClick = {
                    },
                    onLogoClick = {}
                )
            }
        ) { paddingValues ->
            RoutineFeedContent(
                modifier = Modifier.padding(paddingValues),
                navController = rememberNavController(),
                liveUsers = dummyUiState.liveUsers,
                routineSections = dummyUiState.routineSections, // 👈 UI State의 섹션 데이터를 전달
                onLikeClick = {}
            )
        }
    }
}