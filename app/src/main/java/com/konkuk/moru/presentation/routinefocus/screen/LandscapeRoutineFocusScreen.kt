package com.konkuk.moru.presentation.routinefocus.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.layout.onGloballyPositioned
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.layout.onGloballyPositioned
import com.konkuk.moru.R
import com.konkuk.moru.presentation.home.RoutineStepData
import com.konkuk.moru.presentation.home.component.RoutineResultRow
import com.konkuk.moru.presentation.routinefocus.component.RoutineTimelineItem
import com.konkuk.moru.presentation.routinefocus.component.ScreenBlockOverlay
import com.konkuk.moru.presentation.routinefocus.component.AppIcon
import com.konkuk.moru.presentation.routinefocus.component.SettingSwitchGroup
import com.konkuk.moru.presentation.routinefocus.viewmodel.RoutineFocusViewModel
import com.konkuk.moru.presentation.routinefocus.viewmodel.SharedRoutineViewModel
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import com.konkuk.moru.presentation.routinefocus.screen.parseTimeToSeconds
import com.konkuk.moru.presentation.routinefocus.screen.formatTotalTime
import com.konkuk.moru.presentation.routinefocus.screen.formatTime

// 스크롤 가능한 타임라인을 위한 고정 스텝 높이
const val TIMELINE_STEP_HEIGHT = 40
const val TIMELINE_STEP_SPACING = 6
const val MAX_VISIBLE_STEPS = 3

