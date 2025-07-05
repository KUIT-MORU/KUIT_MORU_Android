package com.konkuk.moru.presentation.routinefeed.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.konkuk.moru.R
import com.konkuk.moru.presentation.routinefeed.component.MoruLiveSection
import com.konkuk.moru.presentation.routinefeed.component.TitledRoutineSection
import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo
import com.konkuk.moru.presentation.routinefeed.data.RoutineInfo
import com.konkuk.moru.presentation.routinefeed.data.RoutineSectionModel


@Composable
fun RoutineFeedScreen(modifier: Modifier = Modifier) {
    // --- Sample Data (실제로는 ViewModel에서 가져옵니다) ---
    val liveUsers = remember {
        listOf(
            LiveUserInfo(1, "사용자명", "#운동하자", R.drawable.ic_avatar),
            LiveUserInfo(2, "사용자명", "#운동하자", R.drawable.ic_avatar),
            LiveUserInfo(3, "사용자명", "#운동하자", R.drawable.ic_avatar),
            LiveUserInfo(4, "사용자명", "#운동하자", R.drawable.ic_avatar),
            LiveUserInfo(5, "사용자명", "#운동하자", R.drawable.ic_avatar),
        )
    }
    val routineSections = remember {
        listOf(
            RoutineSectionModel(
                title = "지금 가장 핫한 루틴은?",
                routines = List(5) { RoutineInfo(it, "루틴명명명", "운동하자", 16, it % 2 == 0, it % 2 == 0) }
            ),
            RoutineSectionModel(
                title = "MORU님과 딱 맞는 루틴",
                routines = List(5) { RoutineInfo(it + 10, "맞춤 루틴", "독서", 25, false, it % 3 == 0) }
            ),
            RoutineSectionModel(
                title = "#지하철 #독서",
                routines = List(5) { RoutineInfo(it + 20, "지하철 독서", "자기계발", 8, false, false) }
            )
        )
    }
    // --- 좋아요 상태 관리를 위한 임시 State ---
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

    // --------------------------------------------------------

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                MoruLiveSection(
                    liveUsers = liveUsers,
                    onUserClick = { userId -> println("User $userId clicked") },
                    onTitleClick = { println("Live title clicked") }
                )
            }
            items(routineSections) { section ->
                // TitledRoutineSection에 상태를 올바르게 전달
                TitledRoutineSection(
                    title = section.title,
                    // ✅ 이제 각 루틴 카드는 상태 관리 Map에서 최신 데이터를 가져옵니다.
                    routines = section.routines.map { routine ->
                        routine.copy(
                            isLiked = likedStates[routine.id] ?: routine.isLiked,
                            // likeCount를 RoutineInfo의 likes로 직접 전달하지 않습니다.
                            // 대신 상태 관리 Map에서 가져옵니다.
                        )
                    }.onEach { routine ->
                        // RoutineCardWithImage에 전달할 때 likeCount를 상태 Map에서 가져오기 위해
                        // 이 부분은 TitledRoutineSection 내부의 items 루프에서 직접 처리합니다.
                    },
                    likeCounts = likeCounts,
                    onRoutineClick = { routineId -> println("Routine $routineId clicked") },
                    onMoreClick = { println("More button for '${section.title}' clicked") },
                    // ✅ onLikeClick 람다에서 두 가지 상태를 모두 업데이트합니다.
                    onLikeClick = { routineId, newLikeStatus ->
                        println("Routine $routineId like status changed to $newLikeStatus")

                        // isLiked 상태 업데이트
                        likedStates[routineId] = newLikeStatus

                        // likeCount 상태 업데이트
                        val currentCount = likeCounts[routineId] ?: 0
                        likeCounts[routineId] = if (newLikeStatus) currentCount + 1 else currentCount - 1
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RoutineFeedScreenWithDataPreview() {
    MaterialTheme {
        RoutineFeedScreen()
    }
}