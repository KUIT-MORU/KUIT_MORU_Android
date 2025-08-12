package com.konkuk.moru.presentation.routinefeed.screen


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.konkuk.moru.R
import com.konkuk.moru.presentation.routinefeed.component.notification.NotificationRow
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.BasicTopAppBar
import com.konkuk.moru.presentation.routinefeed.viewmodel.NotificationViewModel
import com.konkuk.moru.ui.theme.MORUTheme
import com.konkuk.moru.ui.theme.moruFontBold
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.items.isEmpty()) {
        if (state.items.isEmpty()) {
            viewModel.loadFirstPage(size = 20)
        }
    }

    // API가 relativeTime만 주므로, relativeTime 기반으로 섹션 그룹핑
    val grouped = remember(state.items) {
        state.items.groupBy { item -> parseRelativeToDayBucket(item.relativeTime) }
        // groupBy는 키의 첫 등장 순서를 유지합니다(리스트가 “최근순”이면 섹션도 그 순서대로).
    }

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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF1F3F5),
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                ),
                titleStyle = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = moruFontBold, fontSize = 16.sp, lineHeight = 28.sp
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues( bottom = 80.dp)
        ) {
            // 섹션 헤더 + 아이템 렌더링
            grouped.forEach { (sectionTitle, list) ->
                stickyHeader {
                    SectionHeader(title = sectionTitle)
                }
                items(
                    items = list,
                    key = { it.id }
                ) { item ->
                    NotificationRow(
                        senderId = item.senderId,
                        nickname = item.senderNickname,
                        profileUrl = item.senderProfileImage,
                        message = item.message,
                        // 표시는 서버가 내려준 relativeTime 그대로 사용
                        relativeTime = item.relativeTime,
                        onProfileClick = { uid ->
                            navController.navigate(
                                com.konkuk.moru.presentation.navigation.Route.UserProfile.createRoute(uid)
                            )
                        }
                    )
                }
            }

            if (state.error != null) {
                item { Text("에러: ${state.error}", color = Color.Red) }
            }

            if (state.hasNext || state.isLoadingMore) {
                item {
                    TextButton(
                        onClick = { viewModel.loadAllRemaining(size = 20) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoadingMore
                    ) {
                        Text(
                            text = if (state.isLoadingMore) "모두 불러오는 중..." else "더보기",
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_down),
                            contentDescription = "아래화살표",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/* ===================== 시간 유틸 & 섹션 헤더 ===================== */

// API의 relativeTime 문자열로 “오늘/하루 전/N일 전/오래 전” 버킷을 만듭니다.
private fun parseRelativeToDayBucket(text: String): String {
    val cleaned = text.trim()
    // 흔한 표현들: "방금 전", "30초 전", "5분 전", "2시간 전", "어제", "하루 전", "2일 전", "3일 전", ...
    if (cleaned.contains("방금")) return "오늘"
    if (cleaned.contains("어제")) return "하루 전" // 혹시 서버가 "어제"로 주는 케이스
    if (cleaned.endsWith("초 전") || cleaned.endsWith("분 전") || cleaned.endsWith("시간 전")) return "오늘"

    val day = Regex("""(\d+)\s*일 전""").find(cleaned)?.groupValues?.getOrNull(1)?.toIntOrNull()
    return when (day) {
        null, 0 -> "오늘"
        1 -> "하루 전"
        2 -> "이틀 전"
        in 3..6 -> "${day}일 전"
        else -> "오래 전"
    }
}

// 필요 시 LocalDateTime → 상대표시 포맷(현재는 API 문자열을 그대로 쓰므로 미사용)
private fun formatTimestamp(timestamp: LocalDateTime): String {
    val now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    val duration = Duration.between(timestamp, now)
    val minutes = duration.toMinutes()
    return when {
        minutes < 1 -> "방금 전"
        minutes < 60 -> "${minutes}분 전"
        minutes < 60 * 24 -> "${duration.toHours()}시간 전"
        minutes < 60 * 24 * 7 -> "${duration.toDays()}일 전"
        else -> timestamp.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    }
}

// 섹션 헤더
@Composable
private fun SectionHeader(title: String) {
    Surface(
        color = Color.White,
        contentColor = Color.Black,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

/* ===================== 전체 프리뷰 (섹션 포함) ===================== */

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun NotificationScreenGroupedPreview() {
    MORUTheme {
        Surface {
            // API 응답 형태에 맞춘 더미 아이템
            data class Item(
                val id: String,
                val senderId: String,
                val senderNickname: String,
                val senderProfileImage: String?,
                val message: String,
                val relativeTime: String
            )

            val items = listOf(
                Item("1", "u1", "MORU", null, "MORU님이 ‘아침 조깅’ 루틴을 좋아합니다.", "5분 전"),
                Item("2", "u2", "Alice", null, "Alice님이 회원님을 팔로우하기 시작했습니다.", "2시간 전"),
                Item("3", "u3", "Bob", null, "Bob님이 ‘영어 공부’ 루틴을 스크랩했습니다.", "하루 전"),
                Item("4", "u4", "Chris", null, "Chris님이 ‘코딩’ 루틴을 좋아합니다.", "3일 전"),
                Item("5", "u5", "Dana", null, "Dana님이 ‘물마시기’ 루틴을 좋아합니다.", "30분 전")
            )

            val grouped = items.groupBy { parseRelativeToDayBucket(it.relativeTime) }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                grouped.forEach { (sectionTitle, list) ->
                    stickyHeader { SectionHeader(title = sectionTitle) }
                    items(list, key = { it.id }) { item ->
                        NotificationRow(
                            senderId = item.senderId,
                            nickname = item.senderNickname,
                            profileUrl = item.senderProfileImage,
                            message = item.message,
                            relativeTime = item.relativeTime,
                            onProfileClick = {}
                        )
                    }
                }
            }
        }
    }
}