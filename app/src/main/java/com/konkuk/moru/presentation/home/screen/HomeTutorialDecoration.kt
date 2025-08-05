package com.konkuk.moru.presentation.home.component

import android.R.attr.top
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun HomeTutorialDecoration(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onFabClick: () -> Unit,
    bottomIconCenters:List<Offset>
) {
    Box(modifier = modifier.fillMaxSize()) {
        // 정기 루틴 확인하기
        TutorialHintWithLineByRatio(
            text = "정기 루틴 확인하기",
            textOffsetRatio = 63f / 360f to 368f / 800f,
            lineOffsetRatio = 53f / 360f to 319f / 800f,
            lineLengthRatio = 61f / 800f,
            isUpward = true
        )

        // 루틴 생성하기
        TutorialHintWithLineByRatio(
            text = "루틴 생성하기",
            textOffsetRatio = 274f / 360f to 507f / 800f,
            lineOffsetRatio = 313f / 360f to 532f / 800f,
            lineLengthRatio = 0.060f,
            isUpward = false
        )

        // 루틴 둘러보기
        TutorialHintWithLineByRatio(
            text = "루틴 둘러보기",
            textOffsetRatio = 50f / 360f to 672f / 800f,
            lineOffsetRatio = 132f / 360f to 681f / 800f,
            lineLengthRatio = 0.050f,
            isUpward = false
        )

        // 루틴 관리하기
        TutorialHintWithLineByRatio(
            text = "루틴 관리하기",
            textOffsetRatio = 183f / 360f to 656f / 800f,
            lineOffsetRatio = 223f / 360f to 681f / 800f,
            lineLengthRatio = 0.050f,
            isUpward = false
        )

        // 루틴 인사이트 보기 (꺾인 화살표)
        DotWithBentArrowByRatio(
            startOffsetRatio = 273f / 360f to 591f / 800f,
            verticalLengthRatio = 0.19f,
            horizontalLengthRatio = 0.1f
        )

        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp

        Text(
            text = "루틴 인사이트 보기",
            style = typography.time_R_14.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier
                .offset(
                    x = screenWidth * (159f / 360f),
                    y = screenHeight * (582f / 800f)
                )
        )

        // 말풍선
        SimpleBottomTailBalloonByRatio(
            image = R.drawable.right_arrow_green,
            text = "바로 생성하기!",
            offsetRatioX = 213f / 360f,
            offsetRatioY = 440f / 800f
        )

        // FAB 터치 클릭 영역
        Box(
            modifier = modifier
                .offset(x = 281.dp, y = 641.dp)
                .size(63.dp)
                .clickable { onFabClick() }
        )
        // BottomBar 아이콘 오버레이 (2,3,4번째)
        BottomOverlayBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            iconCenters = bottomIconCenters
        )


        // 닫기 버튼
        Icon(
            painter = painterResource(id = R.drawable.ic_x),
            contentDescription = "닫기",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 21.dp, end = 17.dp)
                .size(14.dp)
                .clickable { onDismiss() }
        )
    }
}

@Composable
fun TutorialHintWithLine(
    text: String,
    textOffsetDp: Pair<Dp, Dp>,
    lineOffsetDp: Pair<Dp, Dp>,
    lineLength: Dp,
    isUpward: Boolean
) {
    val density = LocalDensity.current
    val textOffset = with(density) { Offset(textOffsetDp.first.toPx(), textOffsetDp.second.toPx()) }
    val lineOffset = with(density) { Offset(lineOffsetDp.first.toPx(), lineOffsetDp.second.toPx()) }

    DotWithLine(
        startX = lineOffsetDp.first,
        startY = lineOffsetDp.second,
        length = lineLength,
        isUpward = isUpward
    )
    Text(
        text = text,
        style = typography.time_R_14.copy(fontWeight = FontWeight.Bold),
        color = Color.White,
        modifier = Modifier.offset(x = textOffsetDp.first, y = textOffsetDp.second)
    )
}



@Composable
fun DotWithLine(
    startX: Dp,
    startY: Dp,
    length: Dp,
    isUpward: Boolean,
    dotRadius: Dp = 3.dp,
    lineStrokeWidth: Dp = 1.dp
) {
    val density = LocalDensity.current

    val dotRadiusPx = density.run { dotRadius.toPx() }
    val lineStrokeWidthPx = density.run { lineStrokeWidth.toPx() }
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))

    val canvasWidth = dotRadius * 2
    val canvasHeight = length + (dotRadius * 2)

    Canvas(
        modifier = Modifier
            .offset(x = startX, y = startY)
            .width(canvasWidth)
            .height(canvasHeight)
    ) {
        val drawCenterX = size.width / 2f

        val dotCenterYInCanvas: Float
        val lineStartYInCanvas: Float
        val lineEndYInCanvas: Float

        if (isUpward) {
            dotCenterYInCanvas = size.height - dotRadiusPx
            lineStartYInCanvas = dotCenterYInCanvas - dotRadiusPx
            lineEndYInCanvas = 0f
        } else {
            dotCenterYInCanvas = dotRadiusPx
            lineStartYInCanvas = dotCenterYInCanvas + dotRadiusPx
            lineEndYInCanvas = size.height
        }

        // 점(원) 그리기
        drawCircle(
            color = Color.White,
            radius = dotRadiusPx,
            center = Offset(drawCenterX, dotCenterYInCanvas)
        )

        // 선 그리기
        drawLine(
            color = Color.White,
            start = Offset(drawCenterX, lineStartYInCanvas),
            end = Offset(drawCenterX, lineEndYInCanvas),
            strokeWidth = lineStrokeWidthPx,
            cap = StrokeCap.Round,
            pathEffect = dashEffect
        )
    }
}

