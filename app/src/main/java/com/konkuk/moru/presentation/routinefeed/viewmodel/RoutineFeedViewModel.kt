package com.konkuk.moru.presentation.routinefeed.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.LikeMemory
import com.konkuk.moru.core.datastore.RoutineSyncBus
import com.konkuk.moru.data.mapper.toRoutineModel
import com.konkuk.moru.domain.repository.RoutineFeedRepository
import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineFeedSectionModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoutineFeedUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val hasNotification: Boolean = true,
    val liveUsers: List<LiveUserInfo> = emptyList(),
    val routineSections: List<RoutineFeedSectionModel> = emptyList()
)

@HiltViewModel
class RoutineFeedViewModel @Inject constructor(
    private val routineFeedRepository: RoutineFeedRepository // 👈 주입받는 타입 변경
) : ViewModel() {
    private val _uiState = MutableStateFlow(RoutineFeedUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadLiveUsers()
        loadRoutineFeed()

        // [추가] 다른 화면에서 발생한 변경 사항을 피드에 반영
        viewModelScope.launch {
            RoutineSyncBus.events.collectLatest { e ->
                when (e) {
                    is RoutineSyncBus.Event.Like -> {
                        _uiState.update { state ->
                            state.copy(
                                routineSections = state.routineSections.map { section ->
                                    section.copy(
                                        routines = section.routines.map { r ->
                                            if (r.routineId == e.routineId)
                                                r.copy(isLiked = e.isLiked, likes = e.likeCount)
                                            else r
                                        }
                                    )
                                }
                            )
                        }
                    }

                    is RoutineSyncBus.Event.Scrap -> {
                        _uiState.update { state ->
                            state.copy(
                                routineSections = state.routineSections.map { section ->
                                    section.copy(
                                        routines = section.routines.map { r ->
                                            if (r.routineId == e.routineId)
                                                r.copy(isBookmarked = e.isScrapped)
                                            else r
                                        }
                                    )
                                }
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun loadLiveUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 👇 새 Repository의 함수들을 호출
                val liveUsersResult = routineFeedRepository.getLiveUsers()
                //val routineSectionsResult = routineFeedRepository.getRoutineSections()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        liveUsers = liveUsersResult,
                        //routineSections = routineSectionsResult
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "데이터를 불러오는 데 실패했습니다.\n${e.message}"
                    )
                }
            }
        }
    }

    private fun loadRoutineFeed() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 1. Repository를 통해 서버로부터 DTO 받아오기
                val response = routineFeedRepository.getRoutineFeed()

                // 2. 받아온 DTO를 UI에서 사용할 `RoutineFeedSectionModel` 리스트로 변환
                val sections = buildList {
                    if (response.hotRoutines.isNotEmpty()) {
                        add(
                            RoutineFeedSectionModel(
                                title = "지금 가장 핫한 루틴은?",
                                routines = response.hotRoutines.map { it.toRoutineModel() } // ✅ 매퍼 함수 사용
                            )
                        )
                    }
                    // TODO: 사용자 별명 받으면 title 변경 
                    if (response.personalRoutines.isNotEmpty()) {
                        add(
                            RoutineFeedSectionModel(
                                title = "MORU님과 딱 맞는 루틴",
                                routines = response.personalRoutines.map { it.toRoutineModel() }
                            )
                        )
                    }
                    if (response.tagPairSection1.routines.isNotEmpty()) {
                        add(
                            RoutineFeedSectionModel(
                                title = "#${response.tagPairSection1.tag1} #${response.tagPairSection1.tag2}",
                                routines = response.tagPairSection1.routines.map { it.toRoutineModel() }
                            )
                        )
                    }
                    if (response.tagPairSection2.routines.isNotEmpty()) {
                        add(
                            RoutineFeedSectionModel(
                                title = "#${response.tagPairSection2.tag1} #${response.tagPairSection2.tag2}",
                                routines = response.tagPairSection2.routines.map { it.toRoutineModel() }
                            )
                        )
                    }
                }

                // 3. 변환된 데이터로 UI State 업데이트
                _uiState.update {
                    it.copy(isLoading = false, routineSections = sections)
                }
            } catch (e: Exception) {
                Log.e("RoutineFeedViewModel", "Failed to load routine feed", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "루틴 피드를 불러오는 데 실패했습니다: ${e.message}"
                    )
                }
            }
        }
    }

    fun onNotificationViewed() {
        _uiState.update { it.copy(hasNotification = false) }
    }



    fun toggleLike(routineId: String) {
        val before = _uiState.value.routineSections

        // 1) 낙관적 업데이트 (UI 먼저 반응)
        val after = before.map { section ->
            section.copy(
                routines = section.routines.map { r ->
                    if (r.routineId == routineId) {
                        val newLiked = !r.isLiked
                        val newCount = (r.likes + if (newLiked) 1 else -1).coerceAtLeast(0)
                        r.copy(isLiked = newLiked, likes = newCount)
                    } else r
                }
            )
        }
        _uiState.update { it.copy(routineSections = after) }

        // 2) 서버 반영
        viewModelScope.launch {
            runCatching {
                val target = after.firstNotNullOfOrNull { s -> s.routines.find { it.routineId == routineId } }
                if (target?.isLiked == true) {
                    routineFeedRepository.addLike(routineId)
                } else {
                    routineFeedRepository.removeLike(routineId)
                }
            }.onSuccess {
                // [추가] 현재 UI 값으로 이벤트 발행 (가벼운 동기화)
                val latest = _uiState.value.routineSections.firstNotNullOfOrNull { s -> s.routines.find { it.routineId == routineId } }
                latest?.let {
                    RoutineSyncBus.publish(RoutineSyncBus.Event.Like(routineId, it.isLiked, it.likes))
                }
            }.onFailure { e ->
                _uiState.update { it.copy(routineSections = before, errorMessage = e.message) }
            }
        }
    }

    // [추가] 스크랩 토글
    fun toggleScrap(routineId: String) {
        val before = _uiState.value.routineSections

        // 1) 낙관적 업데이트: isBookmarked만 토글
        val after = before.map { section ->
            section.copy(
                routines = section.routines.map { r ->
                    if (r.routineId == routineId) {
                        val newScrap = !r.isBookmarked
                        r.copy(
                            isBookmarked = newScrap
                            // scrapCount = ...  // [삭제] 더 이상 건드리지 않음
                        )
                    } else r
                }
            )
        }
        _uiState.update { it.copy(routineSections = after) }

        // 2) 서버 반영
        viewModelScope.launch {
            runCatching {
                val target = after.firstNotNullOfOrNull { s -> s.routines.find { it.routineId == routineId } }
                if (target?.isBookmarked == true) {
                    routineFeedRepository.addScrap(routineId)
                } else {
                    routineFeedRepository.removeScrap(routineId)
                }
            }.onSuccess {
                // [추가]
                val latest = _uiState.value.routineSections.firstNotNullOfOrNull { s -> s.routines.find { it.routineId == routineId } }
                latest?.let {
                    RoutineSyncBus.publish(RoutineSyncBus.Event.Scrap(routineId, it.isBookmarked))
                }
            }.onFailure { e ->
                _uiState.update { it.copy(routineSections = before, errorMessage = e.message) }
            }
        }
    }

}


