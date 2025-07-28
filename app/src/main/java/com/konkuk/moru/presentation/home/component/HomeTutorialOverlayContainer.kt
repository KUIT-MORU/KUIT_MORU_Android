package com.konkuk.moru.presentation.home.component

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.ui.zIndex
import com.konkuk.moru.presentation.home.FabConstants

import com.konkuk.moru.presentation.home.screen.HomeTutorialOverlayView
import com.konkuk.moru.presentation.home.screen.TutorialOverlayView

@Composable
fun HomeTutorialOverlayContainer(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onFabClick: () -> Unit,
    fabOffsetY: Float,
    todayTabOffsetY: Float
) {
    val density = LocalDensity.current
    val config = LocalConfiguration.current

    val rectHole = remember {
        with(density) {
            TutorialOverlayView.HolePx(
                left = 36.dp.toPx(),
                top = todayTabOffsetY- 18.dp.toPx(),
                right = (36 + 288).dp.toPx(),
                bottom = todayTabOffsetY + 18.dp.toPx()
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
          
            // AndroidView와 Compose 간의 좌표계 차이 보정
            val fabCenterY = fabOffsetY

            val holeRadius = fabSizePx / 2f

            // 디버깅: 계산된 좌표 로그
            Log.d("TutorialOverlay", "FAB offsetY (original): $fabOffsetY")
            Log.d("TutorialOverlay", "FAB offsetY (adjusted): $fabCenterY")
            Log.d("TutorialOverlay", "Final FAB center: ($fabCenterX, $fabCenterY)")
            Log.d("TutorialOverlay", "Hole radius: $holeRadius")


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
        modifier = Modifier
            .fillMaxSize()
            .zIndex(2f),
        onDismiss = {},
        onFabClick = {},
        fabOffsetY = 632.5f, // 이 값이 실제와 다를 수 있음
        todayTabOffsetY = 283f // 샘플값 넣기
    )
}
