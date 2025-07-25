package com.konkuk.moru.presentation.onboarding

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.OnboardingPreference
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
    val introduction: String = "",
    val tags: List<String> = emptyList()
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

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
    }

    fun updateGender(gender: String) {
        _userInfo.value = _userInfo.value.copy(gender = gender)
    }

    fun updateBirthday(birthday: String) {
        _userInfo.value = _userInfo.value.copy(birthday = birthday)
    }

    fun updateIntroduction(intro: String) {
        _userInfo.value = _userInfo.value.copy(introduction = intro)
    }

    fun updateTags(tags: List<String>) {
        _userInfo.value = _userInfo.value.copy(tags = tags)
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
            Log.d("Onboarding", "✅ 온보딩 완료됨")
        }
    }
}