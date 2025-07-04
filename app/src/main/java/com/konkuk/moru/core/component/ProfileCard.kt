package com.konkuk.moru.core.component


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme

@Composable
fun ProfileCard(
    modifier: Modifier = Modifier,
    username: String,
    tag: String
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFFFFFFFF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 회색 원형 배경
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(MORUTheme.colors.veryLightGray, CircleShape) // 연한 회색 원
                .clip(CircleShape),
            contentAlignment = Alignment.Center // 아이콘을 중앙에 배치
        ) {
            // 프로필 아이콘
            Image(
                painter = painterResource(R.drawable.ic_profile), // 실제 아이콘 리소스로 변경
                contentDescription = "User Profile",
                modifier = Modifier.size(32.dp), // 아이콘 크기 조절
                contentScale = ContentScale.Fit
            )
        }
        Text(text = username, fontSize = 16.sp)
        Text(text = tag, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
@Preview(showBackground = true)
fun ProfileCardScreen() {
    ProfileCard(username = "Jeong", tag = "#운동하자")
}