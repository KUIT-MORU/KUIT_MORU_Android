package com.konkuk.moru.presentation.routinefocus.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.konkuk.moru.R
import com.konkuk.moru.data.model.AppInfo
import com.konkuk.moru.ui.theme.MORUTheme
import com.konkuk.moru.ui.theme.MORUTheme.colors

@Composable
fun ScreenBlockOverlay(
    selectedApps: List<AppInfo> = emptyList(),
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    // 어두운 회색 배경에 연두색 테두리 말풍선
    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(1000f)
            .background(colors.black50Oopacity)
            .clickable { 
                onDismiss()
            }
    ) {
        // 앱 아이콘들을 하단에 배치 (집중 화면과 동일한 위치)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .offset(y = (-140).dp) // 앱 아이콘들을 더 위로 배치
                .zIndex(1001f), // 오버레이보다 위에 표시
            horizontalAlignment = Alignment.Start
        ) {
            // 말풍선을 앱 아이콘들 바로 위에 배치
            Box(
                modifier = Modifier
                    .offset(y = 10.dp) // 말풍선을 앱 아이콘 바로 위에 배치
                    .padding(horizontal = 8.dp) // 좌우 여백을 더 좁게
            ) {
                DarkBalloonWithTail(
                    text = "설정한 앱만 사용할 수 있어요!",
                    balloonWidth = 200.dp,
                    balloonHeight = 40.dp
                )
            }
            
            // 앱 아이콘들
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .background(Color.Transparent)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                selectedApps.forEach { app ->
                    AppIconItem(app = app)
                }
                // 기본 아이콘들 (선택된 앱이 3개 미만인 경우)
                repeat(3 - selectedApps.size) {
                    AppIconItem()
                }
                }
            }
        }
    }
}

@Composable
fun DarkBalloonWithTail(
    text: String,
    balloonWidth: Dp = 280.dp,
    balloonHeight: Dp = 60.dp
) {
    val density = LocalDensity.current
    val colors = MORUTheme.colors
    val borderColor = colors.limeGreen // limeGreen 테두리
    val backgroundColor = Color(0xFF3C3C3C) // 어두운 회색 배경
    val textColor = colors.limeGreen // limeGreen 텍스트
    val strokeWidthDp = 2.dp
    val tailHeight = 12.dp
    val tailWidth = 16.dp

    Box(
        modifier = Modifier.wrapContentSize()
    ) {
        Canvas(
            modifier = Modifier.size(width = balloonWidth, height = balloonHeight + tailHeight)
        ) {
            val strokePx = with(density) { strokeWidthDp.toPx() }
            val cornerRadius = with(density) { 10.dp.toPx() }
            val tailHeightPx = with(density) { tailHeight.toPx() }
            val tailWidthPx = with(density) { tailWidth.toPx() }

            val balloonBottom = size.height - tailHeightPx
            val tailTipX = size.width * 0.15f // 더 왼쪽으로 꼬리 위치 (15% 지점)
            val tailStartX = tailTipX - tailWidthPx / 2
            val tailEndX = tailTipX + tailWidthPx / 2

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

            // 테두리를 먼저 그린 뒤 → 안쪽을 채운다
            drawPath(path, borderColor, style = Stroke(width = strokePx))
            drawPath(path, backgroundColor)
        }

        Box(
            modifier = Modifier
                .size(balloonWidth, balloonHeight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AppIconItem(app: AppInfo? = null) {
    Image(
        painter = painterResource(id = R.drawable.ic_default),
        contentDescription = app?.name?.let { "${it} 아이콘" } ?: "앱 아이콘",
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(6.dp))
    )
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
fun ScreenBlockOverlayPreview() {
    // 더미 앱 데이터 생성
    val dummyApps = listOf(
        AppInfo(
            name = "YouTube",
            iconUrl = null,
            packageName = "com.google.android.youtube"
        ),
        AppInfo(
            name = "Instagram",
            iconUrl = null,
            packageName = "com.instagram.android"
        ),
        AppInfo(
            name = "KakaoTalk",
            iconUrl = null,
            packageName = "com.kakao.talk"
        )
    )
    
    ScreenBlockOverlay(selectedApps = dummyApps)
}
