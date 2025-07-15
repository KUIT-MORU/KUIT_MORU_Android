package com.konkuk.moru.presentation.routinefeed.screen.main

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.R
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.presentation.routinefeed.component.MoruLiveSection
import com.konkuk.moru.presentation.routinefeed.component.TitledRoutineSection
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.HomeTopAppBar
import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo

data class RoutineFeedSectionModel(
    val title: String,
    val routines: List<Routine>
)

@Composable
fun RoutineFeedScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onNavigateToNotification: () -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }
    var hasNotification by remember { mutableStateOf(true) }

    val liveUsers = remember {
        List(8) { LiveUserInfo(it, "사용자명", "#운동하자", R.drawable.ic_avatar) }
    }

    val routineSections = remember {
        listOf(
            RoutineFeedSectionModel(
                title = "지금 가장 핫한 루틴은?",
                routines = DummyData.dummyRoutines.filter { it.likes > 70 }.take(7)
            ),
            RoutineFeedSectionModel(
                "MORU님과 딱 맞는 루틴",
                routines = DummyData.dummyRoutines.filter { it.authorName == "MORU" }.take(7)
            ),
            RoutineFeedSectionModel(
                "#지하철#독서",
                routines = DummyData.dummyRoutines.filter { it.tags.containsAll(listOf("지하철", "독서")) }.take(7)
            ),
            RoutineFeedSectionModel(
                "#운동#명상",
                routines = DummyData.dummyRoutines.filter { it.tags.containsAll(listOf("운동", "명상")) }.take(7)
            )
        )
    }

    val likedStates = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            DummyData.dummyRoutines.forEach { put(it.id, it.isLiked) }
        }
    }
    val likeCounts = remember {
        mutableStateMapOf<Int, Int>().apply {
            DummyData.dummyRoutines.forEach { put(it.id, it.likes) }
        }
    }

    Scaffold(
        topBar = {
            HomeTopAppBar(
                searchQuery = searchQuery,
                onQueryChange = { newQuery -> searchQuery = newQuery },
                onSearch = { query -> println("Search triggered for: '$query'") },
                hasNotification = hasNotification,
                onNotificationClick = onNavigateToNotification,
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
    likedStates: Map<Int, Boolean>,
    likeCounts: Map<Int, Int>,
    onLikeClick: (Int, Boolean) -> Unit
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                MoruLiveSection(
                    liveUsers = liveUsers,
                    onUserClick = { userId -> println("User $userId clicked") },
                    onTitleClick = { println("Live title clicked") }
                )
            }
            item {
                Spacer(modifier = Modifier.height(14.dp))
            }
            items(routineSections) { section ->
                TitledRoutineSection(
                    title = section.title,
                    routines = section.routines.map { routine ->
                        routine.copy(
                            isLiked = likedStates[routine.id] ?: routine.isLiked
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
        RoutineFeedScreen(navController = rememberNavController())
    }
}