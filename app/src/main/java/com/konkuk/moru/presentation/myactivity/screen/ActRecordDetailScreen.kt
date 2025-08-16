package com.konkuk.moru.presentation.myactivity.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.konkuk.moru.presentation.myactivity.component.RoutineDetailContent
import com.konkuk.moru.presentation.myactivity.viewmodel.MyActRecordDetailViewModel
import com.konkuk.moru.domain.model.MyActRecordDetail
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

enum class RoutineStatus { FOCUSED, RELAXING, DONE }

data class LocalDateTimeRange(
    val start: LocalDateTime,
    val end: LocalDateTime
)

data class RoutineStep(
    val order: Int,
    val name: String,
    val startTime: LocalTime,
    val endTime: LocalTime?,
    val duration: Duration,
    val memo: String? = null
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
fun ActRecordDetailScreen(
    logId: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vm: MyActRecordDetailViewModel = hiltViewModel()
) {
    val detail = vm.detail.collectAsState().value
    val loading = vm.loading.collectAsState().value
    val error = vm.error.collectAsState().value

    LaunchedEffect(logId) { vm.load(logId) }

    when {
        loading && detail == null -> {
            Text("로딩중")
        }
        error != null && detail == null -> {
            Text("error")
        }
        detail != null -> {
            val ui = remember(detail) { detail.toRoutineDetailUi() }
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // 내부 컴포넌트에는 외부 modifier 전달하지 않음(레이아웃 충돌 방지)
                RoutineDetailContent(detail = ui, navController = navController)
            }
        }
    }
}

/* ===================== 도메인 -> UI 매핑 ===================== */

// Z(UTC) 포함/미포함 모두 안전하게 파싱
private fun parseIsoToLocalDateTime(iso: String): LocalDateTime {
    // 1) Z 포함(UTC) 문자열 우선 처리
    runCatching {
        return Instant.parse(iso).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }
    // 2) 로컬 ISO DateTime 보조 처리
    return runCatching { LocalDateTime.parse(iso) }
        .getOrElse { LocalDateTime.now() }
}

private fun MyActRecordDetail.toRoutineDetailUi(): RoutineDetail {
    val start = parseIsoToLocalDateTime(startedAtIso)
    val totalDur = Duration.ofSeconds(totalSec.coerceAtLeast(0))
    val end = endedAtIso?.let { parseIsoToLocalDateTime(it) }
        ?: start.plusSeconds(totalDur.seconds)

    // 완료/미완료만 활용
    val status = if (isCompleted) RoutineStatus.DONE else RoutineStatus.FOCUSED
    val resultText = if (isCompleted) "완료" else "미완료"

    // step 시작/끝 시간: startedAt부터 actualSec 누적
    var cursor: LocalTime = start.toLocalTime()
    val stepUis = steps
        .sortedBy { it.order }
        .map { s ->
            val sec = s.actualSec.coerceAtLeast(0)
            val dur = Duration.ofSeconds(sec)
            val stepStart = cursor
            val stepEnd = stepStart.plusSeconds(dur.seconds)
            cursor = stepEnd
            RoutineStep(
                order = s.order,
                name = s.name,
                startTime = stepStart,
                endTime = stepEnd,
                duration = dur,
                memo = s.note
            )
        }

    return RoutineDetail(
        title = title,
        status = status,
        tags = tags.map { "#$it" },
        totalDuration = totalDur,
        result = resultText,
        dateRange = LocalDateTimeRange(start = start, end = end),
        steps = stepUis
    )
}
