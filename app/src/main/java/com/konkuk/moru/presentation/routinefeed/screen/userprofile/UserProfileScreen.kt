package com.konkuk.moru.presentation.routinefeed.screen.userprofile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.konkuk.moru.R
import com.konkuk.moru.core.component.button.MoruButton
import com.konkuk.moru.core.component.routine.RoutineListItem
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.BasicTopAppBar
import com.konkuk.moru.presentation.routinefeed.data.UserProfileUiState
import com.konkuk.moru.ui.theme.MORUTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavController,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            BasicTopAppBar(
                title = "사용자명",
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        UserProfileContent(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
            onFollowClick = viewModel::toggleFollow,
            onToggleExpansion = viewModel::toggleRunningRoutineExpansion,
            onLikeClick = viewModel::toggleLike,
            onFollowerClick = { navController.navigate("follow_screen/follower") },
            onFollowingClick = { navController.navigate("follow_screen/following") }
        )
    }
}

@Composable
private fun UserProfileContent(
    modifier: Modifier = Modifier,
    state: UserProfileUiState,
    onFollowClick: () -> Unit,
    onToggleExpansion: () -> Unit,
    onLikeClick: (Int) -> Unit,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        item {
            ProfileHeader(
                state = state,
                onFollowClick = onFollowClick,
                onFollowerClick = onFollowerClick,
                onFollowingClick = onFollowingClick
            )
        }
        item {
            ExpandableRoutineSection(
                isExpanded = state.isRunningRoutineExpanded,
                routines = state.runningRoutines,
                nickname = state.nickname,
                onToggle = onToggleExpansion,
                onLikeClick = onLikeClick
            )
        }
        item {
            Text(
                text = "${state.nickname}님의 루틴",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 17.dp, top = 13.dp)
            )
        }
        if (state.userRoutines.isEmpty()) {
            item {
                EmptyRoutineView(modifier = Modifier.padding(vertical = 93.dp))
            }
        } else {
            items(state.userRoutines, key = { it.id }) { routine ->
                RoutineListItem(
                    isRunning = routine.isRunning,
                    routineName = routine.title,
                    tags = routine.tags,
                    likeCount = routine.likes,
                    isLiked = routine.isLiked,
                    onLikeClick = { onLikeClick(routine.id) },
                    onItemClick = {}
                )
            }
        }
    }
}

// --- [수정] 생략되었던 세부 UI 컴포넌트들 모두 포함 ---

@Composable
private fun ProfileHeader(
    state: UserProfileUiState,
    onFollowClick: () -> Unit,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    val buttonText = if (state.isFollowing) "팔로잉" else "팔로우"
    val backgroundColor = if (state.isFollowing) MORUTheme.colors.veryLightGray else Color.Black
    val contentColor =
        if (state.isFollowing) MORUTheme.colors.mediumGray else MORUTheme.colors.limeGreen

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile_with_background),
                contentDescription = "프로필 사진",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                MoruButton(
                    text = buttonText,
                    onClick = onFollowClick,
                    backgroundColor = backgroundColor,
                    contentColor = contentColor,
                    shape = RoundedCornerShape(140.dp),
                    textStyle = MORUTheme.typography.title_B_14,
                    modifier = Modifier
                        .height(37.dp)
                        .width(88.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileStats(
                    routineCount = state.routineCount,
                    followerCount = state.followerCount,
                    followingCount = state.followingCount,
                    onFollowerClick = onFollowerClick,
                    onFollowingClick = onFollowingClick
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = state.nickname, style = MORUTheme.typography.time_R_16, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = state.bio, fontSize = 14.sp, style = MORUTheme.typography.time_R_14, color = Color.DarkGray)
    }
}

@Composable
private fun ProfileStats(
    routineCount: Int,
    followerCount: Int,
    followingCount: Int,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem("루틴", routineCount)
        StatItem("팔로워", followerCount, onFollowerClick)
        StatItem("팔로잉", followingCount, onFollowingClick)
    }
}

