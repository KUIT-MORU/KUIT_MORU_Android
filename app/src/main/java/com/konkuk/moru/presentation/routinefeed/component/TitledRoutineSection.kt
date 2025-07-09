package com.konkuk.moru.presentation.routinefeed.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.core.component.routine.RoutineCardWithImage
import com.konkuk.moru.presentation.routinefeed.data.RoutineInfo


@Composable
fun TitledRoutineSection(
    modifier: Modifier = Modifier,
    title: String,
    routines: List<RoutineInfo>,
    likeCounts: Map<Int, Int>,
    onRoutineClick: (Int) -> Unit,
    onLikeClick: (Int, Boolean) -> Unit,
    onMoreClick: () -> Unit,
) {
    Column(modifier = modifier.padding(vertical = 12.dp))// 다음 항목 윗부분이랑 합쳐서 24 나오게 했습니다.
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onMoreClick() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "더보기",
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(routines) { routine ->
                // ✅ 제공해주신 RoutineCardWithImage를 직접 사용합니다.
                RoutineCardWithImage(
                    isRunning = routine.isRunning,
                    routineName = routine.name,
                    tags = routine.tags,
                    likeCount = likeCounts[routine.id] ?: routine.likes, // 좋아요 수는 나중에 상태관리 필요
                    isLiked = routine.isLiked,
                    onLikeClick = { onLikeClick(routine.id, !routine.isLiked) },
                    onClick = { onRoutineClick(routine.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true) // showBackground = true : 프리뷰의 배경을 흰색으로 설정
@Composable
fun TitledRoutineSectionPreview() {
    // 1. 프리뷰에 사용할 가상 데이터(샘플) 생성
    val sampleRoutines = listOf(
        RoutineInfo(
            id = 1,
            isRunning = true,
            name = "아침 조깅",
            tags = listOf("#운동"),
            likes = 25,
            isLiked = true
        ),
        RoutineInfo(
            id = 2,
            isRunning = false,
            name = "미라클 모닝",
            tags = listOf("#자기계발"),
            likes = 42,
            isLiked = false
        ),
        RoutineInfo(
            id = 3,
            isRunning = false,
            name = "책 20페이지 읽기",
            tags = listOf("#독서"),
            likes = 18,
            isLiked = true
        ),
        RoutineInfo(
            id = 4,
            isRunning = false,
            name = "물 2L 마시기",
            tags = listOf("#건강"),
            likes = 33,
            isLiked = false
        )
    )

    // 2. 좋아요 수는 상태에 따라 변할 수 있으므로 Map 형태로 관리
    val sampleLikeCounts = mapOf(
        1 to 25,
        2 to 42,
        3 to 18,
        4 to 33
    )

    // 3. 생성한 샘플 데이터를 파라미터로 전달
    TitledRoutineSection(
        modifier = Modifier.fillMaxWidth(),
        title = "요즘 인기있는 루틴 🔥",
        routines = sampleRoutines,
        likeCounts = sampleLikeCounts,
        onRoutineClick = { routineId ->
            // Preview에서는 클릭 동작을 로그로 확인하거나 비워둡니다.
            println("Routine $routineId clicked")
        },
        onLikeClick = { routineId, isLiked ->
            // Preview에서는 클릭 동작을 로그로 확인하거나 비워둡니다.
            println("Routine $routineId liked: $isLiked")
        },
        onMoreClick = {
            // Preview에서는 클릭 동작을 로그로 확인하거나 비워둡니다.
            println("More button clicked")
        }
    )
}