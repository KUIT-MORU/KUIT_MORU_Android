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
    Column(modifier = modifier.padding(vertical = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onMoreClick() }.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "더보기", modifier = Modifier.size(28.dp))
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
                    tag = routine.tag,
                    likeCount = likeCounts[routine.id] ?: routine.likes, // 좋아요 수는 나중에 상태관리 필요
                    isLiked = routine.isLiked,
                    onLikeClick = { onLikeClick(routine.id, !routine.isLiked) },
                    onClick = { onRoutineClick(routine.id) }
                )
            }
        }
    }
}
