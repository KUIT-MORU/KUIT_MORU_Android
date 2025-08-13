package com.konkuk.moru.presentation.routinefocus.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.presentation.routinefocus.screen.AppInfo
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun ScreenBlockPopup(
    selectedApps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onOutsideClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onOutsideClick() }
    ) {
        // 사용앱 영역 (강조 표시)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
                .clickable(enabled = false) { /* 사용앱 영역 클릭 방지 */ },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 설명
            Text(
                text = "설정된 앱만 사용할 수 있습니다",
                style = typography.desc_M_14,
                color = colors.mediumGray,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 앱 아이콘들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                selectedApps.forEach { app ->
                    AppIcon(
                        app = app,
                        onClick = { onAppClick(app) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppIcon(
    app: AppInfo,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 앱 아이콘
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(colors.lightGray),
            contentAlignment = Alignment.Center
        ) {
            // 실제 앱에서는 앱 아이콘을 가져와야 함
            // 여기서는 임시로 기본 아이콘 사용
            Text(
                text = app.appName.take(1),
                style = typography.title_B_14,
                color = colors.black
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 앱 이름
        Text(
            text = app.appName,
            style = typography.desc_M_12,
            color = colors.black,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}
