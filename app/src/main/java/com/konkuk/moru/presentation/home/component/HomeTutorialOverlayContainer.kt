package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.presentation.home.screen.HomeTutorialOverlayView
import com.konkuk.moru.presentation.home.screen.TutorialOverlayView

@Composable
fun HomeTutorialOverlayContainer(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onFabClick: () -> Unit
) {
    val density = LocalDensity.current
    val config = LocalConfiguration.current

    val rectHole = remember {
        with(density) {
            TutorialOverlayView.HolePx(
                left = 36.dp.toPx(),
                top = 259.dp.toPx(),
                right = (36 + 288).dp.toPx(),
                bottom = (259 + 36).dp.toPx()
            )
        }
    }

    val circleHole = remember {
        with(density) {
            val fabSizePx = 63.dp.toPx()
            val fabPaddingEndPx = 16.dp.toPx()
            val fabPaddingBottomPx = 96.dp.toPx()

            val screenWidthPx = config.screenWidthDp.dp.toPx()
            val screenHeightPx = config.screenHeightDp.dp.toPx()

            val fabCenterX = screenWidthPx - fabPaddingEndPx - fabSizePx / 2f
            val fabCenterY = screenHeightPx - fabPaddingBottomPx - fabSizePx / 2f

            TutorialOverlayView.HolePx(
                left = fabCenterX - fabSizePx / 2f,
                top = fabCenterY - fabSizePx / 2f,
                right = fabCenterX + fabSizePx / 2f,
                bottom = fabCenterY + fabSizePx / 2f,
                isCircle = true
            )
        }
    }

    val holes = remember { listOf(rectHole, circleHole) }

    Box(modifier = modifier.fillMaxSize()) {
        HomeTutorialOverlayView(
            holes = holes,
            onDismiss = onDismiss
        )

        HomeTutorialDecoration(
            onDismiss = onDismiss,
            onFabClick = onFabClick
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun HomeTutorialOverlayContainerPreview() {
    HomeTutorialOverlayContainer(
        onDismiss = {},
        onFabClick = {}
    )
}
