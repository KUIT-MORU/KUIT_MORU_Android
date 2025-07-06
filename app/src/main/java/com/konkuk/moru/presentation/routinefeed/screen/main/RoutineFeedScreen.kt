package com.konkuk.moru.presentation.routinefeed.screen.main

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.presentation.routinefeed.component.MoruLiveSection
import com.konkuk.moru.presentation.routinefeed.component.TitledRoutineSection
import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo
import com.konkuk.moru.presentation.routinefeed.data.RoutineInfo
import com.konkuk.moru.presentation.routinefeed.data.RoutineSectionModel


/**
 * 실제 앱에서 호출될 메인 스크린.
 * ViewModel로부터 데이터를 받아 RoutineFeedContent에 전달하는 역할을 하게 됩니다.
 * 현재는 샘플 데이터를 생성합니다.
 */
@Composable
fun RoutineFeedScreen(modifier: Modifier = Modifier) {
    // --- Sample Data ---
    val liveUsers = remember {
        listOf(
            LiveUserInfo(1, "사용자명", "#운동하자", R.drawable.ic_avatar),
            LiveUserInfo(2, "사용자명", "#운동하자", R.drawable.ic_avatar),
            LiveUserInfo(3, "사용자명", "#운동하자", R.drawable.ic_avatar),
            LiveUserInfo(3, "사용자명", "#운동하자", R.drawable.ic_avatar),
            LiveUserInfo(3, "사용자명", "#운동하자", R.drawable.ic_avatar),
            LiveUserInfo(3, "사용자명", "#운동하자", R.drawable.ic_avatar),
            LiveUserInfo(3, "사용자명", "#운동하자", R.drawable.ic_avatar),
            LiveUserInfo(3, "사용자명", "#운동하자", R.drawable.ic_avatar),
        )
    }
    val routineSections = remember {
        listOf(
            RoutineSectionModel(
                title = "지금 가장 핫한 루틴은?",
                routines = listOf(
                    RoutineInfo(1, "아침 10분 요가", "건강", 112, false, true),
                    RoutineInfo(2, "매일 TIL 작성하기", "개발", 98, false, false),
                    RoutineInfo(3, "점심시간 산책", "운동", 76, true, false),
                    RoutineInfo(4, "하루 30분 책읽기", "독서", 65, false, true),
                    RoutineInfo(5, "외국어 단어 10개", "학습", 51, false, false)
                )
            ),
            RoutineSectionModel(
                "MORU님과 딱 맞는 루틴",
                List(5) { RoutineInfo(it + 10, "맞춤 루틴", "독서", 25, false, it % 3 == 0) }
            ),
            RoutineSectionModel(
                "#지하철#독서",
                List(5) { RoutineInfo(it + 20, "맞춤 루틴", "독서", 25, false, it % 3 == 0) }
            ),
            RoutineSectionModel(
                "#운동#명상",
                List(5) { RoutineInfo(it + 30, "맞춤 루틴", "독서", 25, false, it % 3 == 0) }
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

    RoutineFeedContent(
        modifier = modifier,
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

/**
 * UI의 실제 내용을 구성하는 stateless 컴포저블.
 * 데이터를 파라미터로 받아 화면을 그리기만 합니다.
 */
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
        RoutineFeedScreen()
    }
}

@Preview(name = "라이브 유저 없을 때", showBackground = true)
@Composable
private fun RoutineFeedScreenWithoutLivePreview() {
    val routineSections = remember {
        listOf(
            RoutineSectionModel("지금 가장 핫한 루틴은?", List(5) { RoutineInfo(it, "루틴명", "#운동", 16, false, false) }),
            RoutineSectionModel("MORU님과 딱 맞는 루틴", List(5) { RoutineInfo(it + 10, "맞춤 루틴", "#독서", 25, false, false) })
        )
    }

    // ✅ 1. 프리뷰에서도 상태를 관리할 변수들을 만듭니다.
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
            // ✅ 2. isLiked 상태가 UI에 반영되도록 routines 리스트를 map으로 가공합니다.
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
            // ✅ 3. onLikeClick에 실제 상태 업데이트 로직을 채워줍니다.
            onLikeClick = { routineId, newLikeStatus ->
                likedStates[routineId] = newLikeStatus
                val currentCount = likeCounts[routineId] ?: 0
                likeCounts[routineId] = if (newLikeStatus) currentCount + 1 else currentCount - 1
            }
        )
    }
}