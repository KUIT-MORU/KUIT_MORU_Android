package com.konkuk.moru.presentation.routinefeed.component.Routine

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
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.ui.theme.MORUTheme

@Composable
fun TitledRoutineSection(
    modifier: Modifier = Modifier,
    title: String,
    routines: List<Routine>,
    likeCounts: Map<Int, Int>,
    onRoutineClick: (Int) -> Unit,
    onLikeClick: (Int, Boolean) -> Unit,
    onMoreClick: (String) -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onMoreClick(title) }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 24.sp)
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
                    routineName = routine.title,
                    tags = routine.tags,
                    likeCount = likeCounts[routine.routineId] ?: routine.likes,
                    isLiked = routine.isLiked,
                    onLikeClick = { onLikeClick(routine.routineId, !routine.isLiked) },
                    onClick = { onRoutineClick(routine.routineId) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TitledRoutineSectionPreview() {
    MORUTheme {
        TitledRoutineSection(
            modifier = Modifier.fillMaxWidth(),
            title = "요즘 인기있는 루틴 🔥",
            routines = DummyData.feedRoutines.take(5),
            likeCounts = DummyData.feedRoutines.associate { it.routineId to it.likes },
            onRoutineClick = { },
            onLikeClick = { _, _ -> },
            onMoreClick = { _ -> }
        )
    }
}