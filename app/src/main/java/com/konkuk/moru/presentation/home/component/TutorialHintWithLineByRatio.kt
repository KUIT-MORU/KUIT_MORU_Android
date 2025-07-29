package com.konkuk.moru.presentation.home.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun TutorialHintWithLineByRatio(
    text: String,
    textOffsetRatio: Pair<Float, Float>,
    lineOffsetRatio: Pair<Float, Float>,
    lineLengthRatio: Float,
    isUpward: Boolean
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    val textOffsetDp = with(density) {
        Pair(
            (screenWidthPx * textOffsetRatio.first).toDp(),
            (screenHeightPx * textOffsetRatio.second).toDp()
        )
    }

    val lineOffsetDp = with(density) {
        Pair(
            (screenWidthPx * lineOffsetRatio.first).toDp(),
            (screenHeightPx * lineOffsetRatio.second).toDp()
        )
    }

    val lineLengthDp = with(density) {
        (screenHeightPx * lineLengthRatio).toDp()
    }

    TutorialHintWithLine(
        text = text,
        textOffsetDp = textOffsetDp,
        lineOffsetDp = lineOffsetDp,
        lineLength = lineLengthDp,
        isUpward = isUpward
    )
}
