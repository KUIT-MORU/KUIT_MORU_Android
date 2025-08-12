package com.konkuk.moru.presentation.routinefeed.component.follow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.konkuk.moru.presentation.routinefeed.data.FollowUser
import com.konkuk.moru.ui.theme.MORUTheme

@Composable
fun FollowListContent(
    users: List<FollowUser>,
    emptyMessage: String,
    emptySubMessage: String,
    myId: String?=null,
    onFollowClick: (FollowUser) -> Unit,
    onUserClick: (String) -> Unit
) {
    if (users.isEmpty()) {
        EmptyFollowContent(
            message = emptyMessage, subMessage = emptySubMessage
        )
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = users, key = { it.id }) { user ->
                val showFollow = myId == null || user.id != myId // ✅ 내 아이디면 숨김
                UserItem(
                    user = user,
                    showFollowButton = showFollow,               // ✅ 변경된 시그니처
                    onFollowClick = onFollowClick,
                    onUserClick = onUserClick
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun FollowListContentPreview() {
    val me = "me-123"
    val users = listOf(
        FollowUser(id = me, profileImageUrl = "", username = "나", bio = "me", isFollowing = false),
        FollowUser(id = "u-2", profileImageUrl = "", username = "Alice", bio = "안녕", isFollowing = true),
        FollowUser(id = "u-3", profileImageUrl = "", username = "Bob", bio = "hi", isFollowing = false),
    )
    MORUTheme {
        FollowListContent(
            users = users,
            emptyMessage = "비었어요",
            emptySubMessage = "사람을 찾아보세요",
            myId = me,                            // ✅ 내 아이디 지정
            onFollowClick = {},
            onUserClick = {}
        )
    }
}