@Composable
fun LandscapeRoutineFocusScreen(
    focusViewModel: RoutineFocusViewModel,
    sharedViewModel: SharedRoutineViewModel,
    routineId: Int,
    onDismiss: () -> Unit,
    currentStep: Int,
    onFinishConfirmed: (String) -> Unit,
    forceShowFinishPopup: Boolean = false,
    forceShowResultPopup: Boolean = false,
    // 내 기록으로 이동을 위한 네비게이션 콜백 추가
    onNavigateToMyActivity: () -> Unit = {}
) {
    val context = LocalContext.current
    
    // 기본 로그 추가
    android.util.Log.d("LandscapeRoutineFocusScreen", "🚀 LandscapeRoutineFocusScreen 시작됨!")
    android.util.Log.d("LandscapeRoutineFocusScreen", "📱 routineId: $routineId, currentStep: $currentStep")
    
    // intro 화면에서 넘기는 데이터들
    val steps =
        sharedViewModel.selectedSteps.collectAsStateWithLifecycle<List<RoutineStepData>>().value
    val routineTitle = sharedViewModel.routineTitle.collectAsStateWithLifecycle<String>().value
    val routineItems = steps.map { it.name to "${it.duration}m" }

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

    // 시간이 흐르는지 여부 판별
    var isTimerRunning = focusViewModel.isTimerRunning

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
    var memoText by remember { 
        val initialMemo = focusViewModel.getStepMemo(currentstep)
        android.util.Log.d("LandscapeRoutineFocusScreen", "📝 memoText 초기화: '$initialMemo' (스텝: $currentstep)")
        mutableStateOf(initialMemo) 
    }
    
    // currentstep이 변경될 때마다 memoText를 업데이트
    LaunchedEffect(currentstep) {
        val savedMemo = focusViewModel.getStepMemo(currentstep)
        memoText = savedMemo
        Log.d("LandscapeRoutineFocusScreen", "🔄 스텝 $currentstep 변경: 메모 업데이트 '$savedMemo'")
    }

    // 앱 아이콘 팝업 상태 저장 - focusViewModel에서 가져오기
    val showAppIcons = focusViewModel.showAppIcons

    // 사용앱 리스트 (루틴 생성 시 선택한 앱들)
    val selectedApps = focusViewModel.selectedApps
    
    // 테스트용 더미 데이터 (selectedApps가 비어있을 때)
    val testApps = if (selectedApps.isEmpty()) {
        listOf(
            com.konkuk.moru.presentation.routinefeed.data.AppDto("카카오톡", "com.kakao.talk"),
            com.konkuk.moru.presentation.routinefeed.data.AppDto("유튜브", "com.google.android.youtube"),
            com.konkuk.moru.presentation.routinefeed.data.AppDto("인스타그램", "com.instagram.android")
        ).also {
            android.util.Log.d("LandscapeRoutineFocusScreen", "🧪 테스트용 더미 앱 데이터 사용: ${it.size}개")
        }
    } else {
        selectedApps
    }

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
     
     // 현재 스텝이 변경될 때마다 해당 스텝의 메모 불러오기 (디버깅용)
     LaunchedEffect(currentstep) {
         val savedMemo = focusViewModel.getStepMemo(currentstep)
         android.util.Log.d("LandscapeRoutineFocusScreen", "📖 스텝 $currentstep 메모 불러오기: $savedMemo")
     }
     
     // 현재 스텝이 변경될 때마다 타임라인을 해당 스텝으로 스크롤
     LaunchedEffect(currentstep) {
         // 스크롤 상태가 준비된 후에 스크롤 실행
         delay(100)
         // 현재 스텝이 3개씩 보이는 화면에서 적절한 위치에 오도록 계산
         val targetIndex = (currentstep - 1).coerceIn(0, routineItems.size - 1)
         // 스크롤 애니메이션 실행
         timelineListState.animateScrollToItem(targetIndex)
     }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) colors.black else Color.White
            )
    ) {
        // 시간 초과 시 배경 오버레이 (하단 바 제외)
        if (isTimeout) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp) // 고정된 하단 바 높이
                    .background(colors.limeGreen.copy(alpha = 0.5f))
                    .zIndex(1f)
            )
        }

        // 메인 콘텐츠
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 상단 영역: X버튼, 설정 버튼, 루틴 제목, 총 소요시간
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // 왼쪽: X버튼과 루틴 제목
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    // X버튼 - 클릭 시 종료 팝업 표시
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

                    Spacer(modifier = Modifier.height(14.dp))

                    // 루틴 제목
                    Text(
                        text = routineTitle,
                        style = typography.desc_M_16,
                        color = if (isDarkMode) Color.White else colors.black
                    )

                    // 현재 루틴 태스크 이름
                    Text(
                        text = currentTitle,
                        style = typography.title_B_20,
                        color = if (isDarkMode && !isTimeout) colors.limeGreen else if (isDarkMode && isTimeout) Color.White else colors.black,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // 오른쪽: 설정 버튼과 총 소요시간
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 35.dp) // 오른쪽 벽에서 조금 멀어지도록
                ) {
                    // 설정 버튼 - 가로모드에서 하단 바에 가려지지 않도록 더 위쪽으로 배치
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

                    Spacer(modifier = Modifier.height(14.dp))

                    // 총 소요시간
                    Text(
                        text = formatTotalTime(totalElapsedSeconds + elapsedSeconds),
                        style = typography.body_SB_16,
                        color = colors.limeGreen
                    )
                }
            }

            // 중앙 영역: 타임라인, 타이머, NEXT STEP 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                                 // 왼쪽: 타임라인 - 한 화면에 3개 정도 보이고 스크롤 가능
                 LazyColumn(
                     state = timelineListState,
                     modifier = Modifier
                         .weight(0.25f)
                         .height((60 * 3).dp), // 3개 스텝만 보이도록 정확한 높이 (60dp * 3)
                     verticalArrangement = Arrangement.spacedBy(0.dp) // 간격 제거하여 선이 연결되도록
                 ) {
                    items(routineItems.size) { index ->
                        val (title, time) = routineItems[index]
                        Box(
                            modifier = Modifier.height(60.dp) // RoutineTimelineItem의 Canvas 높이와 정확히 일치
                        ) {
                            RoutineTimelineItem(
                                time = time,
                                title = title,
                                index = index + 1,
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
                }

                // 중앙: 타이머와 정지/재생 버튼
                Column(
                    modifier = Modifier
                        .weight(0.55f)
                        .padding(top = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    // 중앙 타이머
                    Text(
                        text = formatTime(elapsedSeconds),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isDarkMode && isTimeout) colors.oliveGreen else if (isDarkMode) Color.White else Color.Black,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 정지/재생 버튼
                    Icon(
                        painter = painterResource(id = if (!isUserPaused) R.drawable.ic_pause else R.drawable.baseline_play_arrow_24),
                        contentDescription = if (!isUserPaused) "정지" else "시작",
                        tint = if (isDarkMode) Color.White else colors.black,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                focusViewModel.togglePause()
                            }
                    )
                }

                // 오른쪽: NEXT STEP 버튼
                Column(
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    // 마지막 step 도달 조건
                    val isFinalStep = currentstep == routineItems.size

                    // 다음 버튼
                    Box(
                        modifier = Modifier.size(74.dp)
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
                                             routineItems.getOrNull(currentstep)?.second ?: "0m"
                                         focusViewModel.nextStep(nextStepTimeString)
                                         focusViewModel.resumeTimer()
                                         
                                         // 현재 step으로 타임라인 스크롤
                                         coroutineScope.launch {
                                             timelineListState.animateScrollToItem(currentstep - 1)
                                         }
                                     } else {
                                         focusViewModel.pauseTimer()
                                         showFinishPopup = true
                                     }
                                },
                            tint = if (!isDarkMode && isTimeout) colors.oliveGreen else if (!isDarkMode && !isTimeout) colors.limeGreen else Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 버튼 아래 텍스트
                    when {
                        isFinalStep -> {
                            Text(
                                text = "FINISH !",
                                style = typography.body_SB_16.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
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
                                style = typography.body_SB_16.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = if (isDarkMode) Color.White else colors.oliveGreen
                            )
                        }
                    }
                }
            }

            // 하단 고정 영역을 하나의 Column으로 통합 (세로모드와 동일한 구조)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // 앱 아이콘 (가장 위에 표시 - 조건부)
                if (showAppIcons) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(if (isDarkMode) colors.black else Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 사용앱 아이콘들 (루틴 생성 시 선택한 앱들) - 앱 이름에 맞는 아이콘 표시
                            selectedApps.forEachIndexed { index, appInfo ->
                                android.util.Log.e("TEST_LOG", "🔥 가로모드 렌더링 중: 앱 ${index + 1} - ${appInfo.name} (${appInfo.packageName})")
                                
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
                                        .size(36.dp)
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
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                )
                            }
                        }
                    }
                }

                                 // 메모장 (사용앱 아래에 표시 - 조건부)
                 if (showMemoPad) {
                     Box(
                         modifier = Modifier
                             .fillMaxWidth()
                             .height(120.dp) // 높이를 120dp로 늘려서 더 많은 텍스트 표시
                             .background(colors.veryLightGray)
                             .zIndex(10f) // 메모장이 다른 요소들 위에 표시되도록 zIndex 설정
                             .imePadding() // 키보드가 열릴 때만 메모장을 위로 밀림
                     ) {
                        @OptIn(ExperimentalMaterial3Api::class)
                        androidx.compose.material3.TextField(
                            value = memoText.also { 
                                android.util.Log.d("LandscapeRoutineFocusScreen", "📝 TextField value: '$it'")
                            },
                            onValueChange = { newText ->
                                // 로컬 상태와 ViewModel 상태 모두 업데이트
                                android.util.Log.d("LandscapeRoutineFocusScreen", "📝 onValueChange 호출: '$newText' (이전: '$memoText')")
                                memoText = newText
                                focusViewModel.saveStepMemo(currentstep, newText)
                                android.util.Log.d("LandscapeRoutineFocusScreen", "📝 메모 입력 완료: $newText")
                            },
                            placeholder = {
                                Text(
                                    text = "step $currentstep 메모 하기...",
                                    style = typography.desc_M_14,
                                    color = colors.darkGray
                                )
                            },
                                                         modifier = Modifier
                                 .fillMaxWidth()
                                 .height(100.dp) // 높이를 늘려서 더 많은 텍스트 표시
                                 .padding(16.dp)
                                 .onGloballyPositioned { coordinates ->
                                     android.util.Log.d("LandscapeRoutineFocusScreen", "📝 TextField 위치: ${coordinates.size}")
                                 },
                            textStyle = typography.body_SB_16.copy(color = colors.black),
                                                         singleLine = false, // 여러 줄 입력 가능
                             maxLines = 6, // 최대 6줄까지 입력 가능
                            colors = androidx.compose.material3.TextFieldDefaults.colors(
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

                // 하단 메뉴 버튼 줄 (항상 고정 표시)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(if (isDarkMode) colors.black else colors.veryLightGray)
                        .padding(start = 16.dp, end = 60.dp),
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
                    Spacer(modifier = Modifier.height(14.07.dp))
                    Text(
                        text = "루틴 종료!",
                        style = typography.title_B_20.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.black
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
                            .padding(top = 8.03.dp, bottom = 8.03.dp, start = 15.99.dp, end = 14.dp)
                    ) {
                        RoutineResultRow(R.drawable.schedule_icon, "루틴", routineTitle)
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

        // 화면 차단 팝업창
        if (focusViewModel.isScreenBlockPopupVisible) {
            ScreenBlockOverlay(
                selectedApps = focusViewModel.selectedApps,
                onDismiss = { focusViewModel.hideScreenBlockPopup() }
            )
        }



        // 설정 팝업
        if (showSettingsPopup) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color(0x33000000))
                    .zIndex(102f)
                    .padding(end = 35.dp)
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
                            Triple("방해 금지 모드", { isDoNotDisturb }, { isDoNotDisturb = it }),
                            Triple("스텝 완료 진동", { isStepVibration }, { isStepVibration = it }),
                            Triple(
                                "가로 모드",
                                { focusViewModel.isLandscapeMode },
                                { focusViewModel.toggleLandscapeMode() })
                        )
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 400
)
@Composable
private fun LandscapeRoutineFocusScreenPreview() {
    val dummySharedViewModel = remember { SharedRoutineViewModel() }

    val dummySteps = listOf(
        RoutineStepData("샤워하기", 3, true), // 3분
        RoutineStepData("청소하기", 10, true), // 10분
        RoutineStepData("밥먹기", 7, true), // 7분
        RoutineStepData("옷갈아입기", 5, true), // 5분
        RoutineStepData("운동하기", 15, true), // 15분
        RoutineStepData("독서하기", 20, true), // 20분
        RoutineStepData("정리하기", 8, true) // 8분
    )

    dummySharedViewModel.setRoutineTitle("주말 아침 루틴")
    dummySharedViewModel.setSelectedSteps(dummySteps)

    // Preview용 더미 ViewModel 생성
    val dummyViewModel = remember { RoutineFocusViewModel() }

    LandscapeRoutineFocusScreen(
        focusViewModel = dummyViewModel,
        sharedViewModel = dummySharedViewModel,
        routineId = 501,
        onDismiss = {},
        currentStep = 1,
        onFinishConfirmed = {},
        forceShowFinishPopup = false,
        forceShowResultPopup = false
    )
}