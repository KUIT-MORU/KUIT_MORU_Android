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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun RoutinePaceCard(
    routinePace: String = "미정",
    progress: Float = 0.1f,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(97.dp)
            .background(color = colors.charcoalBlack, shape = RoundedCornerShape(8.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
            ){
                Text(text = "나의 루틴 페이스는 ", style = typography.body_SB_16, color = Color(0xFFFFFFFF))
                Text(text = routinePace, style = typography.body_SB_16, color = colors.limeGreen)
            }
            Icon(
                painterResource(R.drawable.ic_routinepace_info),
                contentDescription = "Settings Icon",
                tint = colors.mediumGray,
                modifier = Modifier
                    .padding(top = 15.dp, end = 16.dp)
                    .size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(9.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .weight(1f)
            ) {
                MovingIconProgressBar(progress)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .padding(bottom = 11.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = (progress * 100).toString() + "%",
                    color = colors.limeGreen,
                    style = typography.body_SB_16
                )
            }
        }
    }
}

@Preview
@Composable
private fun RoutinePaceCardPreview() {
    RoutinePaceCard("간헐적 루틴러", 0.648f)
}
