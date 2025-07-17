package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.konkuk.moru.R
import com.konkuk.moru.presentation.myactivity.screen.RoutineDetail
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import java.time.format.DateTimeFormatter


@Composable
fun RoutineDetailContent(detail: RoutineDetail, navController: NavHostController, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(455.dp)
                .background(colors.veryLightGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Transparent,           // 상단 투명
                                0.55f to Color(0xFFF5F5F5),         // 아주 연한 회색
                                0.75f to Color(0xFFD9D9D9),         // 중간 밝기 회색
                                0.90f to Color(0xFFBBBBBB),         // 약간 진한 회색
                                1.0f to Color(0xFF999999)           // 중간 진함 (더 이상 어둡지 않음)
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = {navController.popBackStack()},
                modifier = Modifier
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_d),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = detail.title,
                        style = typography.title_B_24,
                        color = colors.darkGray
                    )
                    Spacer(modifier = Modifier.width(17.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(47.dp)
                            .height(28.dp)
                            .background(colors.paleLime, RoundedCornerShape(12.dp))
                    ) {
                        Text("집중", color = colors.oliveGreen, style = typography.body_SB_16)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row {
                    detail.tags.forEach {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .height(18.dp)
                                .background(
                                    color = colors.darkGray,
                                    shape = RoundedCornerShape(140.dp)
                                ),
                        ) {
                            Text(
                                text = it,
                                color = colors.paleLime,
                                style = typography.time_R_14,
                                modifier = Modifier.padding(horizontal = 7.dp)
                                )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(11.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.width(130.dp)
                ) {
                    Text("TOTAL", style = typography.time_R_14, color = Color.White)
                    Text(
                        text = String.format(
                            "%d분 %02d초",
                            detail.totalDuration.toMinutes(),
                            detail.totalDuration.seconds % 60
                        ),
                        style = typography.title_B_14,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.width(109.dp)
                ) {
                    Text("RESULT", style = typography.time_R_14, color = Color.White)
                    CompleteCheck(false)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.width(266.dp)
                ) {
                    Text("DATE", style = typography.time_R_14, color = Color.White)
                    Text(
                        text = detail.dateRange.start.format(DateTimeFormatter.ofPattern("yyyy. MM. dd a hh:mm")) +
                                " ~ " + detail.dateRange.end.format(DateTimeFormatter.ofPattern("hh:mm")),
                        style = typography.title_B_14, color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(colors.lightGray, RoundedCornerShape(5.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(11.dp))
            }
        }

        Spacer(modifier = Modifier.height(26.dp))
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text("STEP", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            detail.steps.forEach { step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${step.order}", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(step.name, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = step.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        step.endTime?.let {
                            Text(
                                text = it.format(DateTimeFormatter.ofPattern("HH:mm")),
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
