package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun RoutineCardItem(
    title: String,
    tag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .width(98.dp)
            .height(190.dp)
            .clickable { onClick() }
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.group_208),
                contentDescription = "루틴 썸네일",
                modifier = Modifier
                    .width(98.dp)
                    .height(130.dp)
            )
            Spacer(modifier = modifier.height(8.dp))
            Text(
                text = title,
                style = typography.time_R_12,
                color = colors.black
            )
            Spacer(modifier = modifier.height(2.dp))
            Text(
                text = tag,
                style = typography.time_R_10,
                color = colors.darkGray
            )
        }
    }
}

@Preview
@Composable
private fun RoutineCardItemPreview() {
    RoutineCardItem(
        title = "MORU의 스트레칭 루틴",
        tag = "운동 건강"
    )
}