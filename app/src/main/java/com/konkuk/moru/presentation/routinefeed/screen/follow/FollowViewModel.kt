package com.konkuk.moru.presentation.routinefeed.screen.follow

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.presentation.routinefeed.data.FollowUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel // Hilt를 사용하므로 어노테이션을 붙여줍니다.
class FollowViewModel @Inject constructor(
    // 생성자에서 SavedStateHandle을 받아야 내비게이션으로 전달된 userId를 알 수 있습니다.
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(FollowUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // ViewModel이 생성될 때, NavHost에 정의된 'userId'를 가져옵니다.
        val userId: Int? = savedStateHandle["userId"]
        // userId가 있다면, 해당 유저의 팔로우 데이터를 불러옵니다.
        userId?.let {
            loadFollowDataFor(it)
        }
    }

    /**
     * loadInitialUsers() 대신, 특정 사용자의 팔로워/팔로잉 목록을 불러오는 함수로 변경합니다.
     * @param currentUserId 현재 팔로우 목록을 보려는 사용자의 ID
     */
    private fun loadFollowDataFor(currentUserId: Int) {
        viewModelScope.launch {
            // 1. 내 팔로워 목록 생성 (나를 팔로우하는 사람들)
            val myFollowerIds = DummyData.dummyFollowRelations
                .filter { it.followingId == currentUserId }
                .map { it.followerId }

            val followers = DummyData.dummyUsers
                .filter { myFollowerIds.contains(it.userId) }
                .map { user ->
                    val isFollowingBack = DummyData.dummyFollowRelations
                        .any { it.followerId == currentUserId && it.followingId == user.userId }
                    // [수정] FollowUser의 생성자 형식에 맞게 데이터를 전달합니다.
                    FollowUser(
                        id = user.userId,
                        profileImageUrl = user.profileImageUrl ?: "", // 프로필 이미지가 null일 경우 빈 문자열 전달
                        username = user.nickname,
                        bio = user.bio, 
                        isFollowing = isFollowingBack
                    )
                }

            // 2. 내 팔로잉 목록 생성 (내가 팔로우하는 사람들)
            val myFollowingIds = DummyData.dummyFollowRelations
                .filter { it.followerId == currentUserId }
                .map { it.followingId }

            val followings = DummyData.dummyUsers
                .filter { myFollowingIds.contains(it.userId) }
                .map { user ->
                    FollowUser(
                        id = user.userId,
                        profileImageUrl = user.profileImageUrl ?: "",
                        username = user.nickname,
                        bio = user.bio,
                        isFollowing = true // 내가 팔로우하는 사람이므로 항상 true
                    )
                }

            _uiState.value = FollowUiState(followers = followers, followings = followings)
        }
    }

    // 팔로우/언팔로우 로직은 이전과 동일한 형태로 유지합니다.
    fun toggleFollow(clickedUser: FollowUser) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val currentFollowers = currentState.followers.toMutableList()
                val currentFollowings = currentState.followings.toMutableList()
                val isCurrentlyFollowing = clickedUser.isFollowing

                if (isCurrentlyFollowing) {
                    currentFollowings.removeIf { it.id == clickedUser.id }
                    val followerIndex = currentFollowers.indexOfFirst { it.id == clickedUser.id }
                    if (followerIndex != -1) {
                        currentFollowers[followerIndex] = currentFollowers[followerIndex].copy(isFollowing = false)
                    }
                } else {
                    if (currentFollowings.none { it.id == clickedUser.id }) {
                        currentFollowings.add(0, clickedUser.copy(isFollowing = true))
                    }
                    val followerIndex = currentFollowers.indexOfFirst { it.id == clickedUser.id }
                    if (followerIndex != -1) {
                        currentFollowers[followerIndex] = currentFollowers[followerIndex].copy(isFollowing = true)
                    }
                }
                currentState.copy(followers = currentFollowers, followings = currentFollowings)
            }
        }
    }
}