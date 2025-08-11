package com.konkuk.moru.presentation.routinefeed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.RoutineSyncBus
import com.konkuk.moru.data.mapper.toRoutineModel
import com.konkuk.moru.data.mapper.toUiModel
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.model.SimilarRoutine
import com.konkuk.moru.domain.repository.RoutineFeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// 상세 화면의 모든 UI 상태를 담는 데이터 클래스
data class RoutineDetailUiState(
    val routine: Routine? = null,
    val similarRoutines: List<SimilarRoutine> = emptyList(),
    val canBeAddedToMyRoutines: Boolean = false, // 이 루틴을 복사할 수 있는지 여부
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isLiking: Boolean = false,
    val isScrapping: Boolean = false,
)
@HiltViewModel
class RoutineDetailViewModel @Inject constructor(
    private val repository: RoutineFeedRepository // [추가]
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutineDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun setInitialRoutine(r: Routine) {
        _uiState.update { it.copy(routine = r, isLoading = true, errorMessage = null) }
    }

    // [추가] 서버값과 낙관값 병합 유틸
    private fun mergeLikesAfterToggle(
        beforeLikes: Int,
        wantLike: Boolean,
        serverLikes: Int
    ): Int {
        // 우리가 기대한 낙관값
        val expected = (beforeLikes + if (wantLike) 1 else -1).coerceAtLeast(0)
        // 서버가 아직 반영 전이면(좋아요 직후 서버값이 더 작거나 / 취소 직후 서버값이 더 큰 경우) 낙관값 유지
        return when {
            wantLike  && serverLikes < expected -> expected
            !wantLike && serverLikes > expected -> expected
            else -> serverLikes
        }
    }

    // [추가] 초기 진입 시(로드 시) 피드에서 들고 온 값과 서버값 병합
    private fun mergeLikesOnLoad(prevLikes: Int?, serverLikes: Int?): Int {
        // prevLikes가 존재하고, 서버가 늦게 반영되어 "돌아가는" 경우를 막기 위한 간단한 규칙:
        // - 서버값이 prev보다 작아졌다면(좋아요 직후) prev 유지
        // - 서버값이 prev보다 커졌다면(좋아요 취소 직후) prev 유지
        // - 둘 다 아니면 서버값 채택
        if (prevLikes == null) return serverLikes ?: 0
        if (serverLikes == null) return prevLikes
        return when {
            serverLikes < prevLikes -> prevLikes
            serverLikes > prevLikes -> prevLikes
            else -> serverLikes
        }
    }

    fun toggleLikeSync() {
        val current = _uiState.value
        val r = current.routine ?: return
        if (current.isLiking || current.isLoading) return

        val wantLike = !r.isLiked

        // 1) 낙관적 업데이트
        val before = _uiState.value
        val bumpedLikes = (r.likes + if (wantLike) 1 else -1).coerceAtLeast(0)
        _uiState.update {
            it.copy(routine = r.copy(isLiked = wantLike, likes = bumpedLikes), isLiking = true, errorMessage = null)
        }

        // 2) 서버 반영 + 재조회
        viewModelScope.launch {
            runCatching {
                if (wantLike) repository.addLike(r.routineId) else repository.removeLike(r.routineId)
                repository.getRoutineDetail(r.routineId)
            }.onSuccess { fresh ->
                val serverModel = fresh.toRoutineModel(prev = _uiState.value.routine)
                // [변경] 서버값과 낙관값 병합
                val finalLikes = mergeLikesAfterToggle(
                    beforeLikes = r.likes,
                    wantLike = wantLike,
                    serverLikes = serverModel.likes
                )
                val finalModel = serverModel.copy(likes = finalLikes)

                _uiState.update { it.copy(routine = finalModel, isLiking = false) }

                // [유지/보강] 다른 화면 동기화 (병합된 최종값 발행)
                RoutineSyncBus.publish(
                    RoutineSyncBus.Event.Like(
                        routineId = finalModel.routineId,
                        isLiked = finalModel.isLiked,
                        likeCount = finalModel.likes
                    )
                )
            }.onFailure { e ->
                _uiState.value = before.copy(isLiking = false, errorMessage = e.message)
            }
        }
    }

    fun loadRoutine(routineId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { repository.getRoutineDetail(routineId) }
                .onSuccess { dto ->
                    val prev = _uiState.value.routine // 피드에서 전달받은 pre-selected 값(있으면)
                    val mapped = dto.toRoutineModel(prev = prev)
                    // [변경] 로드 시에도 prev.likes와 서버 likes를 병합해서 "되돌림" 방지
                    val stitchedLikes = mergeLikesOnLoad(prevLikes = prev?.likes, serverLikes = mapped.likes)
                    val final = mapped.copy(likes = stitchedLikes)

                    _uiState.update {
                        it.copy(
                            routine = final,
                            similarRoutines = dto.similarRoutines?.map { it.toUiModel() } ?: emptyList(),
                            canBeAddedToMyRoutines = !dto.isOwner,
                            isLoading = false,
                            errorMessage = null
                        )
                    }

                    // [추가] 최종값을 한 번 더 브로드캐스트 → 피드와 즉시 일치
                    RoutineSyncBus.publish(
                        RoutineSyncBus.Event.Like(
                            routineId = final.routineId,
                            isLiked = final.isLiked,
                            likeCount = final.likes
                        )
                    )
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    /*fun toggleLikeSync() {
        val current = _uiState.value
        val r = current.routine ?: return
        if (current.isLiking || current.isLoading) return

        val wantLike = !r.isLiked

        val before = _uiState.value
        val bumpedLikes = (r.likes + if (wantLike) 1 else -1).coerceAtLeast(0)
        _uiState.update {
            it.copy(routine = r.copy(isLiked = wantLike, likes = bumpedLikes), isLiking = true, errorMessage = null)
        }

        viewModelScope.launch {
            runCatching {
                if (wantLike) repository.addLike(r.routineId) else repository.removeLike(r.routineId)
                repository.getRoutineDetail(r.routineId) // [유지] 서버 최신 재조회
            }.onSuccess { fresh ->
                val synced = fresh.toRoutineModel(prev = _uiState.value.routine)
                _uiState.update { it.copy(routine = synced, isLiking = false) }

                // [추가] 다른 화면 동기화: 서버값으로 발행(정확도 ↑)
                RoutineSyncBus.publish(
                    RoutineSyncBus.Event.Like(
                        routineId = synced.routineId,
                        isLiked = synced.isLiked,
                        likeCount = synced.likes
                    )
                )
            }.onFailure { e ->
                _uiState.value = before.copy(isLiking = false, errorMessage = e.message)
            }
        }
    }*/

    fun toggleScrapSync() {
        val current = _uiState.value
        val r = current.routine ?: return
        if (current.isScrapping || current.isLoading) return

        val wantScrap = !r.isBookmarked

        val before = _uiState.value
        _uiState.update { it.copy(routine = r.copy(isBookmarked = wantScrap), isScrapping = true, errorMessage = null) }

        viewModelScope.launch {
            runCatching {
                if (wantScrap) repository.addScrap(r.routineId) else repository.removeScrap(r.routineId)
                repository.getRoutineDetail(r.routineId)
            }.onSuccess { fresh ->
                val synced = fresh.toRoutineModel(prev = _uiState.value.routine)
                _uiState.update { it.copy(routine = synced, isScrapping = false) }

                // [추가]
                RoutineSyncBus.publish(
                    RoutineSyncBus.Event.Scrap(
                        routineId = synced.routineId,
                        isScrapped = synced.isBookmarked
                    )
                )
            }.onFailure { e ->
                _uiState.value = before.copy(isScrapping = false, errorMessage = e.message)
            }
        }
    }


    /*fun loadRoutine(routineId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                repository.getRoutineDetail(routineId) // [수정] 서버 호출
            }.onSuccess { dto ->
                val merged = dto.toRoutineModel(prev = _uiState.value.routine)
                val similar = dto.similarRoutines?.map { it.toUiModel() } ?: emptyList()
                _uiState.update {
                    it.copy(
                        routine = merged,
                        similarRoutines = similar,
                        canBeAddedToMyRoutines = !dto.isOwner, // [수정] 서버 필드 사용
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message)
                }
            }
        }
    }*/

    // [핵심] '내 루틴으로 복사' 기능을 구현하는 함수
    fun copyRoutineToMyList() {
        _uiState.value.routine?.let { originalRoutine ->
            val myInfo = DummyData.dummyUsers.find { it.userId == DummyData.MY_USER_ID }

            // 1. 루틴 복사 및 authorId 변경
            val newRoutine = originalRoutine.copy(
                routineId = UUID.randomUUID().toString(), // 새 고유 ID 부여
                authorId = DummyData.MY_USER_ID,          // 작성자를 '나'로 변경
                authorName = myInfo?.nickname ?: "MORU (나)",
                authorProfileUrl = myInfo?.profileImageUrl,
                isLiked = false, // 상태 초기화
                likes = 0
            )

            // 2. 전체 데이터 목록에 새로 복사한 루틴 추가
            DummyData.feedRoutines.add(0, newRoutine)
            println("루틴(original id: ${originalRoutine.routineId})이 내 루틴으로 복사되었습니다. (new id: ${newRoutine.routineId})")

            // 3. 복사 후에는 버튼을 다시 보이지 않도록 UI 상태 갱신
            _uiState.update { it.copy(canBeAddedToMyRoutines = false) }
        }
    }
}