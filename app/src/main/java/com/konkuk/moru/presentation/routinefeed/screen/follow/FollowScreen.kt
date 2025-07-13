import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.konkuk.moru.presentation.routinefeed.component.follow.EmptyFollowContent
import com.konkuk.moru.presentation.routinefeed.component.follow.UserItem
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.BasicTopAppBar
import com.konkuk.moru.presentation.routinefeed.data.FollowUser
import com.konkuk.moru.presentation.routinefeed.screen.follow.FollowUiState
import com.konkuk.moru.presentation.routinefeed.screen.follow.FollowViewModel
import com.konkuk.moru.ui.theme.MORUTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * ViewModel과 연결되어 탐색 그래프에서 실제 사용될 Composable
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FollowScreen(
    onBackClick: () -> Unit, viewModel: FollowViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tabs = listOf("팔로워", "팔로잉")
    val pagerState = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()

    FollowScreenContent(
        uiState = uiState,
        tabs = tabs,
        pagerState = pagerState,
        scope = scope,
        onBackClick = onBackClick,
        onFollowClick = viewModel::toggleFollow
    )
}

/**
 * UI 레이아웃을 담당하는 Stateless Composable.
 * FollowScreen과 Preview에서 재사용됩니다.
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FollowScreenContent(
    uiState: FollowUiState,
    tabs: List<String>,
    pagerState: PagerState,
    scope: CoroutineScope,
    onBackClick: () -> Unit,
    onFollowClick: (FollowUser) -> Unit
) {
    Scaffold(
        modifier=Modifier.padding(11.dp),
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
                        modifier=Modifier.height(42.dp),
                        selected = pagerState.currentPage == index,
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

            Spacer(modifier = Modifier.padding(top = 11.dp))

            HorizontalPager(
                state = pagerState, modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> FollowListContent(
                        users = uiState.followers,
                        emptyMessage = "내 팔로워가 없어요.",
                        emptySubMessage = "다른 사람들을 찾아보세요!",
                        onFollowClick = onFollowClick
                    )

                    1 -> FollowListContent(
                        users = uiState.followings,
                        emptyMessage = "내 팔로잉이 없어요.",
                        emptySubMessage = "다른 사람들을 찾아보세요!",
                        onFollowClick = onFollowClick
                    )
                }
            }
        }
    }
}

/**
 * 팔로워/팔로잉 목록을 표시하는 Composable (수정 필요 없음)
 */
@Composable
private fun FollowListContent(
    users: List<FollowUser>,
    emptyMessage: String,
    emptySubMessage: String,
    onFollowClick: (FollowUser) -> Unit
) {
    if (users.isEmpty()) {
        EmptyFollowContent(
            message = emptyMessage, subMessage = emptySubMessage
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = users, key = { it.id }) { user ->
                UserItem(user = user, onFollowClick = onFollowClick)
                Divider(color = Color(0xFFF1F3F5), thickness = 1.dp)
            }
        }
    }
}


/**
 * ViewModel 없이 UI를 테스트하기 위한 Preview.
 * 실제 앱 동작에는 영향을 주지 않습니다.
 */
@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
private fun FollowScreenPreview() {
    // 1. Preview에서 사용할 가상 데이터와 상태를 만듭니다.
    var uiState by remember {
        mutableStateOf(
            FollowUiState(
                followers = listOf(
                    FollowUser(1, "", "Moru_Official", "모루 공식 계정입니다.", true),
                    FollowUser(2, "", "Android_Lover", "안드로이드 개발자", false),
                    FollowUser(3, "", "Compose_Fan", "컴포즈는 쉬워요!", true),
                ), followings = listOf(
                    FollowUser(1, "", "Moru_Official", "모루 공식 계정입니다.", true),
                    FollowUser(3, "", "Compose_Fan", "컴포즈는 쉬워요!", true),
                    FollowUser(4, "", "Kotlin_User", "코틀린 최고", true),
                    FollowUser(5, "", "Jetpack_Guru", "제트팩 전문가", true),
                )
            )
        )
    }
    val tabs = listOf("팔로워", "팔로잉")
    val pagerState = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()

    MORUTheme {
        // 2. Stateless Composable인 FollowScreenContent를 호출합니다.
        FollowScreenContent(
            uiState = uiState,
            tabs = tabs,
            pagerState = pagerState,
            scope = scope,
            onBackClick = {},
            // 3. Preview 내에서만 동작하는 클릭 로직을 구현합니다.
            onFollowClick = { clickedUser ->
                val isCurrentlyFollowing = clickedUser.isFollowing
                val followers = uiState.followers.toMutableList()
                val followings = uiState.followings.toMutableList()

                if (isCurrentlyFollowing) { // 언팔로우
                    followings.removeIf { it.id == clickedUser.id }
                    val followerIndex = followers.indexOfFirst { it.id == clickedUser.id }
                    if (followerIndex != -1) {
                        followers[followerIndex] =
                            followers[followerIndex].copy(isFollowing = false)
                    }
                } else { // 팔로우
                    if (followings.none { it.id == clickedUser.id }) {
                        followings.add(0, clickedUser.copy(isFollowing = true))
                    }
                    val followerIndex = followers.indexOfFirst { it.id == clickedUser.id }
                    if (followerIndex != -1) {
                        followers[followerIndex] = followers[followerIndex].copy(isFollowing = true)
                    }
                }
                // Preview의 상태를 업데이트하여 UI 변경을 확인합니다.
                uiState = uiState.copy(followers = followers, followings = followings)
            })
    }
}