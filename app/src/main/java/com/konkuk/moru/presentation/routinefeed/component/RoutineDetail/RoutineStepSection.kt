package com.konkuk.moru.presentation.routinefeed.component.RoutineDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.core.component.button.MoruButton
import com.konkuk.moru.presentation.routinefeed.data.RoutineStep

@Composable
fun RoutineStepSection(modifier: Modifier = Modifier, steps: List<RoutineStep>) {
    Column(modifier = modifier) {
        // "STEP" 타이틀과 버튼 Row (기존과 동일)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("STEP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            MoruButton(
                text = "내 루틴에 추가",
                onClick = { /* ... */ },
                backgroundColor = Color(0xFFF1F3F5),
                contentColor = Color.Black,
                fontSize = 12.sp,
                iconContent = { Icon(Icons.Default.CalendarToday, "캘린더", Modifier.size(16.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 활동 목록
        Column {
            steps.forEachIndexed { index, step ->
                // ✅ index를 stepNumber로 전달하도록 수정
                RoutineStepItem(stepNumber = index + 1, step = step)

                // 구분선
                if (index < steps.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

// ✅ stepNumber를 파라미터로 받도록 수정
@Composable
fun RoutineStepItem(stepNumber: Int, step: RoutineStep) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ✅ 파라미터로 받은 stepNumber를 표시
        Text(
            text = "$stepNumber",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = step.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = step.duration,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}