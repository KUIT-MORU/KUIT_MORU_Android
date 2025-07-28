package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import android.util.Log
import com.konkuk.moru.presentation.home.FabConstants
import com.konkuk.moru.presentation.home.screen.HomeTutorialOverlayView
import com.konkuk.moru.presentation.home.screen.TutorialOverlayView

@Composable
fun HomeTutorialOverlayContainer(
    modifier: Modifier,
    onDismiss: () -> Unit,
    onFabClick: () -> Unit,
    fabOffsetY: Float
) {
    val density = LocalDensity.current
    val config = LocalConfiguration.current

    // 디버깅: 화면 정보 로그 (필요시 주석 해제)
    // LaunchedEffect(Unit) {
    //     Log.d("TutorialOverlay", "=== Screen Debug Info ===")
    //     Log.d("TutorialOverlay", "Screen size: ${config.screenWidthDp}dp x ${config.screenHeightDp}dp")
    //     Log.d("TutorialOverlay", "Screen size px: ${config.screenWidthDp * density.density} x ${config.screenHeightDp * density.density}")
    //     Log.d("TutorialOverlay", "Density: ${density.density}")
    //     Log.d("TutorialOverlay", "FontScale: ${density.fontScale}")
    //     Log.d("TutorialOverlay", "FAB offsetY received: $fabOffsetY")
    //
    //     with(density) {
    //         val statusBarHeightPx = 24.dp.toPx()
    //         Log.d("TutorialOverlay", "Estimated status bar height: ${statusBarHeightPx}px")
    //     }
    //     Log.d("TutorialOverlay", "========================")
    // }

    val rectHole = remember {
        with(density) {
            TutorialOverlayView.HolePx(
                left = 36.dp.toPx(),
                top = 283.dp.toPx(),
                right = (36 + 288).dp.toPx(),
                bottom = (283 + 36).dp.toPx()
            )
        }
    }

    val circleHole = remember(fabOffsetY, config.screenWidthDp, config.screenHeightDp) {
        with(density) {
            val fabSize = FabConstants.FabSize
            val fabSizePx = fabSize.toPx()
            val fabPaddingEndPx = FabConstants.FabPaddingEnd.toPx()
            val screenWidthPx = config.screenWidthDp.dp.toPx()
            val screenHeightPx = config.screenHeightDp.dp.toPx()

            // FAB의 X 좌표 (오른쪽에서 padding + FAB 크기의 절반만큼 떨어진 위치)
            val fabCenterX = screenWidthPx - fabPaddingEndPx - fabSizePx / 2f

            // AndroidView와 Compose 간의 좌표계 차이 보정
            // 현재 40dp 오프셋 적용 중 - 필요시 값 조정
            val fabCenterY = fabOffsetY - 22.dp.toPx() // 40에서 35로 줄임

            val holeRadius = fabSizePx / 2f

            // 디버깅: 계산된 좌표 로그
            Log.d("TutorialOverlay", "FAB offsetY (original): $fabOffsetY")
            Log.d("TutorialOverlay", "FAB offsetY (adjusted): $fabCenterY")
            Log.d("TutorialOverlay", "Final FAB center: ($fabCenterX, $fabCenterY)")
            Log.d("TutorialOverlay", "Hole radius: $holeRadius")

            TutorialOverlayView.HolePx(
                left = fabCenterX - holeRadius,
                top = fabCenterY - holeRadius,
                right = fabCenterX + holeRadius,
                bottom = fabCenterY + holeRadius,
                isCircle = true
            )
        }
    }

    val holes = remember(rectHole, circleHole) {
        listOf(rectHole, circleHole)
    }

    Box(modifier = modifier.fillMaxSize()) {
        HomeTutorialOverlayView(holes = holes)
        HomeTutorialDecoration(onDismiss = onDismiss, onFabClick = onFabClick)
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
        modifier = Modifier.fillMaxSize().zIndex(2f),
        onDismiss = {},
        onFabClick = {},
        fabOffsetY = 632.5f // 이 값이 실제와 다를 수 있음
    )
}