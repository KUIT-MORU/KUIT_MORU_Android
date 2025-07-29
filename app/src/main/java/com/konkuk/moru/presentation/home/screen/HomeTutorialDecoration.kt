package com.konkuk.moru.presentation.home.component

import android.R.attr.centerX
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import java.nio.file.WatchEvent

@Composable
fun HomeTutorialDecoration(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onFabClick: () -> Unit,
    bottomIconCenters:List<Offset>
) {
    Box(modifier = modifier.fillMaxSize()) {
        // 정기 루틴 확인하기
        TutorialHintWithLine(
            text = "정기 루틴 확인하기",
            textOffsetDp = 63.dp to 368.dp,
            lineOffsetDp = 53.dp to 319.dp,
            lineLength = 61.dp,
            isUpward = true
        )

        // 루틴 생성하기
        TutorialHintWithLine(
            text = "루틴 생성하기",
            textOffsetDp = 274.dp to 507.dp,
            lineOffsetDp = 313.dp to 532.dp,
            lineLength = 109.dp,
            isUpward = false
        )

        // 루틴 둘러보기
        TutorialHintWithLine(
            text = "루틴 둘러보기",
            textOffsetDp = 50.dp to 672.dp,
            lineOffsetDp = 137.dp to 681.dp,
            lineLength = 61.dp,
            isUpward = false
        )

        // 루틴 관리하기
        TutorialHintWithLine(
            text = "루틴 관리하기",
            textOffsetDp = 183.dp to 656.dp,
            lineOffsetDp = 223.dp to 681.dp,
            lineLength = 64.dp,
            isUpward = false
        )

        // 루틴 인사이트 보기 (꺾인 화살표)
        DotWithBentArrow(
            startX = 273.dp,
            startY = 591.dp,
            verticalLength = 166.dp,
            horizontalLength = 28.dp
        )
        Text(
            text = "루틴 인사이트 보기",
            style = typography.time_R_14.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = modifier.offset(x = 159.dp, y = 582.dp)
        )

        // Balloon
        SimpleBottomTailBalloon(
            image = R.drawable.right_arrow_green,
            text = "바로 생성하기!", offsetX = 213.dp,
            offsetY = 451.dp
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
                .padding(top = 16.dp, end = 16.dp)
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
    startY: Dp, // 이 startY를 Canvas의 좌상단 Y로 사용
    length: Dp, // 선의 길이
    isUpward: Boolean, // 선이 위로 뻗어 나가는지 (true) 아래로 뻗어 나가는지 (false)
    dotRadius: Dp = 3.dp, // 점의 반지름
    lineStrokeWidth: Dp = 1.dp // 선의 두께
) {
    val density = LocalDensity.current

    val dotRadiusPx = density.run { dotRadius.toPx() }
    val lineStrokeWidthPx = density.run { lineStrokeWidth.toPx() }
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))

    // Canvas의 총 높이: 선의 길이 + 점의 지름 (점이 선의 한쪽 끝에 있으므로)
    // Canvas의 총 너비: 점의 지름 (가로로 점 중심을 기준으로 할 것이므로)
    val canvasWidth = dotRadius * 2
    val canvasHeight = length + (dotRadius * 2)

    Canvas(
        modifier = Modifier
            .offset(x = startX, y = startY) // Figma 좌표가 Canvas의 좌상단이라고 가정
            .width(canvasWidth)
            .height(canvasHeight)
    ) {
        // Canvas 내에서 그리기 위한 X 좌표 (Canvas 중앙)
        val drawCenterX = size.width / 2f

        val dotCenterYInCanvas: Float // Canvas 내에서 점의 중심 Y 좌표
        val lineStartYInCanvas: Float // Canvas 내에서 선의 시작 Y 좌표
        val lineEndYInCanvas: Float // Canvas 내에서 선의 끝 Y 좌표

        if (isUpward) {
            // 선이 위로 뻗어 나갈 때 (이미지처럼 점이 아래에, 선이 위로)
            // 점은 Canvas의 맨 아래쪽에 위치
            dotCenterYInCanvas = size.height - dotRadiusPx
            lineStartYInCanvas = dotCenterYInCanvas - dotRadiusPx // 선은 점의 맨 위에서 시작
            lineEndYInCanvas = 0f // 선의 끝은 Canvas 맨 위 (size.height - (length + dotRadius*2) )
        } else {
            // 선이 아래로 뻗어 나갈 때 (점이 위에, 선이 아래로)
            // 점은 Canvas의 맨 위쪽에 위치
            dotCenterYInCanvas = dotRadiusPx
            lineStartYInCanvas = dotCenterYInCanvas + dotRadiusPx // 선은 점의 맨 아래에서 시작
            lineEndYInCanvas = size.height // 선의 끝은 Canvas 맨 아래
        }

        // 점 그리기
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
    iconOffsetY: Dp = (-2).dp, // 기본값으로 2dp 위로
    textOffsetY: Dp = 0.dp, // 텍스트 Y 보정값
    textOffsetX: Dp = 0.dp // 텍스트 X 보정값 추가
) {
    // 바텀바 NavigationBarItem과 동일한 레이아웃 구조로 완전히 겹치도록 구성
    Column(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .width(itemWidth) // 동적으로 계산된 실제 아이템 너비
            .height(itemHeight), // 바텀바 높이와 동일
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 아이콘 - 보정값 적용
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier
                .width(iconWidth)
                .height(iconHeight)
                .offset(y = iconOffsetY) // 보정값 적용
        )

        // 아이콘과 텍스트 사이 간격 (NavigationBarItem 기본값)
        Spacer(modifier = Modifier.height(7.dp))

        // 텍스트 - NavigationBarItem의 기본 스타일과 맞춤, 보정값 적용
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