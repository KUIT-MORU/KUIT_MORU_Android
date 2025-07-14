package com.konkuk.moru.presentation.routinefeed.screen.follow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.presentation.routinefeed.data.FollowUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Hilt를 사용한다면 @HiltViewModel 어노테이션 추가
class FollowViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FollowUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // ViewModel이 생성될 때 초기 데이터를 불러옵니다.
        loadInitialUsers()
    }

    // 초기 사용자 데이터를 불러오는 함수
    private fun loadInitialUsers() {
        viewModelScope.launch {
            // TODO: 실제 앱에서는 Repository를 통해 서버에서 데이터를 가져와야 합니다.
            val initialFollowers = listOf(
                FollowUser(1, "", "Moru_Official", "모루 공식 계정입니다.", true),
                FollowUser(2, "", "Android_Lover", "안드로이드 개발자", false),
                FollowUser(3, "", "Compose_Fan", "컴포즈는 쉬워요!", true),
            )
            val initialFollowings = listOf(
                FollowUser(1, "", "Moru_Official", "모루 공식 계정입니다.", true),
                FollowUser(3, "", "Compose_Fan", "컴포즈는 쉬워요!", true),
                FollowUser(4, "", "Kotlin_User", "코틀린 최고", true),
                FollowUser(5, "", "Jetpack_Guru", "제트팩 전문가", true),
            )

            _uiState.value = FollowUiState(
                followers = initialFollowers,
                followings = initialFollowings
            )
        }
    }

    // 팔로우/언팔로우 로직 처리 함수
    fun toggleFollow(clickedUser: FollowUser) {
        // TODO: 실제 서버 API 호출 로직을 여기에 추가해야 합니다.

        viewModelScope.launch {
            val isCurrentlyFollowing = clickedUser.isFollowing

            // UI 즉시 업데이트 (Optimistic Update)
            _uiState.update { currentState ->
                val currentFollowers = currentState.followers.toMutableList()
                val currentFollowings = currentState.followings.toMutableList()

                // 1. 언팔로우 로직
                if (isCurrentlyFollowing) {
                    currentFollowings.removeIf { it.id == clickedUser.id }
                    val followerIndex = currentFollowers.indexOfFirst { it.id == clickedUser.id }
                    if (followerIndex != -1) {
                        currentFollowers[followerIndex] = currentFollowers[followerIndex].copy(isFollowing = false)
                    }
                }
                // 2. 팔로우 로직
                else {
                    if (currentFollowings.none { it.id == clickedUser.id }) {
                        currentFollowings.add(0, clickedUser.copy(isFollowing = true))
                    }
                    val followerIndex = currentFollowers.indexOfFirst { it.id == clickedUser.id }
                    if (followerIndex != -1) {
                        currentFollowers[followerIndex] = currentFollowers[followerIndex].copy(isFollowing = true)
                    }
                }

                // 변경된 리스트로 새로운 상태를 만들어 반환
                currentState.copy(followers = currentFollowers, followings = currentFollowings)
            }
        }
    }
}