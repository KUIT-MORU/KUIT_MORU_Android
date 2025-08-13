package com.konkuk.moru.presentation.myactivity.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltViewModel
class ActUserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender

    private val _birthday = MutableStateFlow("")
    val birthday: StateFlow<String> = _birthday

    private val _bio = MutableStateFlow("")
    val bio: StateFlow<String> = _bio

    private val _routineCount = MutableStateFlow(0)
    val routineCount: StateFlow<Int> = _routineCount
    private val _followerCount = MutableStateFlow(0)
    val followerCount: StateFlow<Int> = _followerCount
    private val _followingCount = MutableStateFlow(0)
    val followingCount: StateFlow<Int> = _followingCount

    val displayGender: StateFlow<String> =
        gender.map(::mapGenderKo)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    val displayBirthday: StateFlow<String> =
        birthday.map(::formatBirthdayDot)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    fun loadMe() = viewModelScope.launch {
        runCatching { userRepository.getUserProfile() }
            .onSuccess { p ->
                _nickname.value = p.nickname ?: ""
                _gender.value = p.gender ?: ""          // 원본 그대로
                _birthday.value = p.birthday ?: ""      // "2025-08-13"
                _bio.value = p.bio ?: ""
                _routineCount.value = p.routineCount
                _followerCount.value = p.followerCount
                _followingCount.value = p.followingCount
            }
            .onFailure { e -> Log.e("UserViewModel", "getUserProfile failed", e) }
    }
}

private fun formatBirthdayDot(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    return runCatching {
        val inFmt = DateTimeFormatter.ISO_LOCAL_DATE        // "yyyy-MM-dd"
        val outFmt = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        LocalDate.parse(raw.trim(), inFmt).format(outFmt)
    }.getOrElse { raw }
}

private fun mapGenderKo(raw: String?): String = when (raw?.trim()?.uppercase()) {
    "MALE" -> "남자"
    "FEMALE" -> "여자"
    else -> ""
}
