package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun InsightGraphB(
    userName: String,
    weekdayUser: Float,
    weekdayAll: Float,
    weekendUser: Float,
    weekendAll: Float,
    modifier: Modifier = Modifier
) {
    val labelsWithProgress = listOf(
        "평일 : $userName"+"님" to weekdayUser,
        "평일 : 전체" to weekdayAll,
        "주말 : $userName"+"님" to weekendUser,
        "주말 : 전체" to weekendAll
    )

    Column(
        modifier = modifier
            .padding(horizontal = 29.dp)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = "평균 루틴 실천률",
                style = typography.body_SB_16,
                color = colors.darkGray
            )
        }
        Spacer(modifier = Modifier.height(18.dp))

        labelsWithProgress.forEach { (label, progress) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = label,
                    style = typography.desc_M_12.copy(fontSize = 10.sp),
                    color = colors.darkGray,
                    modifier = Modifier.width(70.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(50))
                        .background(colors.veryLightGray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(colors.paleLime, colors.limeGreen)
                                )
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(21.dp))
        }
    }
}