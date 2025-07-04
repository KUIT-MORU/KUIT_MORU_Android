package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.LocalMoruColorsProvider
import com.konkuk.moru.ui.theme.LocalMoruTypographyProvider
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun TodayRoutineListBoxItem(
    modifier: Modifier = Modifier,
    title: String = "주말 아침 루틴",
    hashtag: String = "#화이팅",
    dayAndTime: String = "토일 am 09:00 ~ am 09:58",
    progress: Float = 0.25f
) {
    Box(
        modifier = modifier
            .width(328.dp)
            .height(150.dp)
            .padding(8.dp)
    ) {
        Column() {
            Row(
                modifier = modifier.padding(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.transparentbox),
                    contentDescription = "ImageBox",
                    modifier = Modifier
                        .width(53.dp)
                        .height(52.dp)
                )
                Spacer(modifier = modifier.size(15.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = title,
                        style = typography.title_B_14,
                        color = colors.black,
                        textDecoration = TextDecoration.Underline
                    )
                    Spacer(modifier = modifier.size(1.dp))
                    Text(
                        text = hashtag,
                        style = typography.time_R_10,
                        color = colors.black
                    )
                    Spacer(modifier = modifier.size(3.dp))
                    Text(
                        text = dayAndTime,
                        style = typography.time_R_12,
                        color = colors.black
                    )
                }
            }
            Spacer(modifier = modifier.size(39.dp))
            //프로그래스 바와 진행률
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = modifier
                        .weight(1f),
                    color = colors.oliveGreen,
                    trackColor = colors.lightGray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = typography.desc_M_12,
                    color = colors.black
                )
            }
        }
    }
}

@Preview
@Composable
private fun TodayRoutineListBoxItemPreview() {
    TodayRoutineListBoxItem()
}