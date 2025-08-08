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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.presentation.routinefeed.component.Routine.MoruLiveSection
import com.konkuk.moru.presentation.routinefeed.component.Routine.TitledRoutineSection
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.HomeTopAppBar
import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo
import com.konkuk.moru.presentation.routinefeed.viewmodel.RoutineFeedUiState

data class RoutineFeedSectionModel(
    val title: String,
    val routines: List<Routine>
)

@Composable
fun RoutineFeedScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    uiState: RoutineFeedUiState,
    onNotificationClick: () -> Unit,
) {
    val liveUsers = DummyData.dummyLiveUsers

    val routineSections = remember {
        listOf(
            RoutineFeedSectionModel(
                title = "지금 가장 핫한 루틴은?",
                routines = DummyData.feedRoutines.filter { it.likes > 70 }.take(7)
            ),
            RoutineFeedSectionModel(
                "MORU님과 딱 맞는 루틴",
                routines = DummyData.feedRoutines.filter { it.authorName == "MORU" }.take(7)
            ),
            RoutineFeedSectionModel(
                "#지하철#독서",
                routines = DummyData.feedRoutines.filter {
                    it.tags.containsAll(
                        listOf(
                            "지하철",
                            "독서"
                        )
                    )
                }.take(7)
            ),
            RoutineFeedSectionModel(
                "#운동#명상",
                routines = DummyData.feedRoutines.filter {
                    it.tags.containsAll(
                        listOf(
                            "운동",
                            "명상"
                        )
                    )
                }.take(7)
            )
        )
    }

    val likedStates = remember {
        mutableStateMapOf<String, Boolean>().apply {
            DummyData.feedRoutines.forEach { put(it.routineId, it.isLiked) }
        }
    }
    val likeCounts = remember {
        mutableStateMapOf<String, Int>().apply {
            DummyData.feedRoutines.forEach { put(it.routineId, it.likes) }
        }
    }

    Scaffold(
        topBar = {
            HomeTopAppBar(
                onSearchClick = { navController.navigate(Route.RoutineSearch.route) },
                hasNotification = uiState.hasNotification,
                onNotificationClick = onNotificationClick,
                onLogoClick = {}
            )
        }
    ) { paddingValues ->
        RoutineFeedContent(
            modifier = modifier.padding(paddingValues),
            navController = navController,
            liveUsers = liveUsers,
            routineSections = routineSections,
            likedStates = likedStates,
            likeCounts = likeCounts,
            onLikeClick = { routineId, newLikeStatus ->
                likedStates[routineId] = newLikeStatus
                val currentCount = likeCounts[routineId] ?: 0
                likeCounts[routineId] = if (newLikeStatus) currentCount + 1 else currentCount - 1
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
    likedStates: Map<String, Boolean>,
    likeCounts: Map<String, Int>,
    onLikeClick: (String, Boolean) -> Unit
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
                    routines = section.routines.map { routine ->
                        routine.copy(
                            isLiked = likedStates[routine.routineId] ?: routine.isLiked
                        )
                    },
                    likeCounts = likeCounts,
                    onRoutineClick = { routineId ->
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
    MaterialTheme {
        RoutineFeedScreen(
            navController = rememberNavController(),
            uiState = RoutineFeedUiState(hasNotification = true),
            onNotificationClick = {}
        )
    }
}