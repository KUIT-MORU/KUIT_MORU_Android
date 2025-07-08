package com.konkuk.moru.presentation.routinefeed.screen.follow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.presentation.routinefeed.component.follow.EmptyFollowContent
import com.konkuk.moru.presentation.routinefeed.component.follow.UserItem
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.BasicTopAppBar
import com.konkuk.moru.presentation.routinefeed.data.FollowUser
import com.konkuk.moru.ui.theme.MORUTheme
import kotlinx.coroutines.launch

// HorizontalPager가 아직 실험용(Experimental) API이므로 OptIn이 필요합니다.
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FollowScreen(
    onBackClick: () -> Unit
) {
    val tabs = listOf("팔로워", "팔로잉")
    // 1. PagerState를 생성합니다. 탭과 페이지 상태를 모두 관리하는 '단일 공급원' 역할을 합니다.
    val pagerState = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()

    /*val followers = remember { mutableStateListOf<FollowUser>() }   // ★ 일부러 비워 둔 프리뷰용
    val followings = remember { mutableStateListOf<FollowUser>() }*/


    // 임시 데이터 (이 부분은 동일합니다)
    val followers = remember {
        mutableStateListOf(
            FollowUser(1, "", "Moru_Official", "모루 공식 계정입니다.", true),
            FollowUser(2, "", "Android_Lover", "안드로이드 개발자", false),
            FollowUser(3, "", "Compose_Fan", "컴포즈는 쉬워요!", true),
        )
    }
    val followings = remember {
        mutableStateListOf(
            FollowUser(1, "", "Moru_Official", "모루 공식 계정입니다.", true),
            FollowUser(3, "", "Compose_Fan", "컴포즈는 쉬워요!", true),
            FollowUser(4, "", "Kotlin_User", "코틀린 최고", true),
            FollowUser(5, "", "Jetpack_Guru", "제트팩 전문가", true),
        )
    }

    val onFollowClick: (FollowUser) -> Unit = { clickedUser ->
        val updateUser = { list: MutableList<FollowUser>, user: FollowUser ->
            val index = list.indexOfFirst { it.id == user.id }
            if (index != -1) {
                list[index] = user.copy(isFollowing = !user.isFollowing)
            }
        }
        updateUser(followers, clickedUser)
        updateUser(followings, clickedUser)
    }

    Scaffold(
        topBar = {
            BasicTopAppBar(
                title = "사용자명",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(
                // 2. selectedTabIndex를 pagerState에서 가져옵니다.
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.White,
                contentColor = Color.Black,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = Color.Black
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        // 3. 탭 클릭 시 pagerState의 페이지를 변경하도록 요청합니다.
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = title) },
                        selectedContentColor = Color.Black,
                        unselectedContentColor = Color.LightGray
                    )
                }
            }

            Spacer(modifier=Modifier.padding(top=11.dp))

            // 4. when 문 대신 HorizontalPager를 사용합니다.
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                // 페이지 인덱스(page)에 따라 적절한 컨텐츠를 보여줍니다.
                when (page) {
                    0 -> FollowListContent(
                        users = followers,
                        emptyMessage = "내 팔로워가 없어요.",
                        emptySubMessage = "다른 사람들을 찾아보세요!",
                        onFollowClick = onFollowClick
                    )
                    1 -> FollowListContent(
                        users = followings,
                        emptyMessage = "내 팔로잉이 없어요.",
                        emptySubMessage = "다른 사람들을 찾아보세요!",
                        onFollowClick = onFollowClick
                    )
                }
            }
        }
    }
}

// FollowListContent 컴포저블은 수정할 필요 없습니다. (이하 동일)
@Composable
private fun FollowListContent(
    users: List<FollowUser>,
    emptyMessage: String,
    emptySubMessage: String,
    onFollowClick: (FollowUser) -> Unit
) {
    if (users.isEmpty()) {
        EmptyFollowContent(
            message = emptyMessage,
            subMessage = emptySubMessage
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = users,
                key = { it.id }
            ) { user ->
                UserItem(user = user, onFollowClick = onFollowClick)
                Divider(color = Color(0xFFF1F3F5), thickness = 1.dp)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
private fun FollowScreenPreview() {
    MORUTheme {
        FollowScreen(onBackClick = {})
    }
}
