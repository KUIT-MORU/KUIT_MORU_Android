package com.konkuk.moru.presentation.routinefeed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.RoutineSyncBus
import com.konkuk.moru.core.datastore.SocialMemory
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
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

// 상세 화면의 모든 UI 상태를 담는 데이터 클래스
// 상세 화면 UI 상태
data class RoutineDetailUiState(
    val routine: Routine? = null,
    val similarRoutines: List<SimilarRoutine> = emptyList(),
    val canBeAddedToMyRoutines: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isLiking: Boolean = false,
    val isScrapping: Boolean = false,
)

@HiltViewModel
class RoutineDetailViewModel @Inject constructor(
    private val repository: RoutineFeedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutineDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun setInitialRoutine(r: Routine) {
        _uiState.update { it.copy(routine = r, isLoading = true, errorMessage = null) }
    }

    // ===== TTL 가드(서버 반영 지연 방지) =====
    private val PROTECT_TTL = 2.seconds

    private data class LikeStamp(
        val expectedLikes: Int,
        val wantLike: Boolean,
        val mark: TimeMark = TimeSource.Monotonic.markNow()
    )
    private val likeStampMap = ConcurrentHashMap<String, LikeStamp>()

    private data class ScrapStamp(
        val expectedScrap: Int,
        val wantScrap: Boolean,
        val mark: TimeMark = TimeSource.Monotonic.markNow()
    )
    private val scrapStampMap = ConcurrentHashMap<String, ScrapStamp>()

    private fun guardAfterLikeToggle(
        routineId: String,
        serverLikes: Int,
        serverIsLiked: Boolean
    ): Pair<Int, Boolean> {
        val st = likeStampMap[routineId] ?: return serverLikes to serverIsLiked
        if (st.mark.elapsedNow() > PROTECT_TTL) return serverLikes to serverIsLiked
        return st.expectedLikes to st.wantLike
    }

    private fun guardAfterScrapToggle(
        routineId: String,
        serverScrap: Int,
        serverIsBookmarked: Boolean
    ): Pair<Int, Boolean> {
        val st = scrapStampMap[routineId] ?: return serverScrap to serverIsBookmarked
        if (st.mark.elapsedNow() > PROTECT_TTL) return serverScrap to serverIsBookmarked
        return st.expectedScrap to st.wantScrap
    }

    private fun guardOnLoadLikes(
        routineId: String,
        prevLikes: Int?,
        prevIsLiked: Boolean?,
        serverLikes: Int,
        serverIsLiked: Boolean
    ): Pair<Int, Boolean> {
        val (l, f) = guardAfterLikeToggle(routineId, serverLikes, serverIsLiked)
        val likes = if (l == 0 && (prevLikes ?: 0) > 0) prevLikes!! else l
        val isLiked = prevIsLiked ?: f
        return likes to isLiked
    }

    private fun guardOnLoadScrap(
        routineId: String,
        prevScrap: Int?,
        prevIsBookmarked: Boolean?,
        serverScrap: Int,
        serverIsBookmarked: Boolean
    ): Pair<Int, Boolean> {
        val (s, b) = guardAfterScrapToggle(routineId, serverScrap, serverIsBookmarked)
        val scrap = if (s == 0 && (prevScrap ?: 0) > 0) prevScrap!! else s
        val isBookmarked = prevIsBookmarked ?: b
        return scrap to isBookmarked
    }

    // ===== 액션: 좋아요 =====
    fun toggleLikeSync() {
        val state = _uiState.value
        val r = state.routine ?: return
        if (state.isLiking || state.isLoading) return

        val wantLike = !r.isLiked
        val bumped = (r.likes + if (wantLike) 1 else -1).coerceAtLeast(0)

        // 낙관 UI + 메모리 + 스탬프
        _uiState.update { it.copy(routine = r.copy(isLiked = wantLike, likes = bumped), isLiking = true, errorMessage = null) }
        SocialMemory.setLike(r.routineId, wantLike, bumped)
        likeStampMap[r.routineId] = LikeStamp(expectedLikes = bumped, wantLike = wantLike)

        val before = state
        viewModelScope.launch {
            runCatching {
                if (wantLike) repository.addLike(r.routineId) else repository.removeLike(r.routineId)
                repository.getRoutineDetail(r.routineId) // 확정값
            }.onSuccess { fresh ->
                val server = fresh.toRoutineModel(prev = _uiState.value.routine)
                val (likesMerged, isLikedMerged) = guardAfterLikeToggle(r.routineId, server.likes, server.isLiked)
                val merged = server.copy(likes = likesMerged, isLiked = isLikedMerged)

                SocialMemory.setLike(merged.routineId, merged.isLiked, merged.likes)
                _uiState.update { it.copy(routine = merged, isLiking = false) }
                RoutineSyncBus.publish(RoutineSyncBus.Event.Like(merged.routineId, merged.isLiked, merged.likes))
            }.onFailure { e ->
                _uiState.value = before.copy(isLiking = false, errorMessage = e.message)
                // 메모리 롤백
                before.routine?.let { rb -> SocialMemory.setLike(rb.routineId, rb.isLiked, rb.likes) }
            }.also {
                likeStampMap.remove(r.routineId)
            }
        }
    }

    // ===== 액션: 스크랩 =====
    fun toggleScrapSync() {
        val state = _uiState.value
        val r = state.routine ?: return
        if (state.isScrapping || state.isLoading) return

        val want = !r.isBookmarked
        val bumped = (r.scrapCount + if (want) 1 else -1).coerceAtLeast(0)

        // 낙관 UI + 메모리 + 스탬프
        _uiState.update { it.copy(routine = r.copy(isBookmarked = want, scrapCount = bumped), isScrapping = true, errorMessage = null) }
        SocialMemory.setScrap(r.routineId, want, bumped)
        scrapStampMap[r.routineId] = ScrapStamp(expectedScrap = bumped, wantScrap = want)

        val before = state
        viewModelScope.launch {
            runCatching {
                if (want) repository.addScrap(r.routineId) else repository.removeScrap(r.routineId)
                repository.getRoutineDetail(r.routineId)
            }.onSuccess { fresh ->
                val server = fresh.toRoutineModel(prev = _uiState.value.routine)
                val (scrapMerged, isBookmarkedMerged) = guardAfterScrapToggle(r.routineId, server.scrapCount, server.isBookmarked)
                val merged = server.copy(scrapCount = scrapMerged, isBookmarked = isBookmarkedMerged)

                SocialMemory.setScrap(merged.routineId, merged.isBookmarked, merged.scrapCount)
                _uiState.update { it.copy(routine = merged, isScrapping = false) }
                RoutineSyncBus.publish(RoutineSyncBus.Event.Scrap(merged.routineId, merged.isBookmarked))
            }.onFailure { e ->
                _uiState.value = before.copy(isScrapping = false, errorMessage = e.message)
                // 메모리 롤백
                before.routine?.let { rb -> SocialMemory.setScrap(rb.routineId, rb.isBookmarked, rb.scrapCount) }
            }.also {
                scrapStampMap.remove(r.routineId)
            }
        }
    }

    // ===== 로드 =====
    fun loadRoutine(routineId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { repository.getRoutineDetail(routineId) }
                .onSuccess { dto ->
                    val prev = _uiState.value.routine
                    val server = dto.toRoutineModel(prev = prev)

                    // 로드 시에도 “뒤로 감는” 현상 방지
                    val (mergedLikes, mergedIsLiked) = guardOnLoadLikes(
                        routineId = server.routineId,
                        prevLikes = prev?.likes,
                        prevIsLiked = prev?.isLiked,
                        serverLikes = server.likes,
                        serverIsLiked = server.isLiked
                    )
                    val (mergedScrap, mergedIsBookmarked) = guardOnLoadScrap(
                        routineId = server.routineId,
                        prevScrap = prev?.scrapCount,
                        prevIsBookmarked = prev?.isBookmarked,
                        serverScrap = server.scrapCount,
                        serverIsBookmarked = server.isBookmarked
                    )

                    val merged = server.copy(
                        likes = mergedLikes,
                        isLiked = mergedIsLiked,
                        scrapCount = mergedScrap,
                        isBookmarked = mergedIsBookmarked
                    )

                    _uiState.update {
                        it.copy(
                            routine = merged,
                            similarRoutines = dto.similarRoutines?.map { s -> s.toUiModel() } ?: emptyList(),
                            canBeAddedToMyRoutines = !dto.isOwner,
                            isLoading = false,
                            errorMessage = null
                        )
                    }

                    // 다른 화면과 즉시 일치
                    RoutineSyncBus.publish(RoutineSyncBus.Event.Like(merged.routineId, merged.isLiked, merged.likes))
                    RoutineSyncBus.publish(RoutineSyncBus.Event.Scrap(merged.routineId, merged.isBookmarked))
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    // ===== 내 루틴으로 복사(더미) =====
    fun copyRoutineToMyList() {
        _uiState.value.routine?.let { original ->
            val myInfo = DummyData.dummyUsers.find { it.userId == DummyData.MY_USER_ID }
            val newRoutine = original.copy(
                routineId = UUID.randomUUID().toString(),
                authorId = DummyData.MY_USER_ID,
                authorName = myInfo?.nickname ?: "MORU (나)",
                authorProfileUrl = myInfo?.profileImageUrl,
                isLiked = false,
                likes = 0
            )
            DummyData.feedRoutines.add(0, newRoutine)
            _uiState.update { it.copy(canBeAddedToMyRoutines = false) }
        }
    }
}