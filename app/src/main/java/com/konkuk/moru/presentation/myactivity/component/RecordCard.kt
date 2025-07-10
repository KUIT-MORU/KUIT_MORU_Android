package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun RecordCard(
    title: String,
    tags: List<String>,
    imageResId: Int = R.drawable.ic_record_img,
    time: String = "00:00:00",
    completeFlag: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .width(90.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            if (completeFlag) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp, end = 6.dp)
                        .background(colors.black, shape = RoundedCornerShape(size = 100.dp))
                        .width(32.dp)
                        .height(17.dp)

                ) {
                    Text(
                        text = "완료",
                        color = Color(0xFFFFFFFF),
                        style = typography.desc_M_12,
                        modifier = Modifier
                    )
                }
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp, end = 6.dp)
                        .border(
                            width = 1.dp,
                            color = colors.black,
                            shape = RoundedCornerShape(size = 100.dp)
                        )
                        .width(40.dp)
                        .height(18.dp)
                ) {
                    Text(
                        text = "미완료",
                        color = colors.black,
                        style = typography.desc_M_12,
                        modifier = Modifier
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 7.dp)
            ) {
                Text(
                    text = time,
                    color = Color(0xFFFFFFFF),
                    style = typography.title_B_14,
                    modifier = Modifier
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = title,
        color = colors.black,
        style = typography.body_SB_14,
        modifier = Modifier.padding(horizontal = 4.dp)
    )

    Spacer(modifier = Modifier.height(4.dp))
    Box(modifier = Modifier.width(85.dp)) {
        Text(
            text = tags.joinToString(" ") { "#$it" },
            color = colors.mediumGray,
            style = typography.time_R_12,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

