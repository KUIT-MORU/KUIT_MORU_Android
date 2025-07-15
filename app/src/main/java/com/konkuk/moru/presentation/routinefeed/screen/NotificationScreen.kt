package com.konkuk.moru.presentation.routinefeed.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.R
import com.konkuk.moru.presentation.routinefeed.component.notification.NotificationGroup
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.BasicTopAppBar
import com.konkuk.moru.presentation.routinefeed.data.Notification
import java.time.Duration
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit
) {
    val dummyNotifications = listOf(
        Notification(1, null, "xx님", "이 [루틴명]을 생성했습니다.", null, LocalDateTime.now().minusMinutes(2)),
        Notification(2, null, "xx님", "이 [루틴명]을 생성했습니다.", null, LocalDateTime.now().minusMinutes(5)),
        Notification(3, null, "xx님", "님을 팔로우 했습니다.", "[사용자명]", LocalDateTime.now().minusDays(2)),
        Notification(4, null, "xx님", "이 [루틴명]을 생성했습니다.", null, LocalDateTime.now().minusDays(3)),
        Notification(5, null, "xx님", "이 [루틴명]을 생성했습니다.", null, LocalDateTime.now().minusDays(4)),
        Notification(6, null, "xx님", "이 [루틴명]을 생성했습니다.", null, LocalDateTime.now().minusDays(5)),
        Notification(7, null, "xx님", "이 [루틴명]을 생성했습니다.", null, LocalDateTime.now().minusDays(10)),
        Notification(8, null, "xx님", "이 [루틴명]을 생성했습니다.", null, LocalDateTime.now().minusDays(40)),
    )

    val groupedNotifications = dummyNotifications.groupBy {
        val now = LocalDateTime.now()
        when {
            Duration.between(it.timestamp, now).toDays() == 0L -> "오늘"
            Duration.between(it.timestamp, now).toDays() < 7L -> "최근 7일"
            Duration.between(it.timestamp, now).toDays() < 30L -> "최근 30일"
            else -> "오래 전"
        }
    }

    var isScreenExpanded by remember { mutableStateOf(false) }
    val initialCategories = listOf("오늘", "최근 7일")
    val remainingCategories = groupedNotifications.keys.filter { it !in initialCategories }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            BasicTopAppBar(
                title = "알림",
                spacingBetweenIconAndTitle = 27.dp,
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "뒤로가기",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                //modifier = Modifier.padding(start = 27.dp),
                // 4. colors 파라미터로 색상 지정
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF1F3F5),
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                ),
                titleStyle = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 28.sp,// 원하는 글자 크기로 변경
                    fontWeight = FontWeight.Bold // 굵기 등 다른 스타일도 변경 가능
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp,top=0.dp)
        ) {
            // 초기 카테고리 표시
            initialCategories.forEach { category ->
                groupedNotifications[category]?.let { notifications ->
                    item {
                        NotificationGroup(
                            title = category,
                            notifications = notifications,
                            isScreenExpanded = isScreenExpanded // 화면 전체 확장 상태 전달
                        )
                    }
                }
            }

            // '더보기' 버튼
            if (!isScreenExpanded && remainingCategories.isNotEmpty()) {
                item {
                    TextButton(
                        onClick = { isScreenExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "더보기",
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_down),
                            contentDescription = "아래화살표",
                            tint = Color.Unspecified, // 원본 아이콘 색상 사용
                            modifier=Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 확장 시 나머지 카테고리 표시
            if (isScreenExpanded) {
                remainingCategories.forEach { category ->
                    groupedNotifications[category]?.let { notifications ->
                        item {
                            NotificationGroup(
                                title = category,
                                notifications = notifications,
                                isScreenExpanded = isScreenExpanded // 화면 전체 확장 상태 전달
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun NotificationScreenPreview() {
    MaterialTheme {
        NotificationScreen(onNavigateBack = {})
    }
}