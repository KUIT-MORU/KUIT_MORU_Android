package com.konkuk.moru.presentation.routinefocus.screen

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.konkuk.moru.R
import com.konkuk.moru.presentation.home.RoutineStepData
import com.konkuk.moru.presentation.home.component.RoutineResultRow
import com.konkuk.moru.presentation.routinefocus.component.FocusOnboardingPopup
import com.konkuk.moru.presentation.routinefocus.component.AppIcon
import com.konkuk.moru.presentation.routinefocus.component.RoutineTimelineItem
import com.konkuk.moru.presentation.routinefocus.component.SettingSwitchGroup
import com.konkuk.moru.presentation.routinefeed.data.AppDto
import com.konkuk.moru.presentation.routinefocus.component.ScreenBlockOverlay
import com.konkuk.moru.presentation.routinefocus.viewmodel.RoutineFocusViewModel
import com.konkuk.moru.presentation.routinefocus.viewmodel.SharedRoutineViewModel
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

// 총 소요시간 계산하는 함수
fun formatTotalTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02dm %02ds", minutes, secs)
}

// 현재 step에 따라 보여줄 step들을 계산하는 함수
fun calculateVisibleSteps(currentStep: Int, totalSteps: Int): List<Int> {
    return when {
        // step이 1~4일 때는 1,2,3,4를 보여줌
        currentStep <= 4 -> (1..4).takeWhile { it <= totalSteps }
        // step이 5 이상일 때는 currentStep-3부터 currentStep까지 보여줌 (최대 4개)
        else -> {
            val startStep = maxOf(1, currentStep - 3)
            val endStep = minOf(totalSteps, currentStep)
            (startStep..endStep).toList()
        }
    }
}

// 모든 스텝을 반환하는 함수 (스크롤용)
fun getAllSteps(totalSteps: Int): List<Int> {
    return (1..totalSteps).toList()
}

// 스탭 개수에 따라 타임라인을 그리는 함수
@Composable
fun RoutineProgressBar(
    stepCount: Int = 4,
    color: Color = Color(0xFFAADC35) // limeGreen
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .padding(horizontal = 20.dp)
    ) {
        val radius = 7.dp.toPx()
        val stroke = 3.dp.toPx()
        val circleStroke = 6.dp.toPx() // 원 테두리용 선
        val y = center.y

        // 원들의 위치 계산 (전체 너비에서 균등 분할)
        val circleSpacing = size.width / (stepCount + 1)

        // 첫 번째 선 (시작점부터 첫 번째 원까지)
        val firstCircleX = circleSpacing
        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(firstCircleX - radius, y),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        // 각 원과 중간 선들
        for (i in 0 until stepCount) {
            val circleX = (i + 1) * circleSpacing

            // 원 그리기
            drawCircle(
                color = color,
                radius = radius,
                center = Offset(circleX, y)
            )
            drawCircle(
                color = Color.White,
                radius = radius - circleStroke / 2,
                center = Offset(circleX, y)
            )

            // 다음 원까지의 선 (마지막 원이 아닌 경우)
            if (i < stepCount - 1) {
                val nextCircleX = (i + 2) * circleSpacing
                drawLine(
                    color = color,
                    start = Offset(circleX + radius, y),
                    end = Offset(nextCircleX - radius, y),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )
            }
        }

        // 마지막 선 (마지막 원부터 끝점까지)
        val lastCircleX = stepCount * circleSpacing
        drawLine(
            color = color,
            start = Offset(lastCircleX + radius, y),
            end = Offset(size.width, y),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

// 목표시간 초 파싱 함수
fun parseTimeToSeconds(timeStr: String): Int {
    return when {
        timeStr.endsWith("m") -> timeStr.dropLast(1).toIntOrNull()?.times(60) ?: 0
        timeStr.endsWith("s") -> timeStr.dropLast(1).toIntOrNull() ?: 0
        else -> 0
    }
}

// 경과 시간 계산 함수
fun formatTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}

// 진동 효과 함수
fun triggerVibration(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}

// 방해금지 모드 제어 함수
fun toggleDoNotDisturb(context: Context, enable: Boolean) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0 이상에서는 알림 정책 접근 권한이 필요
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

            if (notificationManager.isNotificationPolicyAccessGranted) {
                if (enable) {
                    // 방해금지 모드 활성화
                    notificationManager.setInterruptionFilter(android.app.NotificationManager.INTERRUPTION_FILTER_NONE)
                } else {
                    // 방해금지 모드 비활성화
                    notificationManager.setInterruptionFilter(android.app.NotificationManager.INTERRUPTION_FILTER_ALL)
                }
            } else {
                // 권한이 없으면 설정 화면으로 이동
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
    } catch (e: Exception) {
        // 권한이 없거나 설정할 수 없는 경우
        e.printStackTrace()
    }
}



