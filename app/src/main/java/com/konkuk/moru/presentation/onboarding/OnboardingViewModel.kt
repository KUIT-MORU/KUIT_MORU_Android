package com.konkuk.moru.presentation.onboarding

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.OnboardingPreference
import com.konkuk.moru.data.dto.request.FavoriteTagRequest
import com.konkuk.moru.data.dto.response.UpdateUserProfileResponse
import com.konkuk.moru.domain.repository.OBUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class UserInfo(
    val nickname: String = "",
    val gender: String = "",
    val birthday: String = "",
    val bio: String = "",
    val tags: List<String> = emptyList(),
    val profileImageKey: String? = null // 업로드 결과(temp/...)를 담는다
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: OBUserRepository
) : ViewModel() {

    private val _nicknameAvailable = MutableStateFlow<Boolean?>(null)
    val nicknameAvailable: StateFlow<Boolean?> = _nicknameAvailable

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    private val _isOnboardingComplete = MutableStateFlow(false)
    val isOnboardingComplete: StateFlow<Boolean> = _isOnboardingComplete

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo: StateFlow<UserInfo> = _userInfo

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

    fun submitUserInfo() {
        viewModelScope.launch {
            val info = _userInfo.value
            val serverGender = when (info.gender.trim()) {
                "남자" -> "MALE"
                "여자" -> "FEMALE"
                else -> "MALE"
            }

            Log.d(
                "onboarding",
                // ★ 변경: 로그 문구 정리
                "submitUserInfo() payload: nick='${info.nickname.trim()}', gender=$serverGender, " +
                        "birthday='${info.birthday.trim()}', bio='${info.bio.trim()}', " +
                        "profileImageUrl='${_userInfo.value.profileImageKey}'"
            )

            // ★ 변경: 서버 확인된 스키마로 딱 한 번만 호출 (PUT + Map)
            val body = buildMap<String, String> {
                put("nickname", info.nickname.trim())
                put("gender", serverGender)
                put("birthDate", info.birthday.trim())           // ★ birthday → birthDate
                _userInfo.value.profileImageKey?.takeIf { it.isNotBlank() }?.let {
                    put("profileImageUrl", it)                  // ★ imageKey 대신 profileImageUrl
                }
                info.bio.trim().takeIf { it.isNotEmpty() }?.let {
                    put("bio", it)                              // 비어있으면 키 제외
                }
            }

            userRepository.updateProfileDynamic(body)
                .onSuccess { resp: UpdateUserProfileResponse ->
                    Log.d("onboarding", "submitUserInfo() success: $resp")
                }
                .onFailure { e ->
                    Log.d("onboarding", "submitUserInfo() failed: ${e.message}")
                }
        }
    }

    fun submitFavoriteTags(tagIds: List<String>) {
        viewModelScope.launch {
            userRepository.addFavoriteTags(FavoriteTagRequest(tagIds))
        }
    }

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

    fun uploadProfileImageFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            Log.d("onboarding", "uploadProfileImageFromUri() uri=$uri")
            val tmpFile = copyUriToCacheFile(context, uri)
            Log.d("onboarding", "uploadProfileImageFromUri() tmpFile=${tmpFile.absolutePath} size=${tmpFile.length()}")
            val result = userRepository.uploadImage(tmpFile)
            result.onSuccess { imageUrlOrKey ->
                // 서버는 temp/... 를 profileImageUrl 로 받음
                updateProfileImageUrl(imageUrlOrKey)
                Log.d(
                    "onboarding",
                    "uploadProfileImageFromUri() success imageUrl=$imageUrlOrKey " +
                            "(state.profileImageKey=${_userInfo.value.profileImageKey})"
                )
            }.onFailure { e ->
                Log.d("onboarding", "uploadProfileImageFromUri() failed: ${e.message}")
            }
        }
    }

    // ★ 변경: 내부 필드명(profileImageKey)에 temp 경로 저장. 로그 문구 유지
    fun updateProfileImageUrl(url: String?) {
        _userInfo.value = _userInfo.value.copy(profileImageKey = url) // ★ 변경 포인트
        Log.d("onboarding", "ProfileImageUrl updated: $url")
    }

    // (별칭 제공 유지)
    fun updateProfileImageKey(key: String?) {
        updateProfileImageUrl(key)
    }

    private fun copyUriToCacheFile(context: Context, uri: Uri): File {
        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri).use { input ->
            file.outputStream().use { output -> input?.copyTo(output) }
        }
        return file
    }

    fun nextPage() {
        if (_currentPage.value < LAST_PAGE_INDEX) _currentPage.value += 1
        else completeOnboarding()
    }

    fun skipOnboarding() = completeOnboarding()

    private fun completeOnboarding() {
        viewModelScope.launch {
            OnboardingPreference.setOnboardingComplete(context)
            _isOnboardingComplete.value = true
            Log.d("onboarding", "✅ 온보딩 완료됨")
        }
    }

    companion object {
        const val LAST_PAGE_INDEX = 6
    }
}