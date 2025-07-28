package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun RoutineCardItem(
    modifier: Modifier = Modifier,
    isRunning: Boolean = false // 지금 루틴 실행중인가?
) {
    Box(
        modifier = modifier
            .width(98.dp)
            .height(190.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.group_208),
                contentDescription = "루틴 썸네일",
                modifier = Modifier
                    .width(98.dp)
                    .height(130.dp)
                    .then(
                        if(isRunning) Modifier
                            .border(
                                width=3.dp,
                                color = colors.limeGreen,
                                shape = RoundedCornerShape(4.dp)
                            )
                        else Modifier
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column{
                Text(
                    text = "루틴명",
                    style = typography.time_R_12.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = colors.black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "#태그",
                    style = typography.time_R_10,
                    color = colors.darkGray
                )
            }
        }
    }
}

@Preview
@Composable
private fun RoutineCardItemPreview() {
    // true false에 따라 테두리가 생기고 없어지고
    RoutineCardItem(isRunning = true)
}