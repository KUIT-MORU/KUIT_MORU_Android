package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R

@Composable
fun BottomOverlayBar(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f)) // 첫 번째 칸 비우기

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            BottomOverlayBarItem(
                iconResId = R.drawable.ic_routine_feed_white,
                label = "루틴 피드",
                iconWidth = 16.dp,
                iconHeight = 17.5.dp
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            BottomOverlayBarItem(
                iconResId = R.drawable.ic_my_routine_white,
                label = "내 루틴",
                iconWidth = 16.dp,
                iconHeight = 17.5.dp
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            BottomOverlayBarItem(
                iconResId = R.drawable.ic_my_activity_white,
                label = "내 활동",
                iconWidth = 16.dp,
                iconHeight = 17.5.dp
            )
        }
    }

}


@Preview(
    showBackground = true,
    backgroundColor = 0x80000000,
    widthDp = 360,
    heightDp = 100
)
@Composable
private fun BottomOverlayBarPreview() {
    Box(modifier = Modifier.fillMaxWidth()) {
        BottomOverlayBar(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}