@Composable
fun DotWithBentArrow(startX: Dp, startY: Dp, verticalLength: Dp, horizontalLength: Dp) {
    Canvas(
        modifier = Modifier
            .offset(x = startX, y = startY)
            .width(horizontalLength + 4.dp)
            .height(verticalLength + 4.dp)
    ) {
        val dotRadius = 3.dp.toPx()
        val strokeWidth = 1.dp.toPx()
        drawCircle(Color.White, dotRadius, Offset(0.5f, 0f))
        drawLine(
            Color.White,
            Offset(0.5f, dotRadius),
            Offset(0.5f, verticalLength.toPx()),
            strokeWidth,
            StrokeCap.Round,
            PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
        )
        drawLine(
            Color.White,
            Offset(0.5f, verticalLength.toPx()),
            Offset(horizontalLength.toPx(), verticalLength.toPx()),
            strokeWidth,
            StrokeCap.Round,
            PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
        )
    }
}

@Composable
fun SimpleBottomTailBalloon(
    text: String,
    image:Int,
    offsetX: Dp,
    offsetY: Dp,
    balloonWidth: Dp = 138.dp,
    balloonHeight: Dp = 42.dp
) {
    //올리브 그린 색 미리 빼기(colors.oliveGreen 은 MORUTheme.colors 에 들어 있는 CompositionLocal 값이라)
    val borderColor = colors.oliveGreen
    val strokeWidthDp = 2.dp
    val tailHeight = 8.dp
    val tailWidth = 12.dp
    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .wrapContentSize()
    ) {
        Canvas(
            modifier = Modifier.size(width = balloonWidth, height = balloonHeight + tailHeight)
        ) {
            val strokePx     = strokeWidthDp.toPx()
            val cornerRadius = 10.dp.toPx()
            val tailHeightPx = tailHeight.toPx()
            val tailWidthPx  = tailWidth.toPx()

            val balloonBottom = size.height - tailHeightPx
            val tailTipX      = size.width - 16.dp.toPx()
            val tailStartX    = tailTipX - tailWidthPx / 2
            val tailEndX      = tailTipX + tailWidthPx / 2

            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        0f, 0f,
                        size.width, balloonBottom,
                        CornerRadius(cornerRadius)
                    )
                )
                moveTo(tailStartX, balloonBottom)
                lineTo(tailTipX, size.height)
                lineTo(tailEndX, balloonBottom)
                close()
            }

            /* ① 테두리를 먼저 그린 뒤 → ② 안쪽을 채운다 */
            drawPath(path, borderColor, style = Stroke(width = strokePx))
            drawPath(path, Color(0xFFF3FFD9))
        }

        val inner = strokeWidthDp
        Box(
            modifier = Modifier
                .offset(inner, inner)
                .size(balloonWidth  - inner * 2, balloonHeight - inner * 2)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Spacer(modifier = Modifier.width(5.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text, style = typography.body_SB_16, color = colors.oliveGreen)
                Spacer(modifier = Modifier.width(11.dp))
                Image(
                    painter = painterResource(id=image),
                    contentDescription = "초록색 오른쪽 화살표",
                    modifier = Modifier
                        .size(width = 14.dp, height = 18.dp),
                )
            }
        }
    }
}

@Composable
fun BottomBarIconWithLabelOverlay(
    iconResId: Int,
    label: String,
    offsetX: Dp,
    offsetY: Dp,
    itemWidth: Dp,
    itemHeight: Dp,
    iconWidth: Dp = 16.dp,
    iconHeight: Dp = 17.5.dp,
    // 기기별 보정값 추가
    iconOffsetY: Dp = (-2).dp,
    textOffsetY: Dp = 0.dp,
    textOffsetX: Dp = 0.dp
) {
    // 바텀바 NavigationBarItem과 동일한 레이아웃 구조로 완전히 겹치도록 구성
    Column(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .width(itemWidth)
            .height(itemHeight),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier
                .width(iconWidth)
                .height(iconHeight)
                .offset(y = iconOffsetY)
        )

        // 아이콘과 텍스트 사이 간격 (NavigationBarItem 기본값)
        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = label,
            style = typography.title_B_12,
            color = Color.White,
            modifier = Modifier.offset(x = textOffsetX, y = textOffsetY) // X, Y 보정값 둘 다 적용
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0x80000000,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun HomeTutorialDecorationPreview() {
    val fakeCenters = listOf(
        Offset(0f, 0f),        // 홈 아이콘 (사용 안함)
        Offset(90f, 700f),     // 루틴 피드
        Offset(180f, 700f),    // 내 루틴
        Offset(270f, 700f)     // 내 활동
    )

    HomeTutorialDecoration(
        onDismiss = {},
        onFabClick = {},
        bottomIconCenters = fakeCenters
    )
}