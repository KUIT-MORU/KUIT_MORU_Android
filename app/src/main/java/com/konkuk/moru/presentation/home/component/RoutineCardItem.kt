package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import androidx.compose.material3.Text
import com.konkuk.moru.ui.theme.LocalMoruColorsProvider
import com.konkuk.moru.ui.theme.LocalMoruTypographyProvider

@Composable
fun RoutineCardItem(modifier: Modifier = Modifier) {
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
            )
            Spacer(modifier = modifier.size(8.dp))
            Text(
                text = "루틴명",
                style = LocalMoruTypographyProvider.current.time_R_12,
                color = LocalMoruColorsProvider.current.black
            )
            Spacer(modifier = modifier.size(2.dp))
            Text(
                text = "#태그",
                style = LocalMoruTypographyProvider.current.time_R_10,
                color = LocalMoruColorsProvider.current.darkGray
            )
        }
    }
}

@Preview
@Composable
private fun RoutineCardItemPreview() {
    RoutineCardItem()
}