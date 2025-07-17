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
    onDismiss: () -> Unit,
    onFabClick: () -> Unit
) {
    // 좌표나 구멍 크기는 px값을 필요로 하므로 dp <-> px를 위한 저장
    val density = LocalDensity.current

    // 디바이스의 현재 화면 정보(너비,높이,폰트 비율 등(dp)) 저장
    val config = LocalConfiguration.current

    // 사각형 구멍
    val rectHole = remember {
        with(density) {
            TutorialOverlayView.HolePx(
                // 구멍의 좌상단 좌표
                left = 36.dp.toPx(),
                top = 283.dp.toPx(),
                // 구멍의 우하단 좌표
                right = (36 + 288).dp.toPx(),
                bottom = (283 + 36).dp.toPx()
            )
        }
    }

    // 원형 구멍
    val circleHole = remember {
        with(density) {
            // FAB 크기
            val fabSizePx = 63.dp.toPx()

            // FAB 오른쪽 padding
            val fabPaddingEndPx = 16.dp.toPx()

            // FAB 아래쪽 padding
            val fabPaddingBottomPx = 96.dp.toPx()

            // 현재 디바이스의 화면 너비/높이
            val screenWidthPx = config.screenWidthDp.dp.toPx()
            val screenHeightPx = config.screenHeightDp.dp.toPx()

            // FAB의 중심 좌표 계산
            val fabCenterX = screenWidthPx - fabPaddingEndPx - fabSizePx / 2f
            val fabCenterY = screenHeightPx - fabPaddingBottomPx - fabSizePx / 2f

            //
            TutorialOverlayView.HolePx(
                // 구멍의 좌상단 좌표
                left = fabCenterX - fabSizePx / 2f,
                top = fabCenterY - fabSizePx / 2f,

                // 구멍의 우하단 좌표
                right = fabCenterX + fabSizePx / 2f,
                bottom = fabCenterY + fabSizePx / 2f,

                // 원형 구멍으로 표시
                isCircle = true
            )
        }
    }

    val holes = remember { listOf(rectHole, circleHole) }

    Box(modifier = Modifier.fillMaxSize()) {
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
        onDismiss = {},
        onFabClick = {}
    )
}
