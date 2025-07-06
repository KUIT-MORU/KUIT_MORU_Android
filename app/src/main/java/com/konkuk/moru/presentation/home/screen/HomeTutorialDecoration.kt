package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun HomeTutorialDecoration(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onFabClick: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        DotWithArrowUp(startX = 53.dp, startY = 304.dp, length = 76.dp)
        Text(
            text = "정기 루틴 확인하기",
            style = typography.time_R_14,
            color = Color.White,
            modifier = modifier.offset(x = 63.dp, y = 368.dp)
        )

        DotWithArrow(startX = 313.dp, startY = 532.dp, length = 109.dp)
        Text(
            text = "루틴 생성하기",
            style = typography.time_R_14,
            color = Color.White,
            modifier = modifier.offset(x = 274.dp, y = 507.dp)
        )

        DotWithArrow(startX = 137.dp, startY = 681.dp, length = 61.dp)
        Text(
            text = "루틴 둘러보기",
            style = typography.time_R_14,
            color = Color.White,
            modifier = modifier.offset(x = 50.dp, y = 672.dp)
        )

        DotWithArrow(startX = 223.dp, startY = 681.dp, length = 64.dp)
        Text(
            text = "루틴 관리하기",
            style = typography.time_R_14,
            color = Color.White,
            modifier = modifier.offset(x = 183.dp, y = 656.dp)
        )

        DotWithBentArrow(
            startX = 273.dp,
            startY = 591.dp,
            verticalLength = 166.dp,
            horizontalLength = 28.dp
        )
        Text(
            text = "루틴 인사이트 보기",
            style = typography.time_R_14,
            color = Color.White,
            modifier = modifier.offset(x = 159.dp, y = 582.dp)
        )

        SimpleBottomTailBalloon(text = "바로 생성하기! > ", offsetX = 213.dp, offsetY = 451.dp)

        Box(
            modifier = modifier
                .offset(x = 281.dp, y = 641.dp)
                .size(63.dp)
                .clickable { onFabClick() }
        )

        BottomBarIconWithLabelOverlay(R.drawable.ic_routine_feed, "루틴 피드", 110.dp, 744.dp)
        BottomBarIconWithLabelOverlay(R.drawable.ic_my_routine, "내 루틴", 202.dp, 744.dp)
        BottomBarIconWithLabelOverlay(R.drawable.ic_my_activity, "내 활동", 282.dp, 744.dp)

        Text(
            text = "✕",
            color = Color.White,
            style = typography.body_SB_16,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .clickable { onDismiss() }
        )
    }
}

@Composable
fun DotWithArrowUp(startX: Dp, startY: Dp, length: Dp) {
    Canvas(
        modifier = Modifier.offset(x = startX, y = startY).width(1.dp).height(length)
    ) {
        drawCircle(Color.White, 4.dp.toPx(), Offset(0.5f, size.height))
        drawLine(
            color = Color.White,
            start = Offset(0.5f, size.height - 4.dp.toPx()),
            end = Offset(0.5f, 0f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
        )
    }
}

@Composable
fun DotWithArrow(startX: Dp, startY: Dp, length: Dp) {
    Canvas(
        modifier = Modifier.offset(x = startX, y = startY).width(1.dp).height(length)
    ) {
        drawCircle(Color.White, 4.dp.toPx(), Offset(0.5f, 0f))
        drawLine(
            color = Color.White,
            start = Offset(0.5f, 4.dp.toPx()),
            end = Offset(0.5f, size.height),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
        )
    }
}

@Composable
fun DotWithBentArrow(startX: Dp, startY: Dp, verticalLength: Dp, horizontalLength: Dp) {
    Canvas(
        modifier = Modifier.offset(x = startX, y = startY).width(horizontalLength + 4.dp).height(verticalLength + 4.dp)
    ) {
        val dotRadius = 4.dp.toPx()
        val strokeWidth = 2.dp.toPx()
        drawCircle(Color.White, dotRadius, Offset(0.5f, 0f))
        drawLine(Color.White, Offset(0.5f, dotRadius), Offset(0.5f, verticalLength.toPx()), strokeWidth, StrokeCap.Round, PathEffect.dashPathEffect(floatArrayOf(12f, 12f)))
        drawLine(Color.White, Offset(0.5f, verticalLength.toPx()), Offset(horizontalLength.toPx(), verticalLength.toPx()), strokeWidth, StrokeCap.Round, PathEffect.dashPathEffect(floatArrayOf(12f, 12f)))
    }
}

@Composable
fun SimpleBottomTailBalloon(text: String, offsetX: Dp, offsetY: Dp, balloonWidth: Dp = 138.dp, balloonHeight: Dp = 50.dp) {
    val tailHeight = 8.dp
    val tailWidth = 12.dp
    Box(
        modifier = Modifier.offset(x = offsetX, y = offsetY).wrapContentSize()
    ) {
        Canvas(
            modifier = Modifier.size(width = balloonWidth, height = balloonHeight + tailHeight)
        ) {
            val cornerRadius = 10.dp.toPx()
            val strokeWidth = 1.dp.toPx()
            val tailHeightPx = tailHeight.toPx()
            val tailWidthPx = tailWidth.toPx()
            val balloonRight = size.width
            val balloonBottom = size.height - tailHeightPx
            val tailTipX = size.width - 16.dp.toPx()
            val tailStartX = tailTipX - tailWidthPx / 2
            val tailEndX = tailTipX + tailWidthPx / 2
            val tailTipY = size.height
            val path = Path().apply {
                addRoundRect(RoundRect(0f, 0f, balloonRight, balloonBottom, CornerRadius(cornerRadius)))
                moveTo(tailStartX, balloonBottom)
                lineTo(tailTipX, tailTipY)
                lineTo(tailEndX, balloonBottom)
                close()
            }
            drawPath(path, Color(0xffF3FFD9))
            drawPath(path, Color(0xffF3FFD9), style = Stroke(width = strokeWidth))
        }
        Box(
            modifier = Modifier.size(balloonWidth, balloonHeight).padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text, style = typography.body_SB_16, color = Color(0xFF7AB300))
        }
    }
}

@Composable
fun BottomBarIconWithLabelOverlay(iconResId: Int, label: String, offsetX: Dp, offsetY: Dp) {
    Box(
        modifier = Modifier.offset(x = offsetX, y = offsetY).wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            modifier = Modifier.width(16.dp).height(17.5.dp),
            tint = Color.White
        )
        Text(
            text = label,
            color = Color.White,
            modifier = Modifier.offset(y = 24.dp),
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun HomeTutorialDecorationPreview() {
    HomeTutorialDecoration(
        onDismiss = {},
        onFabClick = {}
    )
}

