package com.konkuk.moru.presentation.onboarding

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.OnboardingPreference
import com.konkuk.moru.data.dto.request.FavoriteTagRequest
import com.konkuk.moru.data.dto.request.UpdateUserProfileRequest
import com.konkuk.moru.domain.repository.OBUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserInfo(
    val nickname: String = "",
    val gender: String = "",
    val birthday: String = "",
    val bio: String = "",
    val tags: List<String> = emptyList(),
    val profileImageUri: String? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: OBUserRepository
) : ViewModel() {

    // ✅ 닉네임 중복 확인 상태
    private val _nicknameAvailable = MutableStateFlow<Boolean?>(null)
    val nicknameAvailable: StateFlow<Boolean?> = _nicknameAvailable


    fun checkNicknameAvailability(nickname: String) {
        Log.d("onboarding", "checkNicknameAvailability() request: nickname=$nickname")
        viewModelScope.launch {
            val result = userRepository.checkNicknameAvailable(nickname)
            result.onSuccess { ok ->
                Log.d("onboarding", "checkNicknameAvailability() success: available=$ok")
                _nicknameAvailable.value = ok
                if (ok) updateNickname(nickname)
            }.onFailure { e ->
                Log.d("onboarding", "checkNicknameAvailability() failed: ${e.message}")
                _nicknameAvailable.value = false
            }
        }
    }

    // ✅ 유저 정보 제출 함수
    fun submitUserInfo() {
        viewModelScope.launch {
            val info = _userInfo.value

            val serverGender = when (info.gender.trim()) {
                "남자" -> "MALE"
                "여자" -> "FEMALE"
                else -> "MALE" // 기본값 설정: 서버에서 요구하는 값이 없을 경우
            }

            val imageForServer = info.profileImageUri?.let { uri ->
                if (uri.startsWith("http://") || uri.startsWith("https://")) uri else "null"
            }

            val safeBirthday = info.birthday.trim()

            Log.d("onboarding", "updateProfile() FINAL body (sanitized)=" +
                    " nickname=${info.nickname.trim()}," +
                    " gender=$serverGender," +
                    " birthday=$safeBirthday," +
                    " bio=${info.bio.trim()}," +
                    " profileImageUrl=$imageForServer"
            )

            userRepository.updateProfile(
                UpdateUserProfileRequest(
                    nickname = info.nickname.trim(),
                    gender = serverGender,
                    birthday = safeBirthday,
                    bio = info.bio.trim(),
                    profileImageUrl = info.profileImageUri
                )
            )
        }
    }

    // ✅ 즐겨찾는 태그 제출 함수
    fun submitFavoriteTags(tagIds: List<String>) {
        viewModelScope.launch {
            userRepository.addFavoriteTags(FavoriteTagRequest(tagIds))
        }
    }

    companion object {
        const val LAST_PAGE_INDEX = 6
    }

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    private val _isOnboardingComplete = MutableStateFlow(false)
    val isOnboardingComplete: StateFlow<Boolean> = _isOnboardingComplete

    // ✅ 유저 정보 상태
    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo: StateFlow<UserInfo> = _userInfo

    // ✅ 유저 정보 업데이트 함수들
    fun updateNickname(nickname: String) {
        _userInfo.value = _userInfo.value.copy(nickname = nickname)
        Log.d("onboarding", "Nickname updated: $nickname")
    }

    fun updateGender(gender: String) {
        _userInfo.value = _userInfo.value.copy(gender = gender)
        Log.d("onboarding", "Gender updated: $gender")
    }

    fun updateBirthday(birthday: String) {
        _userInfo.value = _userInfo.value.copy(birthday = birthday)
    }

    fun updateIntroduction(intro: String) {
        _userInfo.value = _userInfo.value.copy(bio = intro)
    }

    fun updateTags(tags: List<String>) {
        _userInfo.value = _userInfo.value.copy(tags = tags)
    }

    fun updateProfileImage(uri: String?) {
        _userInfo.value = _userInfo.value.copy(profileImageUri = uri)
    }

    // ✅ 다음 페이지 로직
    fun nextPage() {
        if (_currentPage.value < LAST_PAGE_INDEX) {
            _currentPage.value += 1
        } else {
            completeOnboarding()
        }
    }

    fun skipOnboarding() {
        completeOnboarding()
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            // ✅ 추후 서버로 _userInfo.value 를 전송하는 API 호출 예정
            OnboardingPreference.setOnboardingComplete(context)
            _isOnboardingComplete.value = true
            Log.d("onboarding", "✅ 온보딩 완료됨")
        }
    }
}