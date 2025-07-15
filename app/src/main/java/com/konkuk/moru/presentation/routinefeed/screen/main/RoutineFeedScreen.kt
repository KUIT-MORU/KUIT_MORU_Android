// 🎯 아래 코드를 복사해서 RoutineFeedScreen.kt 파일 전체에 붙여넣으세요.

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.presentation.routinefeed.component.MoruLiveSection
import com.konkuk.moru.presentation.routinefeed.component.TitledRoutineSection
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.HomeTopAppBar
import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo
import com.konkuk.moru.presentation.routinefeed.data.RoutineSectionModel
// [수정] 통합 Routine 모델을 임포트합니다.
import com.konkuk.moru.data.model.Routine

@Composable
fun RoutineFeedScreen(
    modifier: Modifier = Modifier,
    onNavigateToNotification: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var hasNotification by remember { mutableStateOf(true) }

    val liveUsers = remember {
        List(8) { LiveUserInfo(it, "사용자명", "#운동하자", R.drawable.ic_avatar) }
    }

    // [수정] 샘플 데이터를 통합 Routine 모델로 변경합니다.
    val routineSections = remember {
        listOf(
            RoutineSectionModel(
                title = "지금 가장 핫한 루틴은?",
                routines = listOf(
                    Routine(1, "아침 10분 요가", "간단한 요가로 하루를 시작해요", null, "건강", listOf("건강"), "요가마스터", null, 112, true, false, false),
                    Routine(2, "매일 TIL 작성하기", "개발 지식을 매일 기록합니다", null, "개발", listOf("개발"), "개발왕", null, 98, false, true, false),
                    Routine(3, "점심시간 산책", "식사 후 가벼운 산책", null, "운동", listOf("운동"), "산책러", null, 76, true, false, true),
                    Routine(4, "하루 30분 책읽기", "마음의 양식을 쌓는 시간", null, "독서", listOf("독서"), "북웜", null, 65, false, true, false),
                    Routine(5, "외국어 단어 10개 암기", "꾸준함이 생명", null, "학습", listOf("학습"), "언어천재", null, 51, false, false, false)
                )
            ),
            RoutineSectionModel(
                "MORU님과 딱 맞는 루틴",
                List(5) { Routine(it + 10, "맞춤 루틴", "", null, "독서", listOf("독서"), "MORU", null, 25, false, it % 3 == 0, false) }
            ),
            RoutineSectionModel(
                "#지하철#독서",
                List(5) { Routine(it + 20, "맞춤 루틴", "", null, "독서", listOf("독서"), "지하철독서왕", null, 25, false, it % 3 == 0, false) }
            ),
            RoutineSectionModel(
                "#운동#명상",
                List(5) { Routine(it + 30, "맞춤 루틴", "", null, "운동", listOf("운동", "명상"), "헬창", null, 25, false, it % 3 == 0, false) }
            )
        )
    }

    val likedStates = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            routineSections.flatMap { it.routines }.forEach { put(it.id, it.isLiked) }
        }
    }
    val likeCounts = remember {
        mutableStateMapOf<Int, Int>().apply {
            routineSections.flatMap { it.routines }.forEach { put(it.id, it.likes) }
        }
    }

    Scaffold(
        topBar = {
            HomeTopAppBar(
                searchQuery = searchQuery,
                onQueryChange = { newQuery -> searchQuery = newQuery },
                onSearch = { query -> println("Search triggered for: '$query'") },
                hasNotification = hasNotification,
                onNotificationClick = {
                    onNavigateToNotification()
                    hasNotification = false
                },
                onLogoClick = {}
            )
        }
    ) { paddingValues ->
        RoutineFeedContent(
            modifier = modifier.padding(paddingValues),
            liveUsers = liveUsers,
            routineSections = routineSections.map { section ->
                section.copy(routines = section.routines.map { routine ->
                    routine.copy(isLiked = likedStates[routine.id] ?: routine.isLiked)
                })
            },
            likeCounts = likeCounts,
            onUserClick = { userId -> println("User $userId clicked") },
            onLiveTitleClick = { println("Live title clicked") },
            onRoutineClick = { routineId -> println("Routine $routineId clicked") },
            onMoreClick = { title -> println("More button for '$title' clicked") },
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
    liveUsers: List<LiveUserInfo>,
    routineSections: List<RoutineSectionModel>,
    likeCounts: Map<Int, Int>,
    onUserClick: (Int) -> Unit,
    onLiveTitleClick: () -> Unit,
    onRoutineClick: (Int) -> Unit,
    onMoreClick: (String) -> Unit,
    onLikeClick: (Int, Boolean) -> Unit
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                MoruLiveSection(
                    liveUsers = liveUsers,
                    onUserClick = onUserClick,
                    onTitleClick = onLiveTitleClick
                )
            }
            item {
                Spacer(modifier = Modifier.height(14.dp))
            }
            items(routineSections) { section ->
                TitledRoutineSection(
                    title = section.title,
                    routines = section.routines,
                    likeCounts = likeCounts,
                    onRoutineClick = onRoutineClick,
                    onMoreClick = { onMoreClick(section.title) },
                    onLikeClick = onLikeClick
                )
            }
        }
    }
}

@Preview(name = "라이브 유저 있을 때", showBackground = true)
@Composable
private fun RoutineFeedScreenWithDataPreview() {
    MaterialTheme {
        RoutineFeedScreen(onNavigateToNotification = {})
    }
}

@Preview(name = "라이브 유저 없을 때", showBackground = true)
@Composable
private fun RoutineFeedScreenWithoutLivePreview() {
    // [수정] 프리뷰용 샘플 데이터를 통합 Routine 모델로 변경합니다.
    val routineSections = remember {
        listOf(
            RoutineSectionModel("지금 가장 핫한 루틴은?", List(5) { Routine(it, "루틴명", "", null, "운동", listOf("#운동"), "모루", null, 16, false, false, false) }),
            RoutineSectionModel("MORU님과 딱 맞는 루틴", List(5) { Routine(it + 10, "맞춤 루틴", "", null, "독서", listOf("#독서"), "모루", null, 25, false, false, false) })
        )
    }

    val likedStates = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            routineSections.flatMap { it.routines }.forEach { put(it.id, it.isLiked) }
        }
    }
    val likeCounts = remember {
        mutableStateMapOf<Int, Int>().apply {
            routineSections.flatMap { it.routines }.forEach { put(it.id, it.likes) }
        }
    }

    MaterialTheme {
        RoutineFeedContent(
            liveUsers = emptyList(),
            routineSections = routineSections.map { section ->
                section.copy(routines = section.routines.map { routine ->
                    routine.copy(isLiked = likedStates[routine.id] ?: routine.isLiked)
                })
            },
            likeCounts = likeCounts,
            onUserClick = {},
            onLiveTitleClick = {},
            onRoutineClick = {},
            onMoreClick = {},
            onLikeClick = { routineId, newLikeStatus ->
                likedStates[routineId] = newLikeStatus
                val currentCount = likeCounts[routineId] ?: 0
                likeCounts[routineId] = if (newLikeStatus) currentCount + 1 else currentCount - 1
            }
        )
    }
}