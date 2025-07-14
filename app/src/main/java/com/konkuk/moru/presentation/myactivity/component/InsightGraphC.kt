package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun InsightGraphC(
    morning: Float,
    afternoon: Float,
    night: Float,
    lateNight: Float,
    modifier: Modifier = Modifier
) {
    val labels = listOf("오전", "오후", "밤", "심야")
    val values = listOf(morning, afternoon, night, lateNight)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "시간대별 루틴 실천률",
            style = typography.body_SB_16,
            color = colors.black,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(17.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(167.dp)
                .padding(end = 28.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(30.dp)
                    .padding(bottom = 17.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                for (i in 10 downTo 0 step 1) {
                    Text(
                        text = "${i * 10}",
                        color = colors.darkGray,
                        style = typography.body_SB_16.copy(fontSize = 9.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                values.forEachIndexed { index, value ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .width(35.dp)
                                .height(149.dp * (value.coerceIn(0f, 100f) / 100f))
                                .clip(RoundedCornerShape(topStart = 100.dp, topEnd = 100.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(colors.limeGreen, Color.Transparent)
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        Text(
                            text = labels[index],
                            style = typography.body_SB_16.copy(fontSize = 9.sp),
                            color = colors.darkGray
                        )
                    }
                }
            }
        }
    }
}