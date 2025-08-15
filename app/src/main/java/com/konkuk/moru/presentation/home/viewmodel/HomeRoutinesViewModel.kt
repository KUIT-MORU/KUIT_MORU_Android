package com.konkuk.moru.presentation.home.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.SchedulePreference
import com.konkuk.moru.data.dto.response.RoutineDetailResponseV1
import com.konkuk.moru.data.dto.response.HomeScheduleResponse
import com.konkuk.moru.data.mapper.toDomain
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.repositoryimpl.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

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
        Log.d(TAG, "🌐 네트워크 연결 테스트 시작...")
        
        viewModelScope.launch {
            Log.d(TAG, "🚀 코루틴 시작됨!")
            Log.d(TAG, "🔄 loadTodayRoutines 코루틴 시작")
            try {
                Log.d(TAG, "🔗 서버 연결 시도 중...")
                Log.d(TAG, "🌐 API 엔드포인트: /api/routines/today (page=$page, size=$size)")
                
                val pageRes = repo.getMyRoutinesToday(page, size)
                Log.d(TAG, "✅ loadTodayRoutines 성공!")
                Log.d(TAG, "📊 서버 응답: total=${pageRes.totalElements}, page=${pageRes.number}, size=${pageRes.size}, contentSize=${pageRes.content.size}")
                
                if (pageRes.content.isNotEmpty()) {
                    Log.d(TAG, "🎯 루틴 목록:")
                    pageRes.content.forEachIndexed { index, routine ->
                        Log.d(TAG, "   [$index] ID: ${routine.routineId}, 제목: ${routine.title}")
                    }
                } else {
                    Log.w(TAG, "⚠️ 서버에서 루틴 데이터가 비어있음")
                }

                _serverRoutines.value = pageRes.content.map { it.toDomain() }
                Log.d(TAG, "✅ _serverRoutines StateFlow 업데이트 완료: ${_serverRoutines.value.size}개")
            } catch (e: Exception) {
                    Log.e(TAG, "❌ loadTodayRoutines 실패!", e)
                Log.e(TAG, "🔍 예외 타입: ${e.javaClass.simpleName}")
                Log.e(TAG, "🔍 예외 메시지: ${e.message}")
                
                when (e) {
                    is retrofit2.HttpException -> {
                        val code = e.code()
                        val err = e.response()?.errorBody()?.string()
                        Log.e(TAG, "HTTP $code errorBody=$err")
                        
                        if (code == 500) {
                            Log.e(TAG, "🚨 서버 내부 오류 (500) - 서버 점검 중일 수 있습니다")
                        } else if (code == 404) {
                            Log.e(TAG, "🚨 서버 엔드포인트를 찾을 수 없습니다 (404)")
                        } else if (code == 403) {
                            Log.e(TAG, "🚨 접근 권한이 없습니다 (403)")
                        }
                    }
                    is java.net.SocketTimeoutException -> {
                        Log.e(TAG, "🚨 네트워크 타임아웃 발생")
                    }
                    is java.net.UnknownHostException -> {
                        Log.e(TAG, "🚨 서버 호스트를 찾을 수 없습니다")
                    }
                    is javax.net.ssl.SSLHandshakeException -> {
                        Log.e(TAG, "🚨 SSL 인증서 문제 발생")
                    }
                    is java.net.ConnectException -> {
                        Log.e(TAG, "🚨 서버 연결 실패")
                    }
                    else -> {
                        Log.e(TAG, "🚨 기타 네트워크 오류: ${e.javaClass.simpleName}")
                    }
                    }
                    
                    // 서버 오류 시 빈 리스트로 설정 (UI가 깨지지 않도록)
                    _serverRoutines.value = emptyList()
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

    // 서버에서 스케줄 정보 가져오기
    suspend fun getRoutineSchedules(routineId: String): List<HomeScheduleResponse> {
        return try {
            Log.d(TAG, "🔄 getRoutineSchedules 호출: routineId=$routineId")
            Log.d(TAG, "🌐 API 엔드포인트: /api/routines/$routineId/schedules")
            
            val schedules = repo.getRoutineSchedules(routineId)
            Log.d(TAG, "✅ 스케줄 정보 가져오기 성공: ${schedules.size}개")
            
            if (schedules.isEmpty()) {
                Log.w(TAG, "⚠️ 스케줄이 비어있음 - 서버에 스케줄 데이터가 없을 수 있음")
            } else {
                Log.d(TAG, "✅ 스케줄 데이터 발견: ${schedules.size}개")
                schedules.forEachIndexed { index, schedule ->
                    Log.d(TAG, "   스케줄[$index]: id=${schedule.id}, dayOfWeek=${schedule.dayOfWeek}, time=${schedule.time}, alarmEnabled=${schedule.alarmEnabled}, repeatType=${schedule.repeatType}, daysToCreate=${schedule.daysToCreate}")
                }
            }
            schedules
        } catch (e: Exception) {
            Log.e(TAG, "❌ 스케줄 정보 가져오기 실패: routineId=$routineId", e)
            Log.e(TAG, "❌ 에러 상세: ${e.message}")
            
            // HTTP 오류인 경우 응답 본문 출력
            if (e is retrofit2.HttpException) {
                val response = e.response()
                val errorBody = response?.errorBody()?.string()
                Log.e(TAG, "HTTP ${response?.code()} 응답: $errorBody")
                
                when (response?.code()) {
                    404 -> Log.e(TAG, "🚨 API 엔드포인트를 찾을 수 없음 (404) - 서버에 해당 엔드포인트가 구현되지 않았을 수 있음")
                    403 -> Log.e(TAG, "🚨 접근 권한 없음 (403) - 인증 토큰 문제일 수 있음")
                    500 -> Log.e(TAG, "🚨 서버 내부 오류 (500) - 서버 점검 중일 수 있음")
                    else -> Log.e(TAG, "🚨 기타 HTTP 오류: ${response?.code()}")
                }
            }
            
            // JSON 파싱 오류인 경우 상세 정보 출력
            if (e.message?.contains("JsonDecodingException") == true || e.message?.contains("Unexpected JSON token") == true) {
                Log.e(TAG, "🚨 JSON 파싱 오류 발생")
                Log.e(TAG, "   - 오류 메시지: ${e.message}")
                Log.e(TAG, "   - 오류 타입: ${e.javaClass.simpleName}")
                
                // 메시지에서 상세 정보 추출
                e.message?.let { message ->
                    if (message.contains("at offset")) {
                        val offset = message.substringAfter("at offset ").substringBefore(":")
                        Log.e(TAG, "   - 오류 위치: $offset")
                    }
                    if (message.contains("Expected")) {
                        val expected = message.substringAfter("Expected ").substringBefore(" but")
                        Log.e(TAG, "   - 예상 타입: $expected")
                    }
                    if (message.contains("but '") && message.contains("' literal")) {
                        val actual = message.substringAfter("but '").substringBefore("' literal")
                        Log.e(TAG, "   - 실제 값: $actual")
                    }
                    if (message.contains("at path:")) {
                        val path = message.substringAfter("at path: ").substringBefore(" ")
                        Log.e(TAG, "   - 필드 경로: $path")
                    }
                }
            }
            
            e.printStackTrace()
            emptyList()
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
