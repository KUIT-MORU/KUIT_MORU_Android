package com.konkuk.moru.presentation.routinefeed.component.follow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.konkuk.moru.presentation.routinefeed.data.FollowUser

@Composable
fun FollowListContent(
    users: List<FollowUser>,
    emptyMessage: String,
    emptySubMessage: String,
    onFollowClick: (FollowUser) -> Unit,
    onUserClick: (String) -> Unit
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
                UserItem(user = user, onFollowClick = onFollowClick, onUserClick = onUserClick)
            }
        }
    }
}