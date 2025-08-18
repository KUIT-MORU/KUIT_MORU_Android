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

// -------------------------------
// UI 모델
// -------------------------------
data class UserInfo(
    val nickname: String = "",
    val gender: String = "",
    val birthday: String = "",
    val bio: String = "",
    val tags: List<String> = emptyList(),
    val profileImageKey: String? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: OBUserRepository
) : ViewModel() {

    // -------------------------------
    // Const & Logs
    // -------------------------------
    companion object {
        const val LAST_PAGE_INDEX = 6
        private const val TAG = "onboarding"
    }

    // -------------------------------
    // UI 상태
    // -------------------------------
    private val _nicknameAvailable = MutableStateFlow<Boolean?>(null)
    val nicknameAvailable: StateFlow<Boolean?> = _nicknameAvailable

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    private val _isOnboardingComplete = MutableStateFlow(false)
    val isOnboardingComplete: StateFlow<Boolean> = _isOnboardingComplete

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo: StateFlow<UserInfo> = _userInfo

    // -------------------------------
    // 태그 캐시: "이름" → "ID"
    // -------------------------------
    private val tagIdMap = MutableStateFlow<Map<String, String>>(emptyMap())

    /** 진입 시점 등에서 호출: /api/tags 프리패치 */
    fun prefetchTags() {
        viewModelScope.launch {
            runCatching { ensureTagIdMap() }
                .onSuccess { Log.d(TAG, "prefetchTags() ok: size=${it.size}") }
                .onFailure { Log.d(TAG, "prefetchTags() failed: ${it.message}") }
        }
    }

    // -------------------------------
    // 유틸
    // -------------------------------
    /** "#라벨" → "라벨", 공백 제거 */
    private fun normalizeTagLabel(labelWithHash: String): String =
        labelWithHash.removePrefix("#").trim()

    // -------------------------------
    // 닉네임
    // -------------------------------
    fun checkNicknameAvailability(nickname: String) {
        Log.d(TAG, "checkNicknameAvailability() request: nickname=$nickname")
        viewModelScope.launch {
            val result = userRepository.checkNicknameAvailable(nickname)
            result.onSuccess { ok ->
                Log.d(TAG, "checkNicknameAvailability() success: available=$ok")
                _nicknameAvailable.value = ok
                if (ok) updateNickname(nickname)
            }.onFailure { e ->
                Log.d(TAG, "checkNicknameAvailability() failed: ${e.message}")
                _nicknameAvailable.value = false
            }
        }
    }

    // -------------------------------
    // 프로필 제출
    // -------------------------------
    fun submitUserInfo() {
        viewModelScope.launch {
            val info = _userInfo.value
            val serverGender = when (info.gender.trim()) {
                "남자" -> "MALE"
                "여자" -> "FEMALE"
                else -> "MALE"
            }

            Log.d(
                TAG,
                "submitUserInfo() payload: nick='${info.nickname.trim()}', gender=$serverGender, " +
                        "birthday='${info.birthday.trim()}', bio='${info.bio.trim()}', " +
                        "profileImageUrl='${_userInfo.value.profileImageKey}'"
            )

            // 서버 스키마 확인: PATCH /api/user/me + Map
            val body = buildMap<String, String> {
                put("nickname", info.nickname.trim())
                put("gender", serverGender)
                put("birthday", info.birthday.trim())
                _userInfo.value.profileImageKey?.takeIf { it.isNotBlank() }?.let {
                    put("profileImageUrl", it)
                }
                info.bio.trim().takeIf { it.isNotEmpty() }?.let {
                    put("bio", it)
                }
            }

            userRepository.updateProfileDynamic(body)
                .onSuccess { resp: UpdateUserProfileResponse ->
                    Log.d(TAG, "submitUserInfo() success: $resp")
                }
                .onFailure { e ->
                    Log.d(TAG, "submitUserInfo() failed: ${e.message}")
                }
        }
    }

    // -------------------------------
    // 태그 매핑 & 전송
    // -------------------------------
    /** /api/tags 호출 후 이름→ID 맵을 구성·캐시 */
    private suspend fun ensureTagIdMap(): Map<String, String> {
        if (tagIdMap.value.isNotEmpty()) return tagIdMap.value
        val result = userRepository.getAllTags() // Result<List<ServerTag>>
        val map = result
            .onFailure { e -> Log.d(TAG, "getAllTags() failed: ${e.message}") }
            .getOrElse { emptyList() }
            // ✅ associateBy 두 번째 파라미터는 valueTransform!
            .associateBy(
                keySelector = { it.name.trim() },   // "공부"
                valueTransform = { it.id }           // UUID
            )

        tagIdMap.value = map
        Log.d(TAG, "ensureTagIdMap() size=${map.size}")
        return map
    }

    fun submitFavoriteTagsByLabels(labelsWithHash: List<String>) {
        viewModelScope.launch {
            val map = ensureTagIdMap()
            if (map.isEmpty()) {
                Log.d(TAG, "submitFavoriteTagsByLabels() aborted: tagIdMap is empty")
                return@launch // [추가]
            }

            val ids = labelsWithHash
                .map(::normalizeTagLabel)
                .mapNotNull { map[it] }
                .distinct()

            if (ids.isEmpty()) {
                Log.d(TAG, "submitFavoriteTagsByLabels() aborted: resolved ids is empty for labels=$labelsWithHash")
                return@launch // [추가]
            }

            Log.d(TAG, "submitFavoriteTagsByLabels() labels=$labelsWithHash ids=$ids")
            userRepository.addFavoriteTags(FavoriteTagRequest(ids))
                .onSuccess { Log.d(TAG, "addFavoriteTags() success") }
                .onFailure { e -> Log.d(TAG, "addFavoriteTags() failed: ${e.message}") }
        }
    }

    /** (호환) 이미 ID가 있는 경우 그대로 전송 */
    fun submitFavoriteTags(tagIds: List<String>) {
        viewModelScope.launch {
            userRepository.addFavoriteTags(FavoriteTagRequest(tagIds))
        }
    }

    // -------------------------------
    // UI 상태 업데이트
    // -------------------------------
    fun updateNickname(nickname: String) {
        _userInfo.value = _userInfo.value.copy(nickname = nickname)
        Log.d(TAG, "Nickname updated: $nickname")
    }

    fun updateGender(gender: String) {
        _userInfo.value = _userInfo.value.copy(gender = gender)
        Log.d(TAG, "Gender updated: $gender")
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

    // -------------------------------
    // 이미지 업로드
    // -------------------------------
    fun uploadProfileImageFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            Log.d(TAG, "uploadProfileImageFromUri() uri=$uri")
            val tmpFile = copyUriToCacheFile(context, uri)
            Log.d(TAG, "uploadProfileImageFromUri() tmpFile=${tmpFile.absolutePath} size=${tmpFile.length()}")
            val result = userRepository.uploadImage(tmpFile)
            result.onSuccess { imageUrlOrKey ->
                updateProfileImageUrl(imageUrlOrKey) // 서버는 temp/... 경로를 profileImageUrl로 받음
                Log.d(TAG, "uploadProfileImageFromUri() success imageUrl=$imageUrlOrKey (state.profileImageKey=${_userInfo.value.profileImageKey})")
            }.onFailure { e ->
                Log.d(TAG, "uploadProfileImageFromUri() failed: ${e.message}")
            }
        }
    }

    fun updateProfileImageUrl(url: String?) {
        _userInfo.value = _userInfo.value.copy(profileImageKey = url)
        Log.d(TAG, "ProfileImageUrl updated: $url")
    }

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

    // -------------------------------
    // 온보딩 흐름
    // -------------------------------
    fun nextPage() {
        if (_currentPage.value < LAST_PAGE_INDEX) _currentPage.value += 1
        else completeOnboarding()
    }

    fun skipOnboarding() = completeOnboarding()

    private fun completeOnboarding() {
        viewModelScope.launch {
            OnboardingPreference.setOnboardingComplete(context)
            _isOnboardingComplete.value = true
            Log.d(TAG, "✅ 온보딩 완료됨")
        }
    }
}