@Composable
private fun StatItem(label: String, count: Int, onClick: (() -> Unit)? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    ) {
        Text(text = count.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
private fun ExpandableRoutineSection(
    isExpanded: Boolean,
    routines: List<Routine>,
    nickname: String,
    onToggle: () -> Unit,
    onLikeClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "펼치기/접기",
                modifier = Modifier.clickable(onClick = onToggle)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$nickname 님의 실행 중인 루틴",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onToggle)
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            if (routines.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person_standing),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.Unspecified
                    )
                    Text("현재 실행중인 루틴이 없습니다.", color = Color.Gray)
                }
            } else {
                Column {
                    routines.forEach { routine ->
                        RoutineListItem(
                            isRunning = routine.isRunning,
                            routineName = routine.title,
                            tags = routine.tags,
                            likeCount = routine.likes,
                            isLiked = routine.isLiked,
                            onLikeClick = { onLikeClick(routine.id) },
                            onItemClick = {}
                        )
                    }
                }
            }
        }
    }
}


// --- 프리뷰 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserProfileScreenPreview(isDataEmpty: Boolean = false) {
    // [수정] 프리뷰용 샘플 데이터를 통합 Routine 모델로 변경
    val sampleRunningRoutines = remember {
        listOf(Routine(1, "아침 운동 1", "", null, "운동", listOf("#테그그그그그", "#tag"), "모루", null, 16, true, false, true))
    }
    val sampleUserRoutines = remember {
        List(5) { index ->
            Routine(index + 2, "아침 운동", "", null, "운동", listOf("#모닝루틴", "#스트레칭"), "모루", null, 16, false, index % 2 == 0, false)
        }
    }

    var isFollowing by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(true) }

    val likedStates = remember {
        val allRoutines = sampleRunningRoutines + sampleUserRoutines
        mutableStateMapOf(*allRoutines.map { it.id to it.isLiked }.toTypedArray())
    }
    val likeCounts = remember {
        val allRoutines = sampleRunningRoutines + sampleUserRoutines
        mutableStateMapOf(*allRoutines.map { it.id to it.likes }.toTypedArray())
    }

    val runningRoutines = sampleRunningRoutines.map {
        it.copy(isLiked = likedStates[it.id] ?: it.isLiked, likes = likeCounts[it.id] ?: it.likes)
    }
    val userRoutines = sampleUserRoutines.map {
        it.copy(isLiked = likedStates[it.id] ?: it.isLiked, likes = likeCounts[it.id] ?: it.likes)
    }

    val state = UserProfileUiState(
        nickname = "팔로우",
        bio = "자기소개입니다. 자기소개입니다.",
        routineCount = if (isDataEmpty) 0 else 4,
        followerCount = 628,
        followingCount = 221,
        isFollowing = isFollowing,
        isRunningRoutineExpanded = isExpanded,
        runningRoutines = if (isDataEmpty) emptyList() else runningRoutines,
        userRoutines = if (isDataEmpty) emptyList() else userRoutines,
    )

    MORUTheme {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                BasicTopAppBar(
                    title = "사용자명",
                    navigationIcon = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "뒤로가기",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
                    spacingBetweenIconAndTitle = 24.dp,
                )
            }
        ) { paddingValues ->
            UserProfileContent(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onFollowClick = { isFollowing = !isFollowing },
                onToggleExpansion = { isExpanded = !isExpanded },
                onLikeClick = { id ->
                    val currentStatus = likedStates[id] ?: false
                    likedStates[id] = !currentStatus
                    val currentCount = likeCounts[id] ?: 0
                    likeCounts[id] = if (!currentStatus) currentCount + 1 else currentCount - 1
                },
                onFollowerClick = {},
                onFollowingClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Default State")
@Composable
fun UserProfileScreenDefaultPreview() {
    UserProfileScreenPreview(isDataEmpty = false)
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun UserProfileScreenEmptyPreview() {
    UserProfileScreenPreview(isDataEmpty = true)
}