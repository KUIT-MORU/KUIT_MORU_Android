package com.konkuk.moru.presentation.routinefeed.screen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
                            text = if (state.isLoadingMore) "모두 불러오는 중..." else "더보기(끝까지)",
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
private fun NotificationRow(
    senderId: String,
    nickname: String,
    profileUrl: String?,
    message: String,
    relativeTime: String,
    onProfileClick: (String) -> Unit
) {
    val context = LocalContext.current // ★ ADDED

    androidx.compose.foundation.layout.Row(
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(profileUrl)              // 서버에서 넘어온 이미지 URL
                .crossfade(true)
                .build(),
            contentDescription = "profile",
            placeholder = painterResource(R.drawable.ic_profile_with_background), // 로딩 중
            error = painterResource(R.drawable.ic_profile_with_background),       // 실패 시
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)              // 동그란 아바타
                .padding(end = 12.dp)
                .clickable { onProfileClick(senderId) } // 프로필 클릭 이동
        )

        androidx.compose.foundation.layout.Column(Modifier.weight(1f)) {
            Text(
                text = nickname,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onProfileClick(senderId) } // ★ 유지
            )
            Text(text = message, color = Color.DarkGray)
            Text(text = relativeTime, color = Color.Gray)
        }
    }
}
/*@Preview(showBackground = true)
@Composable
private fun NotificationScreenPreview() {
    MaterialTheme {
        NotificationScreen(navController =rememberNavController(), onNavigateBack = {})
    }
}*/
