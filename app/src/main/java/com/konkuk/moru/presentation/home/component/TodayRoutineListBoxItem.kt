package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun TodayRoutineListBoxItem(
    modifier: Modifier = Modifier,
    title: String = "아침 운동",
    hashtag: String = "#모닝 루틴 #스트레칭",
    heartCount: Int = 16,
    day: String = "토일",
    time: String = "am 09:00",
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .width(330.dp)
            .height(120.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
            )
            .background(
                color = Color.White,  // 원하는 배경색
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ) {
            // 1.카드 박스
            Box(
                modifier = Modifier
                    .width(298.dp)
                    .height(72.dp)
            ) {
                Row() {
                    // 루틴 이미지
                    Image(
                        painter = painterResource(id = R.drawable.routine_image),
                        contentDescription = "ImageBox",
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 7.dp)
                    ) {
                        //제목(ex)아침 운동)
                        Text(
                            text = title,
                            style = typography.title_B_14,
                            color = colors.black,
                        )
                        Spacer(modifier = modifier.size(2.dp))
                        // 해시태그(ex)#모닝 루틴,#스트레칭)
                        Text(
                            text = hashtag,
                            style = typography.time_R_10,
                            color = colors.black
                        )
                        Spacer(modifier = modifier.size(3.dp))
                        //하트와 하트 클릭 수
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.empty_heart),
                                contentDescription = "empty heart Icon",
                                modifier = Modifier.size(width = 13.33.dp, height = 11.47.dp)
                            )
                            Text(
                                text = "$heartCount",
                                style = typography.time_R_12,
                                color = colors.black
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(14.dp))
            // 2. 요일과 시간
            Row() {
                Text(
                    text = day,
                    style = typography.title_B_12,
                    color = colors.black
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = time,
                    style = typography.title_B_12,
                    color = colors.black
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 330,
    heightDp = 120
)
@Composable
private fun TodayRoutineListBoxItemPreview() {
    TodayRoutineListBoxItem(
        onClick = {}
    )
}