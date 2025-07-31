package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun WeeklyCalendarView(
    modifier: Modifier = Modifier,
    routinesPerDate: Map<Int, List<String>>, // 날짜별 루틴 목록
    today: Int // ⬅️ 오늘 날짜 (예: 10)
) {
    val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")
    val dates = listOf(8, 9, 10, 11, 12, 13, 14)

    Column(
        modifier = modifier
            .height(160.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // 요일 행
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            daysOfWeek.forEach { day ->
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        style = typography.title_B_14,
                        color = colors.black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // 날짜 행
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            dates.forEach { date ->
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (date == today) {
                            Box(
                                modifier = Modifier
                                    .size(17.dp)
                                    .background(
                                        colors.limeGreen,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$date",
                                    style = typography.title_B_14,
                                    color = Color.White
                                )
                            }

                        } else {
                            Text(
                                text = "$date",
                                style = typography.title_B_14,
                                color = colors.mediumGray
                            )
                        }
                        // 루틴 태크(요일별 할 루틴요소들)
                        routinesPerDate[date]?.forEach { routine ->
                            Spacer(modifier = Modifier.height(2.dp))
                            RoutineTag(routine)
                        }
                    }
                }
            }
        }
    }
}

//루틴 태그 컴포넌트
@Composable
fun RoutineTag(
    text: String,
    minWidth: Dp = 48.dp
) {
    Text(
        text = text,
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = Modifier
            .background(
                color = colors.darkGray,
                shape = RoundedCornerShape(2.dp)
            )
            .defaultMinSize(
                minWidth = minWidth
            )
    )
}


@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 200
)
@Composable
private fun WeeklyCalendarViewPreview() {
    val sampleRoutines = mapOf(
        8 to listOf("아침 운동", "회의"),
        10 to listOf("아침 운동"),
        12 to listOf("아침 운동", "회의"),
        13 to listOf("주말아침루틴"),
        14 to listOf("주말아침루틴")
    )

    WeeklyCalendarView(
        routinesPerDate = sampleRoutines,
        today = 13 // 오늘 날짜 강조
    )
}
