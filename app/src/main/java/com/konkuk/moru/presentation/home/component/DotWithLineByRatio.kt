package com.konkuk.moru.presentation.home.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun DotWithLineByRatio(
    startOffsetRatio: Pair<Float, Float>,
    lengthRatio: Float,
    isUpward: Boolean
) {
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp.dp
    val screenHeight = config.screenHeightDp.dp

    val startX = screenWidth * startOffsetRatio.first
    val startY = screenHeight * startOffsetRatio.second
    val length = screenHeight * lengthRatio

    DotWithLine(
        startX = startX,
        startY = startY,
        length = length,
        isUpward = isUpward
    )
}
