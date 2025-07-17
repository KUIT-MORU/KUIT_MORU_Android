package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.presentation.myactivity.screen.RoutineDetail
import java.time.format.DateTimeFormatter

@Composable
fun RoutineDetailContent(detail: RoutineDetail, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Color(0xFFF6F6F6), Color(0xFFD9D9D9))
                    )
                )
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Spacer(modifier = Modifier.height(120.dp)) // 이미지 자리
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = detail.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFB8EE44), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("집중", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    detail.tags.forEach {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF444444), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .padding(end = 4.dp)
                        ) {
                            Text(text = it, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("TOTAL", fontSize = 12.sp, color = Color.Gray)
                Text(
                    text = String.format(
                        "%d분 %02d초",
                        detail.totalDuration.toMinutes(),
                        detail.totalDuration.seconds % 60
                    ),
                    fontSize = 14.sp, fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("RESULT", fontSize = 12.sp, color = Color.Gray)
                Text(text = detail.result, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("DATE", fontSize = 12.sp, color = Color.Gray)
                Text(
                    text = detail.dateRange.start.format(DateTimeFormatter.ofPattern("yyyy. MM. dd a hh:mm")) +
                            " ~ " + detail.dateRange.end.format(DateTimeFormatter.ofPattern("hh:mm")),
                    fontSize = 14.sp, fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text("STEP", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            detail.steps.forEach { step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${step.order}", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(step.name)
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = step.startTime.toString(),
                            fontWeight = FontWeight.SemiBold
                        )
                        step.endTime?.let {
                            Text(
                                text = it.toString(),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

}