// 앱 실행 함수
fun launchApp(context: Context, packageName: String) {
    try {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun PortraitRoutineFocusScreen(
    focusViewModel: RoutineFocusViewModel,
    sharedViewModel: SharedRoutineViewModel,
    routineId: Int,
    onDismiss: () -> Unit,
    currentStep: Int,
    onFinishConfirmed: (String) -> Unit,
    // Preview용 강제 상태 파라미터 추가
    forceShowFinishPopup: Boolean = false,
    forceShowResultPopup: Boolean = false,
    // 내 기록으로 이동을 위한 네비게이션 콜백 추가
    onNavigateToMyActivity: () -> Unit = {}
) {
    // 강제 테스트 로그
    android.util.Log.e("TEST_LOG", "🔥 PortraitRoutineFocusScreen 시작됨! routineId=$routineId")
    System.out.println("🔥 System.out: PortraitRoutineFocusScreen 시작됨!")

    // 기본 로그 추가
    android.util.Log.d("PortraitRoutineFocusScreen", "🚀 PortraitRoutineFocusScreen 시작됨!")
    android.util.Log.d("PortraitRoutineFocusScreen", "📱 routineId: $routineId, currentStep: $currentStep")

    val context = LocalContext.current

    // intro에서 데이터값 받아오기
    val routineTitle = sharedViewModel.routineTitle.collectAsStateWithLifecycle<String>().value
    val steps =
        sharedViewModel.selectedSteps.collectAsStateWithLifecycle<List<RoutineStepData>>().value

    val routineItems = steps.map { it.name to "${it.duration}m" }

    // 스톱워치 정지 유무
    var isTimerRunning = focusViewModel.isTimerRunning

    // 정지/재생 아이콘 상태
    val isUserPaused = focusViewModel.isUserPaused

    // 전체 누적 시간
    val totalElapsedSeconds = focusViewModel.totalElapsedSeconds

    // step 별 경과 시간 저장
    val elapsedSeconds = focusViewModel.elapsedSeconds

    // 현재 step 저장
    var currentstep = focusViewModel.currentStep

    // 현재 스텝의 목표 시간 문자열 추출 ("15m" 등)
    val currentTimeStr = routineItems.getOrNull(currentstep - 1)?.second ?: "0m"

    // 현재 루틴의 세부 루틴 문자열 추출 ("샤워하기" 등)
    val currentTitle = routineItems.getOrNull(currentstep - 1)?.first ?: ""

    // 문자열을 초 단위로 변환 (예: "15m" → 900초)
    val maxSeconds = parseTimeToSeconds(currentTimeStr)

    // 초과 여부 판별
    var isTimeout = focusViewModel.isTimeout

    // 종료 팝업 상태 저장 (강제 상태 반영)
    var showFinishPopup by remember { mutableStateOf(forceShowFinishPopup) }

    // 결과 팝업 상태 저장 (강제 상태 반영)
    var showResultPopup by remember { mutableStateOf(forceShowResultPopup) }

    // 설정 팝업 상태 저장
    val showSettingsPopup = focusViewModel.isSettingsPopupVisible

    // 다크 모드 on/off 상태 저장
    val isDarkMode = focusViewModel.isDarkMode

    // 방해 금지 모드 on/off 상태 저장
    var isDoNotDisturb by remember { mutableStateOf(false) }

    // 스텝 완료 진동 모드 on/off 상태 저장
    var isStepVibration by remember { mutableStateOf(false) }

    // 가로 모드 on/off 상태 저장
    var isLandscapeMode by remember { mutableStateOf(false) }

    // 메모장 팝업 상태 저장 - focusViewModel에서 가져오기
    val showMemoPad = focusViewModel.showMemoPad

    // 메모장 내용 저장 - focusViewModel에서 실시간으로 가져오기
    var memoText by remember { mutableStateOf("") }

    // currentstep이 변경될 때마다 memoText를 업데이트
    LaunchedEffect(currentstep) {
        memoText = focusViewModel.getStepMemo(currentstep)
    }

    // STEP별 메모는 focusViewModel에서 관리

    // 앱 아이콘 팝업 상태 저장
    val showAppIcons = focusViewModel.isAppIconsVisible

    // 사용앱 리스트 (루틴 생성 시 선택한 앱들)
    val selectedApps = focusViewModel.selectedApps

    // 강제 테스트 로그
    android.util.Log.e("TEST_LOG", "🔥 PortraitRoutineFocusScreen - selectedApps 상태: ${selectedApps.size}개")
    selectedApps.forEachIndexed { index, app ->
        android.util.Log.e("TEST_LOG", "🔥 앱 ${index + 1}: ${app.name} (${app.packageName})")
    }
    System.out.println("🔥 System.out: selectedApps 상태 - ${selectedApps.size}개")

    // 사용앱 데이터 로깅
    android.util.Log.d("PortraitRoutineFocusScreen", "🔍 selectedApps 초기값: ${selectedApps.size}개")
    selectedApps.forEachIndexed { index, app ->
        android.util.Log.d("PortraitRoutineFocusScreen", "   - 초기 앱 ${index + 1}: ${app.name} (${app.packageName})")
    }

    // 테스트용 더미 데이터 (selectedApps가 비어있을 때)
    val testApps = if (selectedApps.isEmpty()) {
        listOf(
            com.konkuk.moru.presentation.routinefeed.data.AppDto("카카오톡", "com.kakao.talk"),
            com.konkuk.moru.presentation.routinefeed.data.AppDto("유튜브", "com.google.android.youtube"),
            com.konkuk.moru.presentation.routinefeed.data.AppDto("인스타그램", "com.instagram.android")
        ).also {
            android.util.Log.d("PortraitRoutineFocusScreen", "🧪 테스트용 더미 앱 데이터 사용: ${it.size}개")
        }
    } else {
        selectedApps.also {
            android.util.Log.d("PortraitRoutineFocusScreen", "✅ 서버에서 받은 사용앱 데이터 사용: ${it.size}개")
        }
    }

    // 강제로 더미 데이터 사용 (테스트용)
    val forceTestApps = listOf(
        com.konkuk.moru.presentation.routinefeed.data.AppDto("카카오톡", "com.kakao.talk"),
        com.konkuk.moru.presentation.routinefeed.data.AppDto("유튜브", "com.google.android.youtube"),
        com.konkuk.moru.presentation.routinefeed.data.AppDto("인스타그램", "com.instagram.android")
    )

    android.util.Log.d("PortraitRoutineFocusScreen", "🧪 강제 테스트 앱 데이터: ${forceTestApps.size}개")

    // 집중 루틴 시작
    LaunchedEffect(Unit) {
        focusViewModel.startFocusRoutine()
    }

    //1초마다 시간 증가,시간 초과 판단
    LaunchedEffect(currentstep) {
        val stepLimit = parseTimeToSeconds(routineItems.getOrNull(currentstep - 1)?.second ?: "0m")
        focusViewModel.setStepLimitFromTimeString(stepLimit)
        focusViewModel.startTimer()
    }

         // 타임라인 스크롤 상태 관리
     val timelineListState = rememberLazyListState()

     // 코루틴 스코프 생성
     val coroutineScope = rememberCoroutineScope()

          // 현재 스텝이 변경될 때마다 타임라인을 해당 스텝으로 스크롤
      LaunchedEffect(currentstep) {
         // 스크롤 상태가 준비된 후에 스크롤 실행
         delay(100)
         // 현재 스텝이 화면에 보이도록 스크롤
         val targetIndex = (currentstep - 1).coerceIn(0, routineItems.size - 1)
         // 스크롤 애니메이션 실행
         timelineListState.animateScrollToItem(targetIndex)
     }

    // 시간 초과 시 진동 효과
    LaunchedEffect(isTimeout) {
        if (isTimeout && isStepVibration) {
            triggerVibration(context)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) colors.black else Color.White
            )
    ) {
        // 시간 초과 시 배경 오버레이 (하단 제외)
        if (isTimeout) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 133.dp) // 하단 영역만 제외
                    .background(colors.limeGreen.copy(alpha = 0.5f))
                    .zIndex(1f)
            )
        }

        // 메인 컨텐츠 영역
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단 컨텐츠 영역
            Column(
                modifier = Modifier
                    .background(if (isDarkMode) colors.black else Color.White)
                    .padding(horizontal = 16.dp)
                    .weight(1f) // 하단 고정 영역을 제외한 나머지 공간 사용
            ) {
                // Top Bar: X 버튼, 설정 버튼
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // x버튼 - 클릭 시 종료 팝업 표시
                    Icon(
                        painter = painterResource(id = R.drawable.ic_x),
                        contentDescription = "종료",
                        tint = if (isDarkMode) Color.White else colors.black,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                showFinishPopup = true
                            }
                    )
                    // 설정 버튼
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gear),
                        contentDescription = "설정",
                        tint = if (isDarkMode) Color.White else colors.black,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                focusViewModel.toggleSettingsPopup()
                            }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                // 루틴명
                Text(
                    text = routineTitle,
                    style = typography.desc_M_16,
                    color = if (isDarkMode) Color.White else colors.black,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // 현재 루틴 태스크 이름
                Text(
                    text = currentTitle,
                    style = typography.title_B_20,
                    color = if (isDarkMode && !isTimeout) colors.limeGreen else if (isDarkMode && isTimeout) Color.White else colors.black,
                    modifier = Modifier.padding(bottom = 9.dp)
                )

                // 중앙 타이머
                Text(
                    text = formatTime(elapsedSeconds),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!isDarkMode && isTimeout) colors.oliveGreen else if (isDarkMode) Color.White else Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                )

                // 정지/재생 버튼
                Icon(
                    painter = painterResource(id = if (!isUserPaused) R.drawable.ic_pause else R.drawable.baseline_play_arrow_24),
                    contentDescription = if (!isUserPaused) "정지" else "시작",
                    tint = if (isDarkMode) Color.White else colors.black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                        .size(45.dp)
                        .clickable {
                            focusViewModel.togglePause()
                        }
                )

                // 타임라인과 다음 버튼을 포함하는 Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(275.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                                         // 타임라인 영역 - LazyColumn으로 스크롤 가능하게 변경
                     LazyColumn(
                         state = timelineListState,
                         modifier = Modifier
                             .weight(1f)
                             .height(200.dp), // 고정 높이 설정
                         verticalArrangement = Arrangement.spacedBy(0.dp), // 간격 제거하여 선이 연결되도록
                         horizontalAlignment = Alignment.Start
                     ) {
                        // 모든 스텝을 표시하여 스크롤 가능하게 함
                        val allSteps = getAllSteps(routineItems.size)

                        items(allSteps) { stepIndex ->
                            val (title, time) = routineItems[stepIndex - 1] // stepIndex는 1부터 시작하므로 -1
                            RoutineTimelineItem(
                                time = time,
                                title = title,
                                index = stepIndex,
                                currentStep = currentstep,
                                isTimeout = isTimeout,
                                isDarkMode = isDarkMode,
                                onStepClick = { clickedStep ->
                                    // 클릭된 스텝으로 이동
                                    if (clickedStep != currentstep) {
                                        val stepTimeString = routineItems.getOrNull(clickedStep - 1)?.second ?: "0m"
                                        focusViewModel.updateCurrentStep(clickedStep)
                                        focusViewModel.setStepLimitFromTimeString(parseTimeToSeconds(stepTimeString))
                                        focusViewModel.resetTimer()
                                        focusViewModel.startTimer()
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // 다음 버튼 영역 - 고정 크기
                    Box(
                        modifier = Modifier
                            .size(width = 100.dp, height = 123.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // 마지막 step 도달 조건
                            val isFinalStep = currentstep == routineItems.size

                            //다음 버튼
                            Box(
                                modifier = Modifier
                                    .size(74.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = if (isFinalStep) R.drawable.enable_check_icon else R.drawable.ic_next_in_circle),
                                    contentDescription = if (isFinalStep) "완료됨" else "다음 루틴으로",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) {
                                            // 다음 step으로 가는 기능
                                            if (!isFinalStep) {
                                                val nextStepTimeString =
                                                    routineItems.getOrNull(currentstep)?.second
                                                        ?: "0m"
                                                focusViewModel.nextStep(nextStepTimeString)
                                                focusViewModel.resumeTimer()

                                                                                                 // 현재 step으로 타임라인 스크롤
                                                 coroutineScope.launch {
                                                     timelineListState.animateScrollToItem(currentstep - 1)
                                                 }
                                            } else {
                                                focusViewModel.pauseTimer()
                                                // 루틴 종료 시 사용앱과 메모장 자동으로 끄기
                                                focusViewModel.hideAppIcons()
                                                focusViewModel.hideMemoPad()
                                                showFinishPopup = true
                                            }
                                        },
                                    tint = if (!isDarkMode && isTimeout) colors.oliveGreen else if (!isDarkMode && !isTimeout) colors.limeGreen else Color.White
                                )
                            }

                            // 시간초과이면 NEXT STEP 텍스트 뜨도록
                            when {
                                isFinalStep -> {
                                    Text(
                                        text = "FINISH !",
                                        style = typography.body_SB_16.copy(fontWeight = FontWeight.Bold),
                                        color = when {
                                            isDarkMode -> Color.White
                                            isFinalStep && isTimeout -> colors.oliveGreen
                                            else -> colors.black
                                        }
                                    )
                                }

                                isTimeout -> {
                                    Text(
                                        text = "NEXT STEP",
                                        style = typography.body_SB_16.copy(fontWeight = FontWeight.Bold),
                                        color = if (isDarkMode) Color.White else colors.oliveGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 하단 고정 영역을 하나의 Column으로 통합
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 앱 아이콘 (조건부 표시)
                if (showAppIcons) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.dp)
                            .background(if (isDarkMode) colors.black else Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 사용앱 아이콘들 (루틴 생성 시 선택한 앱들) - 앱 이름에 맞는 아이콘 표시
                            selectedApps.forEachIndexed { index, appInfo ->
                                android.util.Log.e("TEST_LOG", "🔥 렌더링 중: 앱 ${index + 1} - ${appInfo.name} (${appInfo.packageName})")
                                
                                // 앱 이름에 따라 적절한 아이콘 선택
                                val iconResource = when (appInfo.name.lowercase()) {
                                    "카카오톡" -> R.drawable.kakaotalk_icon
                                    "네이버" -> R.drawable.naver_icon
                                    "인스타그램" -> R.drawable.instagram_icon
                                    "유튜브" -> R.drawable.youtube_icon
                                    else -> R.drawable.ic_default
                                }
                                
                                Image(
                                    painter = painterResource(id = iconResource),
                                    contentDescription = "사용앱 ${appInfo.name}",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            // 앱 바로 실행
                                            launchApp(context, appInfo.packageName)
                                        }
                                )
                            }
                            // 기본 아이콘들 (선택된 앱이 3개 미만인 경우)
                            repeat(3 - selectedApps.size) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_default),
                                    contentDescription = "사용앱 ${it + 1}",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                )
                            }
                        }
                    }
                }

                // 메모장 (조건부 표시)
                if (showMemoPad) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(153.dp)
                            .background(colors.veryLightGray)
                    ) {
                        @OptIn(ExperimentalMaterial3Api::class)
                        TextField(
                            value = memoText,
                            onValueChange = { newText ->
                                // 로컬 상태와 ViewModel 상태 모두 업데이트
                                memoText = newText
                                focusViewModel.saveStepMemo(currentstep, newText)
                                android.util.Log.d("PortraitRoutineFocusScreen", "📝 메모 입력: $newText")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            placeholder = {
                                Text(
                                    text = "STEP ${currentstep} 메모 하기...",
                                    style = typography.desc_M_14,
                                    color = colors.darkGray
                                )
                            },
                            textStyle = typography.body_SB_16.copy(color = colors.black),
                            singleLine = false,
                            maxLines = 5,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                errorContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                cursorColor = colors.black
                            )
                        )
                    }
                }

                // 하단 메뉴 버튼 줄 (항상 표시)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(53.dp)
                        .background(if (isDarkMode) colors.black else colors.veryLightGray)
                        .padding(top = 15.dp, bottom = 14.dp, start = 16.dp, end = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.menu_icon),
                        contentDescription = "메뉴 아이콘",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                focusViewModel.toggleAppIcons()
                            },
                        colorFilter = ColorFilter.tint(if (isDarkMode) colors.mediumGray else colors.black)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.chatting_icon),
                        contentDescription = "채팅 아이콘",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                focusViewModel.toggleMemoPad()
                            },
                        colorFilter = ColorFilter.tint(if (isDarkMode) colors.mediumGray else colors.black)
                    )
                }

                // TOTAL 영역 (항상 표시)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(if (isDarkMode) Color(0xFF383838) else colors.black)
                        .padding(top = 24.dp, bottom = 25.dp, start = 16.dp, end = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL",
                        style = typography.body_SB_16,
                        color = colors.limeGreen
                    )
                    Text(
                        text = formatTotalTime(totalElapsedSeconds + elapsedSeconds),
                        style = typography.body_SB_16,
                        color = colors.limeGreen
                    )
                }
            }
        }

        // 팝업 1(종료 확인 팝업)
        if (showFinishPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000))
                    .zIndex(100f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .width(264.dp)
                        .height(140.dp)
                        .padding(vertical = 6.dp, horizontal = 7.72.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(21.dp))
                    Text(
                        text = "루틴을 종료하시겠습니까?",
                        style = typography.title_B_20,
                        color = colors.black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "종료한 루틴은 내활동에 저장됩니다.",
                        style = typography.title_B_12.copy(fontWeight = FontWeight.Normal),
                        color = colors.darkGray
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.lightGray)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    showFinishPopup = false
                                }
                                .width(123.dp)
                                .height(40.55.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "돌아가기",
                                style = typography.body_SB_16.copy(fontWeight = FontWeight.Bold),
                                color = colors.mediumGray
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.limeGreen)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                                                 ) {
                                     // 현재 STEP 메모 저장
                                     focusViewModel.saveStepMemo(currentstep, memoText)
                                     showResultPopup = true
                                     showFinishPopup = false
                                 }
                                .width(123.dp)
                                .height(40.55.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "종료",
                                style = typography.body_SB_16,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 팝업 2(최종 종료 팝업)
        if (showResultPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000))
                    .zIndex(101f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .width(264.dp)
                        .height(290.dp)
                        .padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier=Modifier.height(14.07.dp))
                    Text(
                        text = "루틴 종료!",
                        style = typography.title_B_20.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.black
                    )
                    Spacer(modifier = Modifier.height(12.04.dp))
                    RoutineProgressBar(
                        stepCount = steps.size,
                        color = colors.limeGreen
                    )
                    Spacer(modifier = Modifier.height(12.04.dp))
                    Text(
                        text = "$currentstep/${routineItems.size}",
                        style = typography.desc_M_14,
                        color = colors.black
                    )
                    Spacer(modifier = Modifier.height(9.03.dp))
                    Column(
                        modifier = Modifier
                            .width(252.dp)
                            .height(102.35.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .padding(
                                top = 8.03.dp,
                                bottom = 8.03.dp,
                                start = 15.99.dp,
                                end = 14.dp
                            )
                    ) {
                        RoutineResultRow(R.drawable.schedule_icon, "루틴", "주말 아침 루틴")
                        Spacer(modifier = Modifier.height(17.56.dp))
                        RoutineResultRow(R.drawable.check_icon_gray, "결과", "완료")
                        Spacer(modifier = Modifier.height(17.56.dp))
                        RoutineResultRow(
                            R.drawable.clock_icon,
                            "시간",
                            formatTime(totalElapsedSeconds + elapsedSeconds)
                        )
                    }
                    Spacer(modifier = Modifier.height(9.03.dp))

                                         Row(
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(horizontal = 5.dp),
                         horizontalArrangement = Arrangement.End,
                         verticalAlignment = Alignment.CenterVertically
                     ) {
                         Box(
                             modifier = Modifier
                                 .size(width = 100.dp, height = 14.05.dp)
                                 .clickable(
                                     indication = null,
                                     interactionSource = remember { MutableInteractionSource() }
                                 ) {
                                     onNavigateToMyActivity()
                                 },
                             contentAlignment = Alignment.Center
                         ) {
                             Text(
                                 text = "내 기록으로 이동",
                                 style = typography.time_R_12.copy(
                                     textDecoration = TextDecoration.Underline
                                 ),
                                 color = colors.mediumGray
                             )
                         }
                     }
                    Spacer(modifier = Modifier.height(6.02.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.limeGreen)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                showResultPopup = false
                                // 집중 루틴 종료
                                focusViewModel.endFocusRoutine()
                                // routineId를 String으로 변환하여 전달
                                onFinishConfirmed(routineId.toString())
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "확인",
                            style = typography.body_SB_16,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 설정 팝업
        if (showSettingsPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x33000000))
                    .zIndex(102f)
                    .clickable { focusViewModel.closeSettingsPopup() },
                contentAlignment = Alignment.TopEnd
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 80.dp, end = 17.dp)
                        .width(149.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFFFFF).copy(alpha = 0.75f))
                        .clickable(
                            onClick = { /* Do nothing */ }
                        )
                ) {
                    SettingSwitchGroup(
                        settings = listOf(
                            Triple(
                                "다크 모드",
                                { focusViewModel.isDarkMode },
                                { focusViewModel.toggleDarkMode() }),
                            Triple("방해 금지 모드", { isDoNotDisturb }, {
                                isDoNotDisturb = it
                                toggleDoNotDisturb(context, it)
                            }),
                            Triple("스텝 완료 진동", { isStepVibration }, {
                                isStepVibration = it
                            }),
                            Triple(
                                "가로 모드",
                                { focusViewModel.isLandscapeMode },
                                { focusViewModel.toggleLandscapeMode() })
                        )
                    )
                }
            }
        }

        // 화면 차단 팝업창
        if (focusViewModel.isScreenBlockPopupVisible) {
            ScreenBlockOverlay(
                selectedApps = selectedApps,
                onDismiss = { focusViewModel.hideScreenBlockPopup() }
            )
        }

        // 온보딩 팝업창
        if (focusViewModel.isOnboardingPopupVisible) {
            FocusOnboardingPopup(
                selectedApps = focusViewModel.selectedApps,
                                 onAppClick = { app ->
                     // 허용된 앱 실행 플래그 설정
                     focusViewModel.setPermittedAppLaunch(true)
                                          // 실제 앱 실행
                      launchApp(context, app.packageName)
                     focusViewModel.hideOnboardingPopup()
                 },
                onOutsideClick = {
                    focusViewModel.hideOnboardingPopup()
                }
            )
        }


    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun PortraitRoutineFocusScreenPreview() {
    val dummyFocusViewModel = remember { RoutineFocusViewModel() }
    val dummySharedViewModel = remember { SharedRoutineViewModel() }

    val dummySteps = listOf(
        RoutineStepData("샤워하기", 1, true), // 3분
        RoutineStepData("청소하기", 10, true), // 10분
        RoutineStepData("밥먹기", 7, true), // 7분
        RoutineStepData("옷갈아입기", 5, true) // 5분
    )

    val dummyApps = listOf(
        AppDto("앱1", "com.example.app1"),
        AppDto("앱2", "com.example.app2"),
        AppDto("앱3", "com.example.app3")
    )

    dummySharedViewModel.setRoutineTitle("주말 아침 루틴")
    dummySharedViewModel.setSelectedSteps(dummySteps)
    dummySharedViewModel.setSelectedApps(dummyApps)

    PortraitRoutineFocusScreen(
        focusViewModel = dummyFocusViewModel,
        sharedViewModel = dummySharedViewModel,
        routineId = 501,
        onDismiss = {},
        currentStep = 1,
        onFinishConfirmed = {},
        forceShowFinishPopup = false,
        forceShowResultPopup = false
    )
}