package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import com.google.accompanist.flowlayout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun RoutineCardItem(
    title: String,
    tags: List<String>,
    scheduledDays: Set<DayOfWeek> = emptySet(),
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isHighlighted: Boolean = false
) {
    // 디버깅용 로그 추가
    LaunchedEffect(scheduledDays) {
        android.util.Log.d("RoutineCardItem", "🔍 루틴: $title, scheduledDays=$scheduledDays, isEmpty=${scheduledDays.isEmpty()}")
    }
    Box(
        modifier = modifier
            .width(98.dp)
            .height(190.dp)
            .clickable { onClick() }
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.group_208),
                contentDescription = "루틴 썸네일",
                modifier = Modifier
                    .width(98.dp)
                    .height(130.dp)
            )
            Spacer(modifier = modifier.height(8.dp))
            Text(
                text = title,
                style = typography.title_B_12,
                color = colors.black
            )
            Spacer(modifier = modifier.height(2.dp))
            FlowRow(
                mainAxisSpacing = 4.dp,
                crossAxisSpacing = 2.dp
            ) {
                tags.forEach { tag ->
                    Text(
                        text = "#$tag",
                        style = typography.time_R_10.copy(fontWeight = FontWeight.Bold),
                        color = colors.darkGray
                    )
                }
            }
            
            // 요일 정보 표시 (월화수목금토일 순서로 정렬)
            if (scheduledDays.isNotEmpty()) {
                Spacer(modifier = modifier.height(4.dp))
                Text(
                    text = scheduledDays
                        .sortedBy { it.value } // DayOfWeek.value로 정렬 (월=1, 화=2, ..., 일=7)
                        .joinToString("") { day ->
                            day.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
                        },
                    style = typography.time_R_10,
                    color = colors.darkGray
                )
            }
        }
    }
}

@Preview
@Composable
private fun RoutineCardItemPreview() {
    RoutineCardItem(
        title = "MORU의 스트레칭 루틴",
        tags = listOf("운동", "건강"),
        scheduledDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
        isHighlighted = true
    )
}