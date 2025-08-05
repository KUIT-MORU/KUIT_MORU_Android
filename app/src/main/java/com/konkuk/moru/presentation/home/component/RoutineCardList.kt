package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.data.model.Routine

@Composable
fun RoutineCardList(
    routines: List<Routine>,
    onRoutineClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    //스크롤 대비 상태 저장
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(17.dp)
    ) {
        //나중에 진짜 루틴들 받아올 것
        routines.forEach { routine ->
            RoutineCardItem(
                title = routine.title,
                tags = routine.tags,
                onClick = { onRoutineClick(routine.routineId) }
            )
        }
    }
}

@Preview
@Composable
private fun RoutineCardListPreview() {
    val dummyRoutines = listOf(
        Routine(
            routineId = 1,
            title = "MORU의 집중 코딩",
            description = "집중력을 높이는 코딩 루틴",
            imageUrl = null,
            category = "공부",
            tags = listOf("개발", "코딩"),
            authorId = 1,
            authorName = "MORU",
            authorProfileUrl = null,
            likes = 10,
            isLiked = false,
            isBookmarked = false,
            isRunning = false
        ),
        Routine(
            routineId = 2,
            title = "아침 명상 루틴",
            description = "마음을 가다듬는 아침 명상",
            imageUrl = null,
            category = "건강",
            tags = listOf("명상", "아침"),
            authorId = 1,
            authorName = "MORU",
            authorProfileUrl = null,
            likes = 8,
            isLiked = true,
            isBookmarked = false,
            isRunning = false
        ),
        Routine(
            routineId = 3,
            title = "주말 대청소",
            description = "집을 깨끗이! 대청소 루틴",
            imageUrl = null,
            category = "생활",
            tags = listOf("청소", "집안일"),
            authorId = 1,
            authorName = "MORU",
            authorProfileUrl = null,
            likes = 5,
            isLiked = false,
            isBookmarked = true,
            isRunning = false
        )
    )

    RoutineCardList(
        routines = dummyRoutines,
        onRoutineClick = {}
    )
}
