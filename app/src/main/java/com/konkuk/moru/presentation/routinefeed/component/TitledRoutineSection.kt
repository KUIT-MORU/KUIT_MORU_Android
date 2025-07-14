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
// [수정] 통합 Routine 모델을 임포트합니다.
import com.konkuk.moru.data.model.Routine

@Composable
fun TitledRoutineSection(
    modifier: Modifier = Modifier,
    title: String,
    routines: List<Routine>, // [수정] List<RoutineInfo> -> List<Routine>
    likeCounts: Map<Int, Int>,
    onRoutineClick: (Int) -> Unit,
    onLikeClick: (Int, Boolean) -> Unit,
    onMoreClick: () -> Unit,
) {
    Column(modifier = modifier.padding(vertical = 12.dp)) {
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
                RoutineCardWithImage(
                    isRunning = routine.isRunning,
                    routineName = routine.title, // [수정] routine.name -> routine.title
                    tags = routine.tags,
                    likeCount = likeCounts[routine.id] ?: routine.likes,
                    isLiked = routine.isLiked,
                    onLikeClick = { onLikeClick(routine.id, !routine.isLiked) },
                    onClick = { onRoutineClick(routine.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TitledRoutineSectionPreview() {
    // [수정] 프리뷰용 샘플 데이터를 통합 Routine 모델로 변경합니다.
    val sampleRoutines = listOf(
        Routine(
            id = 1, isRunning = true, title = "아침 조깅", tags = listOf("#운동"), likes = 25, isLiked = true,
            description = "", imageUrl = null, category = "운동", authorName = "프리뷰", authorProfileUrl = null, isBookmarked = false
        ),
        Routine(
            id = 2, isRunning = false, title = "미라클 모닝", tags = listOf("#자기계발"), likes = 42, isLiked = false,
            description = "", imageUrl = null, category = "자기계발", authorName = "프리뷰", authorProfileUrl = null, isBookmarked = false
        ),
        Routine(
            id = 3, isRunning = false, title = "책 20페이지 읽기", tags = listOf("#독서"), likes = 18, isLiked = true,
            description = "", imageUrl = null, category = "독서", authorName = "프리뷰", authorProfileUrl = null, isBookmarked = false
        ),
        Routine(
            id = 4, isRunning = false, title = "물 2L 마시기", tags = listOf("#건강"), likes = 33, isLiked = false,
            description = "", imageUrl = null, category = "건강", authorName = "프리뷰", authorProfileUrl = null, isBookmarked = false
        )
    )

    val sampleLikeCounts = sampleRoutines.associate { it.id to it.likes }

    TitledRoutineSection(
        modifier = Modifier.fillMaxWidth(),
        title = "요즘 인기있는 루틴 🔥",
        routines = sampleRoutines,
        likeCounts = sampleLikeCounts,
        onRoutineClick = { },
        onLikeClick = { _, _ -> },
        onMoreClick = { }
    )
}