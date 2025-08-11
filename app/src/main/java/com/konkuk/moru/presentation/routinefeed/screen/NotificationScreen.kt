package com.konkuk.moru.presentation.routinefeed.screen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.konkuk.moru.R
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.BasicTopAppBar
import com.konkuk.moru.presentation.routinefeed.viewmodel.NotificationViewModel
import com.konkuk.moru.ui.theme.MORUTheme

@OptIn(ExperimentalMaterial3Api::class)
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
                    fontSize = 16.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp)
        ) {
            items(
                count = state.items.size,
                key = { index -> state.items[index].id }
            ) { index ->
                val item = state.items[index]
                NotificationRow(
                    senderId = item.senderId,                         // ★ CHANGED
                    nickname = item.senderNickname,
                    profileUrl = item.senderProfileImage,
                    message = item.message,
                    relativeTime = item.relativeTime,
                    onProfileClick = { uid ->                          // ★ ADDED
                        navController.navigate(
                            com.konkuk.moru.presentation.navigation.Route.UserProfile.createRoute(
                                uid
                            )
                        )
                    }
                )
            }

            // 로딩/에러/더보기 처리
            // 로딩/에러/더보기(= 끝까지)
            if (state.error != null) {
                item { Text("에러: ${state.error}", color = Color.Red) }
            }

            if (state.hasNext || state.isLoadingMore) { // ★ CHANGED: 로딩 중에도 버튼 영역 유지
                item {
                    TextButton(
                        onClick = { viewModel.loadAllRemaining(size = 20) }, // ★ CHANGED
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoadingMore // ★ ADDED: 중복 클릭 방지
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

@Composable
fun NotificationRow(
    senderId: String,
    nickname: String,
    profileUrl: String?,
    message: String,
    relativeTime: String,
    onProfileClick: (userId: String) -> Unit
) {
    // 1. 서버에서 받은 전체 메시지(message)에서 닉네임(nickname) 부분만 강조 처리
    val annotatedMessage = buildAnnotatedString {
        append(message)
        val startIndex = message.indexOf(nickname)
        if (startIndex != -1) {
            addStyle(
                style = SpanStyle(fontWeight = FontWeight.Bold),
                start = startIndex,
                end = startIndex + nickname.length
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Row 전체를 클릭 가능하게 만들고 프로필 화면으로 이동시킵니다.
            .clickable { onProfileClick(senderId) }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 2. Coil의 AsyncImage를 사용하여 프로필 URL 로드
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(profileUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_profile_with_background), // 로딩 중 이미지
            error = painterResource(R.drawable.ic_profile_with_background),       // 에러 시 이미지
            contentDescription = "프로필 이미지",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 3. 위에서 만든 강조된 메시지를 Text에 적용
        Text(
            text = annotatedMessage,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 4. 서버에서 받은 상대 시간(relativeTime)을 그대로 사용
        Text(
            text = relativeTime,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationRowCombinedPreview() {
    MORUTheme {
        Surface {
            NotificationRow(
                senderId = "user-123",
                nickname = "MORU",
                profileUrl = null, // 프리뷰에서는 이미지 URL이 없다고 가정
                message = "MORU님이 회원님의 '아침 조깅' 루틴을 좋아합니다.",
                relativeTime = "2시간 전",
                onProfileClick = { userId ->
                    println("Profile clicked: $userId")
                }
            )
        }
    }
}
