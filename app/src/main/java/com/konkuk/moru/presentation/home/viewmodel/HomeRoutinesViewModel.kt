package com.konkuk.moru.presentation.home.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.SchedulePreference
import com.konkuk.moru.data.dto.response.Routine.RoutineDetailResponseV1
import com.konkuk.moru.data.mapper.toDomain
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.repositoryimpl.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeRoutinesViewModel @Inject constructor(
    private val repo: RoutineRepository
) : ViewModel() {

    private companion object {
        private const val TAG = "HomeRoutinesVM"
    }

    init {
        Log.d(TAG, "🚀 HomeRoutinesViewModel 생성됨!")
        Log.d(TAG, "🔍 repo: $repo")
    }

    private val _serverRoutines = MutableStateFlow<List<Routine>>(emptyList())
    val serverRoutines: StateFlow<List<Routine>> = _serverRoutines

    // 내 루틴 전체(하단 카드)
    private val _myRoutines = MutableStateFlow<List<Routine>>(emptyList())
    val myRoutines: StateFlow<List<Routine>> = _myRoutines

    // 스케줄 정보가 병합된 루틴 목록
    private val _scheduledRoutines = MutableStateFlow<List<Routine>>(emptyList())
    val scheduledRoutines: StateFlow<List<Routine>> = _scheduledRoutines

    // 루틴 상세 정보 (스텝 포함)
    private val _routineDetail = MutableStateFlow<RoutineDetailResponseV1?>(null)
    val routineDetail: StateFlow<RoutineDetailResponseV1?> = _routineDetail

    fun loadTodayRoutines(page: Int = 0, size: Int = 20) {
        Log.d(TAG, "🔄 loadTodayRoutines 호출됨: page=$page, size=$size")
        viewModelScope.launch {
            Log.d(TAG, "🔄 loadTodayRoutines 코루틴 시작")
            runCatching { repo.getMyRoutinesToday(page, size) }
                .onSuccess { pageRes ->
                    Log.d(TAG, "✅ loadTodayRoutines 성공!")
                    Log.d(TAG, "loadTodayRoutines success: " +
                            "total=${pageRes.totalElements}, page=${pageRes.number}, size=${pageRes.size}, " +
                            "contentSize=${pageRes.content.size}"
                    )
                    Log.d(TAG, "server IDs=" + pageRes.content.joinToString { it.routineId })

                    _serverRoutines.value = pageRes.content.map { it.toDomain() }
                    Log.d(TAG, "✅ _serverRoutines StateFlow 업데이트 완료")
                }
                .onFailure { e ->
                    Log.e(TAG, "❌ loadTodayRoutines 실패!", e)
                    if (e is retrofit2.HttpException) {
                        val code = e.code()
                        val err = e.response()?.errorBody()?.string()
                        android.util.Log.e("HomeRoutinesVM", "HTTP $code errorBody=$err")
                        
                        // HTTP 500 오류 시 사용자에게 친화적인 메시지
                        if (code == 500) {
                            Log.e(TAG, "🚨 서버 내부 오류 (500) - 서버 점검 중일 수 있습니다")
                            Log.d(TAG, "💡 서버 점검 완료까지 잠시 기다려주세요")
                        }
                    } else {
                        android.util.Log.e(TAG, "loadTodayRoutines failed", e)
                    }
                    
                    // 서버 오류 시 빈 리스트로 설정 (UI가 깨지지 않도록)
                    _serverRoutines.value = emptyList()
                    
                    // TODO: 향후 로컬 캐시 데이터를 사용하도록 개선
                    Log.d(TAG, "💡 서버 오류로 인해 빈 리스트로 설정됨. 서버 상태를 확인해주세요.")
                }
        }
    }

    // 전체 루틴 로드
    fun loadMyRoutines(page: Int = 0, size: Int = 100) = viewModelScope.launch {
        Log.d(TAG, "🔄 loadMyRoutines 호출됨: page=$page, size=$size")
        runCatching { repo.getMyRoutinesToday(page, size) }
            .onSuccess { pageRes ->
                Log.d(TAG, "✅ loadMyRoutines 성공!")
                Log.d(TAG, "📊 응답 데이터: total=${pageRes.totalElements}, page=${pageRes.number}, size=${pageRes.size}, contentSize=${pageRes.content.size}")
                
                val routines = pageRes.content.map { it.toDomain() }
                Log.d(TAG, "🔄 도메인 변환 완료: ${routines.size}개")
                
                // 각 루틴의 상세 정보 로깅
                routines.forEachIndexed { index, routine ->
                    Log.d(TAG, "🔍 루틴[$index]: ${routine.title}, category=${routine.category}, scheduledDays=${routine.scheduledDays}, scheduledTime=${routine.scheduledTime}, requiredTime=${routine.requiredTime}")
                }
                
                _myRoutines.value = routines
                Log.d(TAG, "✅ _myRoutines StateFlow 업데이트 완료")
            }
            .onFailure { e ->
                Log.e(TAG, "❌ loadMyRoutines 실패!", e)
                if (e is retrofit2.HttpException) {
                    val code = e.code()
                    val err = e.response()?.errorBody()?.string()
                    android.util.Log.e(TAG, "HTTP $code errorBody=$err")
                } else {
                    android.util.Log.e(TAG, "loadMyRoutines failed", e)
                }
                _myRoutines.value = emptyList()
            }
    }

    // 루틴 상세 정보 로드 (스텝 포함)
    fun loadRoutineDetail(routineId: String) = viewModelScope.launch {
        Log.d(TAG, "🔄 loadRoutineDetail 시작: routineId=$routineId")

        runCatching { repo.getRoutineDetail(routineId) }
            .onSuccess { detail ->
                Log.d(TAG, "✅ loadRoutineDetail 성공!")
                Log.d(TAG, "   - 제목: ${detail.title}")
                Log.d(TAG, "   - 설명: ${detail.description ?: "없음"}")
                Log.d(TAG, "   - 카테고리: ${detail.category ?: "없음"}")
                Log.d(TAG, "   - 태그: ${detail.tags}")
                Log.d(TAG, "   - 스텝 개수: ${detail.steps.size}")
                Log.d(TAG, "   - 작성자: ${detail.author?.name ?: detail.authorName ?: "없음"}")

                detail.steps.forEachIndexed { index, step ->
                    Log.d(TAG, "   - 스텝 ${index + 1}: ${step.name} (${step.duration})")
                    Log.d(TAG, "     설명: ${step.description ?: "없음"}")
                }
                _routineDetail.value = detail
                Log.d(TAG, "✅ _routineDetail StateFlow 업데이트 완료")

                // 스텝 정보를 SharedRoutineViewModel에 직접 설정
                Log.d(TAG, "🔄 스텝 정보를 SharedRoutineViewModel에 설정")
                setStepsToSharedViewModel(detail.steps)
            }
            .onFailure { e ->
                Log.e(TAG, "❌ loadRoutineDetail 실패: routineId=$routineId", e)

                // HTTP 오류인 경우 응답 본문 출력
                if (e is retrofit2.HttpException) {
                    val response = e.response()
                    val errorBody = response?.errorBody()?.string()
                    Log.e(TAG, "HTTP ${response?.code()} 응답: $errorBody")
                }

                _routineDetail.value = null
            }
    }

    // 스텝 정보를 SharedRoutineViewModel에 설정
    private fun setStepsToSharedViewModel(steps: List<com.konkuk.moru.data.dto.response.RoutineStepResponse>) {
        Log.d(TAG, "🔄 setStepsToSharedViewModel 호출: ${steps.size}개 스텝")
        // 이 함수는 SharedRoutineViewModel과 연결되어야 합니다
        // 현재는 로그만 출력
        steps.forEachIndexed { index, step ->
            Log.d(TAG, "   - 스텝 ${index + 1}: ${step.name} (${step.duration})")
        }
    }

    // SharedRoutineViewModel 인스턴스를 받아서 스텝 설정
    fun setSharedRoutineViewModel(sharedViewModel: com.konkuk.moru.presentation.routinefocus.viewmodel.SharedRoutineViewModel) {
        _sharedViewModel = sharedViewModel
    }
    
    // 로컬 스케줄 정보 가져오기
    suspend fun getLocalSchedule(context: Context, routineId: String): com.konkuk.moru.core.datastore.SchedulePreference.ScheduleInfo? {
        return try {
            val localSchedules = com.konkuk.moru.core.datastore.SchedulePreference.getSchedules(context)
            localSchedules.find { it.routineId == routineId }
        } catch (e: Exception) {
            Log.e(TAG, "로컬 스케줄 정보 가져오기 실패: routineId=$routineId", e)
            null
        }
    }

    private var _sharedViewModel: com.konkuk.moru.presentation.routinefocus.viewmodel.SharedRoutineViewModel? = null

    // 로컬 스케줄 정보와 병합
    fun mergeWithLocalSchedule(context: Context) = viewModelScope.launch {
        try {
            val localSchedules = SchedulePreference.getSchedules(context)
            Log.d(TAG, "로컬 스케줄 정보 로드: ${localSchedules.size}개")

            localSchedules.forEach { schedule ->
                Log.d(TAG, "루틴 ${schedule.routineId}: ${schedule.scheduledDays}, ${schedule.scheduledTime}")
            }

            // 서버 루틴과 로컬 스케줄 정보 병합
            val mergedRoutines = _serverRoutines.value.map { routine ->
                val localSchedule = localSchedules.find { it.routineId == routine.routineId }
                if (localSchedule != null) {
                    routine.copy(
                        scheduledDays = SchedulePreference.stringsToDayOfWeeks(localSchedule.scheduledDays),
                        scheduledTime = SchedulePreference.stringToLocalTime(localSchedule.scheduledTime)
                    )
                } else {
                    routine
                }
            }

            _scheduledRoutines.value = mergedRoutines
            Log.d(TAG, "스케줄 정보 병합 완료: ${mergedRoutines.size}개")

        } catch (e: Exception) {
            Log.e(TAG, "로컬 스케줄 정보 병합 실패", e)
            _scheduledRoutines.value = _serverRoutines.value
        }
    }
}
