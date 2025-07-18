package com.konkuk.moru.presentation.routinefocus.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.ui.theme.MORUTheme

@Composable
fun RoutineTimelineItem(
    time: String, //소요시간
    title: String, //세부 루틴명
    index: Int, // 1부터 시작
    currentStep: Int, // 1부터 시작
    isTimeout: Boolean // 시간 초과 유무
) {
    val colors = MORUTheme.colors
    val typography = MORUTheme.typography

    // 진행했거나 진행 중이거나
    val isActive = index <= currentStep

    // 진행상태에 따른 텍스트
    val textColor = if (isActive) colors.black else colors.darkGray
    val lineColor = when {
        isTimeout && isActive -> colors.black
        !isTimeout && isActive -> colors.limeGreen
        else -> colors.lightGray
    }

    // 점선의 촘촘한 정도
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 시간
        Text(
            text = time,
            style = typography.time_R_14,
            color = textColor,
            modifier = Modifier.width(40.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        // 타임라인
        Box(
            modifier = Modifier
                .width(20.dp)
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2
                val centerY = size.height / 2

                // 위 선
                drawLine(
                    color = lineColor,
                    start = Offset(centerX, 0f),
                    end = Offset(centerX, centerY - 6.dp.toPx()),
                    strokeWidth = 3.dp.toPx(),
                    pathEffect = if (isActive) null else pathEffect
                )

                // 아래 선
                drawLine(
                    color = lineColor,
                    start = Offset(centerX, centerY + 6.dp.toPx()),
                    end = Offset(centerX, size.height),
                    strokeWidth = 3.dp.toPx(),
                    pathEffect = if (isActive) null else pathEffect
                )

                if (isActive) {
                    //원 내부 칠하기
                    drawCircle(
                        color = Color.White,
                        center = Offset(centerX, centerY),
                        radius = 6.dp.toPx(),
                        style = Fill
                    )

                    // 원 테두리
                    drawCircle(
                        color = lineColor,
                        center = Offset(centerX, centerY),
                        radius = 6.dp.toPx(),
                        style = Stroke(width = 2.dp.toPx())
                    )
                } else {
                    // 미진행 상태: 작은 회색 원
                    drawCircle(
                        color = colors.lightGray,
                        center = Offset(centerX, centerY),
                        radius = 4.dp.toPx(),
                        style = Fill
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        // 태스크 이름
        Text(
            text = title,
            style = typography.time_R_16,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoutineTimelineItemPreview() {
    val routineItems = listOf(
        "샤워하기" to "15m",
        "청소하기" to "10m",
        "밥먹기" to "30m",
        "옷갈아입기" to "3m"
    )

    Column {
        routineItems.forEachIndexed { rawIndex, (title, time) ->
            val index = rawIndex + 1
            RoutineTimelineItem(
                time = time,
                title = title,
                index = index,
                currentStep = 2, // limeGreen 스타일은 1, 2번만 적용됨
                isTimeout = false
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // isTimeout = true (모두 검정색 표시)
        routineItems.forEachIndexed { rawIndex, (title, time) ->
            val index = rawIndex + 1
            RoutineTimelineItem(
                time = time,
                title = title,
                index = index,
                currentStep = 2,   // 중요하지 않음
                isTimeout = true   // ← 시간 초과 상태
            )
        }
    }
}
