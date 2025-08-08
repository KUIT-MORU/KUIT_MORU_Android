package com.konkuk.moru.presentation.routinefeed.component.follow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.BasicTopAppBar
import com.konkuk.moru.presentation.routinefeed.data.FollowUser
import com.konkuk.moru.presentation.routinefeed.screen.follow.FollowUiState
import com.konkuk.moru.ui.theme.MORUTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FollowScreenContent(
    uiState: FollowUiState,
    tabs: List<String>,
    pagerState: PagerState,
    scope: CoroutineScope,
    onBackClick: () -> Unit,
    onFollowClick: (FollowUser) -> Unit,
    onUserClick: (String) -> Unit
) {
    Scaffold(
        modifier = Modifier.padding(11.dp),
        containerColor = Color.White,
        topBar = {
            BasicTopAppBar(
                title = "사용자명", // TODO: 이 부분도 ViewModel의 uiState에서 가져오도록 수정 가능
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "뒤로가기"
                        )
                    }
                })
        }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.White,
                contentColor = Color.Black,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = Color.Black
                    )
                }) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        modifier = Modifier.height(42.dp),
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = title) },
                        selectedContentColor = Color.Black,
                        unselectedContentColor = MORUTheme.colors.mediumGray
                    )
                }
            }

            Spacer(modifier = Modifier.padding(top = 11.dp))

            HorizontalPager(
                state = pagerState, modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> FollowListContent(
                        users = uiState.followers,
                        emptyMessage = "내 팔로워가 없어요.",
                        emptySubMessage = "다른 사람들을 찾아보세요!",
                        onFollowClick = onFollowClick,
                        onUserClick = onUserClick
                    )

                    1 -> FollowListContent(
                        users = uiState.followings,
                        emptyMessage = "내 팔로잉이 없어요.",
                        emptySubMessage = "다른 사람들을 찾아보세요!",
                        onFollowClick = onFollowClick,
                        onUserClick = onUserClick
                    )
                }
            }
        }
    }
}