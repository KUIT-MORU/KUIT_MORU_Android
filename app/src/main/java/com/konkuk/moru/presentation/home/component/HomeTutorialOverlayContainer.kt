package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.konkuk.moru.presentation.home.screen.HomeTutorialOverlayView
import com.konkuk.moru.presentation.home.screen.TutorialOverlayView

@Composable
fun HomeTutorialOverlayContainer(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onFabClick: () -> Unit,
    fabOffsetY: Float,
    todayTabOffsetY: Float,
    bottomIconCenters: List<Offset>
) {
    LaunchedEffect(bottomIconCenters) {
        bottomIconCenters.forEachIndexed { idx, offset ->
        }
    }

    val density = LocalDensity.current
    val config = LocalConfiguration.current

    val rectHole = remember(todayTabOffsetY) { // todayTabOffsetY 값이 변경될 때마다 재계산
        with(density) {
            // 로그에서 확인된 실제 탭 bounds: Rect.fromLTRB(16.0, 279.0, 344.0, 315.0)
            // 탭의 실제 높이: 315.0 - 279.0 = 36.0px
            val tabHeightPx = 36.dp.toPx() // 36dp를 픽셀로 변환

            // 로그에서 확인된 실제 패딩값 사용
            val horizontalPaddingPx = 16.dp.toPx() // 16dp를 픽셀로 변환

            // 화면 너비
            val screenWidthPx = config.screenWidthDp.dp.toPx()

            // 구멍의 위치 계산 - 로그에서 확인된 bounds와 일치하도록
            val holeLeft = horizontalPaddingPx  // 16.0
            val holeRight = screenWidthPx - horizontalPaddingPx  // 344.0 (360 - 16)
            val holeTop = todayTabOffsetY - (tabHeightPx / 2f)  // 297.0 - 18.0 = 279.0
            val holeBottom = todayTabOffsetY + (tabHeightPx / 2f)  // 297.0 + 18.0 = 315.0

            TutorialOverlayView.HolePx(
                left = holeLeft,
                top = holeTop,
                right = holeRight,
                bottom = holeBottom,
                isCircle = false
            )
        }
    }

    val circleHole = remember(fabOffsetY) { // fabOffsetY 값이 변경될 때마다 재계산
        with(density) {
            val fabSizePx = 63.dp.toPx()
            val fabPaddingEndPx = 16.dp.toPx()

            val screenWidthPx = config.screenWidthDp.dp.toPx()

            // FAB의 중심 좌표 계산
            val fabCenterX = screenWidthPx - fabPaddingEndPx - fabSizePx / 2f
            val fabCenterY = fabOffsetY

            // 구멍의 위치 계산
            val holeLeft = fabCenterX - fabSizePx / 2f
            val holeTop = fabCenterY - fabSizePx / 2f
            val holeRight = fabCenterX + fabSizePx / 2f
            val holeBottom = fabCenterY + fabSizePx / 2f

            TutorialOverlayView.HolePx(
                left = holeLeft,
                top = holeTop,
                right = holeRight,
                bottom = holeBottom,
                isCircle = true
            )
        }
    }

    val holes = remember(rectHole, circleHole) { listOf(rectHole, circleHole) }

    Box(modifier = modifier.fillMaxSize()) {
        HomeTutorialOverlayView(
            holes = holes,
        )

        HomeTutorialDecoration(
            onDismiss = onDismiss,
            onFabClick = onFabClick,
            bottomIconCenters = bottomIconCenters
        )

        BottomOverlayBar(
            iconCenters = bottomIconCenters,
            modifier = Modifier.zIndex(3f) // 오버레이 요소 위에 표시
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
        fabOffsetY = 632.5f,   // 실제 측정값 기반
        todayTabOffsetY = 283f,
        bottomIconCenters = listOf(
            Offset.Zero,              // 홈 (highlight 안함)
            Offset(134f, 748f),       // 루틴 피드
            Offset(226f, 748f),       // 내 루틴
            Offset(318f, 748f)        // 내 활동
        )
    )
}
