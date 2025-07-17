package com.konkuk.moru.presentation.myactivity.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.konkuk.moru.presentation.myactivity.component.RoutineDetailContent
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

enum class RoutineStatus {
    FOCUSED, RELAXING, DONE
}

data class LocalDateTimeRange(
    val start: LocalDateTime,
    val end: LocalDateTime
)

data class RoutineStep(
    val order: Int,
    val name: String,
    val startTime: LocalTime,
    val endTime: LocalTime?,
    val duration: Duration
)

data class RoutineDetail(
    val title: String,
    val status: RoutineStatus,
    val tags: List<String>,
    val totalDuration: Duration,
    val result: String,
    val dateRange: LocalDateTimeRange,
    val steps: List<RoutineStep>
)

@Composable
fun ActRecordDetailScreen(modifier: Modifier = Modifier) {
    val dummyData = remember {
        RoutineDetail(
            title = "루틴 제목",
            status = RoutineStatus.FOCUSED,
            tags = listOf("#태그1", "#태그2", "#태그3"),
            totalDuration = Duration.ofMinutes(30).plusSeconds(56),
            result = "이완중",
            dateRange = LocalDateTimeRange(
                start = LocalDateTime.of(2025, 5, 9, 8, 0),
                end = LocalDateTime.of(2025, 5, 9, 9, 10)
            ),
            steps = listOf(
                RoutineStep(
                    order = 1,
                    name = "활동명",
                    startTime = LocalTime.of(10, 0),
                    endTime = LocalTime.of(10, 0),
                    duration = Duration.ZERO
                ),
                RoutineStep(
                    order = 2,
                    name = "활동명",
                    startTime = LocalTime.of(13, 20),
                    endTime = LocalTime.of(13, 0),
                    duration = Duration.ofMinutes(-20)
                ),
                RoutineStep(
                    order = 3,
                    name = "활동명",
                    startTime = LocalTime.of(7, 36),
                    endTime = LocalTime.of(10, 0),
                    duration = Duration.ofMinutes(144)
                )
            )
        )
    }

    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        RoutineDetailContent(detail = dummyData, modifier = modifier)
    }
}

@Preview
@Composable
private fun ActRecordDetailScreenPreview() {
    ActRecordDetailScreen()
}