package com.konkuk.moru.presentation.home.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.RoutineSyncBus
import com.konkuk.moru.core.datastore.SchedulePreference
import com.konkuk.moru.data.dto.response.HomeScheduleResponse
import com.konkuk.moru.data.dto.response.Routine.RoutineDetailResponseV1
import com.konkuk.moru.data.mapper.toDomain
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.repositoryimpl.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeRoutinesViewModel @Inject constructor(
    private val repo: RoutineRepository,
    private val myRoutineRepo: com.konkuk.moru.domain.repository.MyRoutineRepository
) : ViewModel() {

    private companion object {
        private const val TAG = "HomeRoutinesVM"
    }

    init {
        Log.d(TAG, "🚀 HomeRoutinesViewModel 생성됨!")
        Log.d(TAG, "🔍 repo: $repo")
        Log.d(TAG, "🔍 myRoutineRepo: $myRoutineRepo")
        
        // 간단한 테스트 로그
        android.util.Log.e("TEST_LOG", "이 로그가 보이나요? HomeRoutinesViewModel 생성됨!")
        System.out.println("System.out 테스트: HomeRoutinesViewModel 생성됨!")
        
        // RoutineSyncBus 구독하여 내 루틴 변경 시 자동 리프레시
        viewModelScope.launch {
            RoutineSyncBus.events
                .filterIsInstance<RoutineSyncBus.Event.MyRoutinesChanged>()
                .collectLatest {
                    Log.d(TAG, "🔄 RoutineSyncBus.MyRoutinesChanged 이벤트 수신 - 내 루틴 리프레시")
                    loadMyRoutines()
                    loadTodayRoutines()
                }
        }
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
        runCatching { repo.getAllMyRoutines(page, size) }
            .onSuccess { pageRes ->
                Log.d(TAG, "✅ loadMyRoutines 성공!")
                Log.d(TAG, "📊 응답 데이터: total=${pageRes.totalElements}, page=${pageRes.number}, size=${pageRes.size}, contentSize=${pageRes.content.size}")
                
                val routines = pageRes.content.map { it.toDomain() }
                Log.d(TAG, "🔄 도메인 변환 완료: ${routines.size}개")
                
                // 각 루틴의 상세 정보 로깅
                routines.forEachIndexed { index, routine ->
                    Log.d(TAG, "🔍 루틴[$index]: ${routine.title}, category=${routine.category}, scheduledDays=${routine.scheduledDays}, scheduledTime=${routine.scheduledTime}, requiredTime=${routine.requiredTime}")
                }
                
                // 첫 번째 로드인지 확인 (현재 루틴 목록이 비어있는 경우)
                val isFirstLoad = _myRoutines.value.isEmpty()
                
                if (isFirstLoad) {
                    // 첫 번째 로드: 기본 정렬 적용
                    val sortedRoutines = routines.sortedWith(
                        compareByDescending<Routine> { it.scheduledTime == null }
                            .thenBy { it.scheduledTime ?: java.time.LocalTime.MAX }
                    )
                    
                    Log.d(TAG, "🔄 첫 번째 로드: 기본 정렬 적용 - 시간 미설정 ${sortedRoutines.count { it.scheduledTime == null }}개, 시간 설정 ${sortedRoutines.count { it.scheduledTime != null }}개")
                    _myRoutines.value = sortedRoutines
                } else {
                    // 이후 로드: 진행 중인 루틴들의 정렬 유지
                    val currentRoutines = _myRoutines.value
                    val runningRoutines = currentRoutines.filter { it.isRunning }
                    val nonRunningRoutines = routines.filter { !it.isRunning }
                    
                    // 진행 중이지 않은 루틴들을 기본 정렬 기준으로 정렬
                    val sortedNonRunning = nonRunningRoutines.sortedWith(
                        compareByDescending<Routine> { it.scheduledTime == null }
                            .thenBy { it.scheduledTime ?: java.time.LocalTime.MAX }
                    )
                    
                    // 진행 중인 루틴들 + 정렬된 나머지 루틴들
                    val finalRoutines = runningRoutines + sortedNonRunning
                    
                    Log.d(TAG, "🔄 이후 로드: 진행중 루틴 정렬 유지 - 진행중 ${runningRoutines.size}개, 시간 미설정 ${sortedNonRunning.count { it.scheduledTime == null }}개, 시간 설정 ${sortedNonRunning.count { it.scheduledTime != null }}개")
                    _myRoutines.value = finalRoutines
                }
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

    // MyRoutineDetailDto를 사용하여 루틴 상세 정보 로드 (사용앱 정보 포함)
    fun loadMyRoutineDetail(routineId: String) = viewModelScope.launch {
        // 강제 테스트 로그
        android.util.Log.e("TEST_LOG", "🔥 loadMyRoutineDetail 호출됨! routineId=$routineId")
        System.out.println("🔥 System.out: loadMyRoutineDetail 호출됨! routineId=$routineId")
        
        Log.d(TAG, "🔄 loadMyRoutineDetail 시작: routineId=$routineId")

        runCatching { 
            // MyRoutineRepository를 사용하여 사용앱 정보가 포함된 상세 정보 가져오기
            Log.d(TAG, "🔄 myRoutineRepo.getRoutineDetailRaw 호출: routineId=$routineId")
            val result = myRoutineRepo.getRoutineDetailRaw(routineId)
            Log.d(TAG, "✅ myRoutineRepo.getRoutineDetailRaw 성공: $result")
            result
        }
        .onSuccess { detail ->
            // 강제 테스트 로그
            android.util.Log.e("TEST_LOG", "🔥 loadMyRoutineDetail 성공!")
            android.util.Log.e("TEST_LOG", "🔥 제목: ${detail.title}")
            android.util.Log.e("TEST_LOG", "🔥 스텝 개수: ${detail.steps.size}")
            android.util.Log.e("TEST_LOG", "🔥 사용앱 개수: ${detail.apps.size}")
            System.out.println("🔥 System.out: loadMyRoutineDetail 성공!")
            
            Log.d(TAG, "✅ loadMyRoutineDetail 성공!")
            Log.d(TAG, "   - 제목: ${detail.title}")
            Log.d(TAG, "   - 스텝 개수: ${detail.steps.size}")
            Log.d(TAG, "   - 사용앱 개수: ${detail.apps.size}")
            
            // 사용앱 정보 상세 로깅
            if (detail.apps.isNotEmpty()) {
                Log.d(TAG, "📱 사용앱 상세 정보:")
                detail.apps.forEachIndexed { index, app ->
                    Log.d(TAG, "   - 사용앱 ${index + 1}: ${app.name} (${app.packageName})")
                }
            } else {
                Log.w(TAG, "⚠️ 사용앱 정보가 비어있음! detail.apps.size = ${detail.apps.size}")
                Log.d(TAG, "🔍 detail 객체 전체 정보: $detail")
            }
            
            // 스텝 정보를 SharedRoutineViewModel에 설정
            Log.d(TAG, "🔄 스텝 정보를 SharedRoutineViewModel에 설정")
            val stepDataList = detail.steps.map { step ->
                com.konkuk.moru.presentation.home.RoutineStepData(
                    name = step.name,
                    duration = step.estimatedTime?.let { time ->
                        // ISO 8601 Duration 형식을 분 단위로 변환
                        when {
                            time.startsWith("PT") -> {
                                val timePart = time.substring(2)
                                when {
                                    timePart.endsWith("H") -> {
                                        val hours = timePart.removeSuffix("H").toIntOrNull() ?: 0
                                        hours * 60
                                    }
                                    timePart.endsWith("M") -> {
                                        timePart.removeSuffix("M").toIntOrNull() ?: 0
                                    }
                                    timePart.endsWith("S") -> {
                                        val seconds = timePart.removeSuffix("S").toIntOrNull() ?: 0
                                        (seconds + 59) / 60 // 올림 처리
                                    }
                                    else -> 1
                                }
                            }
                            else -> 1
                        }
                    } ?: 1,
                    isChecked = true
                )
            }
            
            _sharedViewModel?.let { shared ->
                shared.setSelectedSteps(stepDataList)
                Log.d(TAG, "✅ SharedRoutineViewModel에 스텝 정보 설정 완료: ${stepDataList.size}개")
            }
            
            // 사용앱 정보를 SharedRoutineViewModel에 설정
            Log.d(TAG, "🔄 사용앱 정보를 SharedRoutineViewModel에 설정")
            setAppsToSharedViewModel(detail.apps)
            
            // 기존 RoutineDetailResponseV1 형식으로 변환하여 _routineDetail에 설정
            // (기존 코드와의 호환성을 위해)
                         val convertedDetail = com.konkuk.moru.data.dto.response.Routine.RoutineDetailResponseV1(
                 id = detail.id,
                title = detail.title,
                description = detail.description,
                category = if (detail.isSimple) "간편" else "집중",
                tags = detail.tags,
                                 steps = detail.steps.map { step ->
                     com.konkuk.moru.data.dto.response.RoutineStepResponse(
                         id = step.id,
                         order = step.stepOrder,
                         name = step.name,
                         duration = step.estimatedTime,
                         description = null
                     )
                 },
                author = com.konkuk.moru.data.dto.response.Routine.AuthorResponse(
                    id = detail.author.id,
                    name = detail.author.nickname,
                    profileImageUrl = detail.author.profileImageUrl
                ),
                authorName = detail.author.nickname
            )
            
            _routineDetail.value = convertedDetail
            Log.d(TAG, "✅ _routineDetail StateFlow 업데이트 완료")
        }
        .onFailure { e ->
            Log.e(TAG, "❌ loadMyRoutineDetail 실패: routineId=$routineId", e)
            _routineDetail.value = null
        }
    }

    // SharedRoutineViewModel 참조
    private var _sharedViewModel: com.konkuk.moru.presentation.routinefocus.viewmodel.SharedRoutineViewModel? = null

    // 스텝 정보를 SharedRoutineViewModel에 설정
    private fun setStepsToSharedViewModel(steps: List<com.konkuk.moru.data.dto.response.RoutineStepResponse>) {
        Log.d(TAG, "🔄 setStepsToSharedViewModel 호출: ${steps.size}개 스텝")
        
        _sharedViewModel?.let { shared ->
            // 스텝 정보를 RoutineStepData로 변환하여 설정
            val stepDataList = steps.map { step ->
                com.konkuk.moru.presentation.home.RoutineStepData(
                    name = step.name,
                    duration = step.duration?.let { duration ->
                        // ISO 8601 Duration 형식을 분 단위로 변환
                        when {
                            duration.startsWith("PT") -> {
                                val timePart = duration.substring(2)
                                when {
                                    timePart.endsWith("H") -> {
                                        val hours = timePart.removeSuffix("H").toIntOrNull() ?: 0
                                        hours * 60
                                    }
                                    timePart.endsWith("M") -> {
                                        timePart.removeSuffix("M").toIntOrNull() ?: 0
                                    }
                                    timePart.endsWith("S") -> {
                                        val seconds = timePart.removeSuffix("S").toIntOrNull() ?: 0
                                        (seconds + 59) / 60 // 올림 처리
                                    }
                                    else -> 1
                                }
                            }
                            else -> 1
                        }
                    } ?: 1,
                    isChecked = true
                )
            }
            
            shared.setSelectedSteps(stepDataList)
            Log.d(TAG, "✅ SharedRoutineViewModel에 스텝 정보 설정 완료: ${stepDataList.size}개")
        } ?: run {
            Log.w(TAG, "⚠️ SharedRoutineViewModel이 설정되지 않음")
        }
    }

    // 사용앱 정보를 SharedRoutineViewModel에 설정
    private fun setAppsToSharedViewModel(apps: List<com.konkuk.moru.data.dto.response.MyRoutine.MyRoutineDetailDto.AppDto>) {
        Log.d(TAG, "🔄 setAppsToSharedViewModel 호출: ${apps.size}개 앱")
        
        _sharedViewModel?.let { shared ->
            // AppDto를 AppDto로 변환하여 설정 (패키지명과 이름만 있음)
            val appDtoList = apps.map { app ->
                com.konkuk.moru.presentation.routinefeed.data.AppDto(
                    name = app.name,
                    packageName = app.packageName
                )
            }
            
            shared.setSelectedApps(appDtoList)
            Log.d(TAG, "✅ SharedRoutineViewModel에 사용앱 정보 설정 완료: ${appDtoList.size}개")
            appDtoList.forEachIndexed { index, app ->
                Log.d(TAG, "   - 앱 ${index + 1}: ${app.name} (${app.packageName})")
            }
            
            // 추가 로그: SharedRoutineViewModel의 selectedApps 상태 확인
            Log.d(TAG, "🔍 SharedRoutineViewModel.selectedApps 확인: ${shared.selectedApps.value.size}개")
            shared.selectedApps.value.forEachIndexed { index, app ->
                Log.d(TAG, "   - SharedViewModel 앱 ${index + 1}: ${app.name} (${app.packageName})")
            }
        } ?: run {
            Log.w(TAG, "⚠️ SharedRoutineViewModel이 설정되지 않음")
        }
    }

    // SharedRoutineViewModel 인스턴스를 받아서 설정
    fun setSharedRoutineViewModel(sharedViewModel: com.konkuk.moru.presentation.routinefocus.viewmodel.SharedRoutineViewModel) {
        _sharedViewModel = sharedViewModel
        Log.d(TAG, "✅ SharedRoutineViewModel 설정 완료")
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

    // myRoutines 업데이트 (진행중인 루틴을 맨 앞으로 이동할 때 사용)
    fun updateMyRoutines(updatedRoutines: List<Routine>) {
        Log.d(TAG, "🔄 updateMyRoutines 호출: ${updatedRoutines.size}개 루틴")
        Log.d(TAG, "📋 업데이트 전 myRoutines: " + _myRoutines.value.joinToString { "${it.title}(isRunning=${it.isRunning})" })
        Log.d(TAG, "📋 업데이트할 루틴들: " + updatedRoutines.joinToString { "${it.title}(isRunning=${it.isRunning})" })

        _myRoutines.value = updatedRoutines

        Log.d(TAG, "✅ _myRoutines StateFlow 업데이트 완료")
        Log.d(TAG, "📋 업데이트 후 myRoutines: " + _myRoutines.value.joinToString { "${it.title}(isRunning=${it.isRunning})" })
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
