package com.konkuk.moru.presentation.routinefocus.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.konkuk.moru.R
import com.konkuk.moru.presentation.home.RoutineStepData
import com.konkuk.moru.presentation.home.component.RoutineResultRow
import com.konkuk.moru.presentation.routinefocus.viewmodel.SharedRoutineViewModel
import com.konkuk.moru.presentation.routinefocus.component.RoutineTimelineItem
import com.konkuk.moru.presentation.routinefocus.component.SettingSwitchGroup
import com.konkuk.moru.presentation.routinefocus.viewmodel.RoutineFocusViewModel
import com.konkuk.moru.presentation.routinefocus.screen.formatTotalTime
import com.konkuk.moru.presentation.routinefocus.screen.formatTime
import com.konkuk.moru.presentation.routinefocus.screen.calculateVisibleSteps
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun LandscapeRoutineFocusScreen(
    viewModel: RoutineFocusViewModel = hiltViewModel(),
    sharedViewModel: SharedRoutineViewModel,
    routineId : Int,
    onDismiss: () -> Unit,
    currentStep: Int,
    onFinishConfirmed: (String) -> Unit,
    forceShowFinishPopup: Boolean = false,
    forceShowResultPopup: Boolean = false,
    // 내 기록으로 이동을 위한 네비게이션 콜백 추가
    onNavigateToMyActivity: () -> Unit = {}
) {
    // intro 화면에서 넘기는 데이터들
    val steps = sharedViewModel.selectedSteps.collectAsStateWithLifecycle<List<RoutineStepData>>().value
    val routineTitle = sharedViewModel.routineCategory.collectAsStateWithLifecycle<String>().value
    val routineItems = steps.map { it.name to "${it.duration}m" }

    // 정지/재생 아이콘 상태
    var isUserPaused by remember { mutableStateOf(false) }

    // 전체 누적 시간
    val totalElapsedSeconds = viewModel.totalElapsedSeconds

    // step 별 경과 시간 저장
    val elapsedSeconds = viewModel.elapsedSeconds

    // 현재 step 저장
    var currentstep = viewModel.currentStep

    // 현재 스텝의 목표 시간 문자열 추출 ("15m" 등)
    val currentTimeStr = routineItems.getOrNull(currentstep - 1)?.second ?: "0m"

    // 현재 루틴의 세부 루틴 문자열 추출 ("샤워하기" 등)
    val currentTitle = routineItems.getOrNull(currentstep - 1)?.first ?: ""

    // 시간이 흐르는지 여부 판별
    var isTimerRunning = viewModel.isTimerRunning

    // 초과 여부 판별
    var isTimeout = viewModel.isTimeout

    // 종료 팝업 상태 저장 (강제 상태 반영)
    var showFinishPopup by remember { mutableStateOf(forceShowFinishPopup) }

    // 결과 팝업 상태 저장 (강제 상태 반영)
    var showResultPopup by remember { mutableStateOf(forceShowResultPopup) }

    // 설정 팝업 상태 저장
    var showSettingsPopup by remember { mutableStateOf(false) }

    // 다크 모드 on/off 상태 저장
    val isDarkMode = viewModel.isDarkMode

    // 방해 금지 모드 on/off 상태 저장
    var isDoNotDisturb by remember { mutableStateOf(false) }

    // 스텝 완료 진동 모드 on/off 상태 저장
    var isStepVibration by remember { mutableStateOf(false) }

    // 가로 모드 on/off 상태 저장
    var isLandscapeMode by remember { mutableStateOf(false) }

    // 메모장 팝업 상태 저장
    var showMemoPad by remember { mutableStateOf(false) }

    // 메모장 내용 저장
    var memoText by remember { mutableStateOf("") }

    // 앱 아이콘 팝업 상태 저장
    var showAppIcons by remember { mutableStateOf(false) }

    //1초마다 시간 증가,시간 초과 판단
    LaunchedEffect(currentstep) {
        val stepLimit = parseTimeToSeconds(routineItems.getOrNull(currentstep - 1)?.second ?: "0m")
        viewModel.setStepLimitFromTimeString(stepLimit)
        viewModel.startTimer()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) colors.black else Color.White
            )
    ) {
        // 시간 초과 시 배경 오버레이
        if (isTimeout) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.limeGreen.copy(alpha = 0.5f))
                    .zIndex(1f)
            )
        }

        // 메인 콘텐츠
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // 왼쪽 영역: 타임라인
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (isDarkMode) colors.black else Color.White)
                    .padding(16.dp)
            ) {
                // Top Bar: X 버튼, 루틴명, 설정 버튼
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // x버튼
                    Icon(
                        painter = painterResource(id = R.drawable.ic_x),
                        contentDescription = "종료",
                        tint = if (isDarkMode) Color.White else colors.black,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                            ) {
                                onDismiss()
                            }
                    )
                    
                    // 루틴명
                    Text(
                        text = routineTitle,
                        style = typography.desc_M_16,
                        color = if (isDarkMode) Color.White else colors.black
                    )
                    
                    // 설정 버튼
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gear),
                        contentDescription = "설정",
                        tint = if (isDarkMode) Color.White else colors.black,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                showSettingsPopup = !showSettingsPopup
                            }
                    )
                }

                // 총 시간 표시
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = formatTotalTime(totalElapsedSeconds + elapsedSeconds),
                        style = typography.body_SB_16,
                        color = colors.limeGreen
                    )
                }

                // 타임라인 아이템들
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 현재 step에 따라 보여줄 step들을 동적으로 계산
                    val visibleSteps = calculateVisibleSteps(currentstep, routineItems.size)
                    
                    visibleSteps.forEach { stepIndex ->
                        val (title, time) = routineItems[stepIndex - 1] // stepIndex는 1부터 시작하므로 -1
                        RoutineTimelineItem(
                            time = time,
                            title = title,
                            index = stepIndex,
                            currentStep = currentstep,
                            isTimeout = isTimeout,
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }

            // 중앙 영역: 타이머
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (isDarkMode) colors.black else Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 중앙 타이머
                Text(
                    text = formatTime(elapsedSeconds),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!isDarkMode && isTimeout) colors.oliveGreen else if (isDarkMode) Color.White else Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 정지/재생 버튼
                Icon(
                    painter = painterResource(id = if (!isUserPaused) R.drawable.ic_pause else R.drawable.baseline_play_arrow_24),
                    contentDescription = if (!isUserPaused) "정지" else "시작",
                    tint = if (isDarkMode) Color.White else colors.black,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            viewModel.togglePause()
                        }
                )
            }

                         // 오른쪽 영역: NEXT STEP 버튼과 하단 메뉴
             Column(
                 modifier = Modifier
                     .weight(1f)
                     .fillMaxHeight()
                     .background(if (isDarkMode) colors.black else Color.White)
                     .padding(16.dp),
                 horizontalAlignment = Alignment.CenterHorizontally,
                 verticalArrangement = Arrangement.SpaceBetween
             ) {
                 // 상단 여백
                 Spacer(modifier = Modifier.height(60.dp))
                 
                 // 다음 버튼
                 Column(
                     horizontalAlignment = Alignment.CenterHorizontally
                 ) {
                     // 마지막 step 도달 조건
                     val isFinalStep = currentstep == routineItems.size

                     // 다음 버튼
                     Box(
                         modifier = Modifier.size(60.dp),
                         contentAlignment = Alignment.Center
                     ) {
                         Icon(
                             painter = painterResource(id = if (isFinalStep) R.drawable.enable_check_icon else R.drawable.ic_next_in_circle),
                             contentDescription = if (isFinalStep) "완료됨" else "다음 루틴으로",
                             modifier = Modifier
                                 .fillMaxSize()
                                 .clickable(
                                     indication = null,
                                     interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                                 ) {
                                     // 다음 step으로 가는 기능
                                     if (!isFinalStep) {
                                         val nextStepTimeString =
                                             routineItems.getOrNull(currentstep)?.second ?: "0m"
                                         viewModel.nextStep(nextStepTimeString)
                                         viewModel.resumeTimer()
                                     } else {
                                         viewModel.pauseTimer()
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
                                 style = typography.body_SB_16.copy(fontWeight = FontWeight.Bold),
                                 color = when {
                                     isDarkMode -> Color.White
                                     isFinalStep && isTimeout -> colors.oliveGreen
                                     else -> colors.black
                                 },
                                 textAlign = androidx.compose.ui.text.style.TextAlign.Center
                             )
                         }
                         isTimeout -> {
                             Text(
                                 text = "NEXT STEP",
                                 style = typography.body_SB_16.copy(fontWeight = FontWeight.Bold),
                                 color = if (isDarkMode) Color.White else colors.oliveGreen,
                                 textAlign = androidx.compose.ui.text.style.TextAlign.Center
                             )
                         }
                         else -> {
                             Text(
                                 text = "NEXT STEP",
                                 style = typography.body_SB_16.copy(fontWeight = FontWeight.Bold),
                                 color = if (isDarkMode) Color.White else colors.black,
                                 textAlign = androidx.compose.ui.text.style.TextAlign.Center
                             )
                         }
                     }
                 }

                // 하단 영역
                Column {
                    // 앱 아이콘 (메모장보다 위에)
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
                                repeat(3) {
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

                    // 메모장
                    if (showMemoPad) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(colors.veryLightGray)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "메모 하기...",
                                style = typography.desc_M_14,
                                color = colors.darkGray
                            )
                        }
                    }

                    // 하단 메뉴 버튼
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.menu_icon),
                            contentDescription = "메뉴 아이콘",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    showAppIcons = !showAppIcons
                                },
                            colorFilter = ColorFilter.tint(if (isDarkMode) colors.mediumGray else colors.black)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.chatting_icon),
                            contentDescription = "채팅 아이콘",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    showMemoPad = !showMemoPad
                                },
                            colorFilter = ColorFilter.tint(if (isDarkMode) colors.mediumGray else colors.black)
                        )
                    }

                    // TOTAL 영역
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDarkMode) Color(0xFF383838) else colors.black)
                            .padding(horizontal = 16.dp),
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
        }

        // 팝업 1(종료 확인 팝업)
        if (showFinishPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000))
                    .zIndex(1f),
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
                                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
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
                                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
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
                    .zIndex(2f),
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
                                     interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
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
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                            ) {
                                showResultPopup = false
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
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color(0x33000000))
                    .zIndex(10f)
                    .clickable { showSettingsPopup = false },
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
                                { viewModel.isDarkMode },
                                { viewModel.toggleDarkMode() }),
                            Triple("방해 금지 모드", { isDoNotDisturb }, { isDoNotDisturb = it }),
                            Triple("스텝 완료 진동", { isStepVibration }, { isStepVibration = it }),
                            Triple(
                                "가로 모드",
                                { viewModel.isLandscapeMode },
                                { viewModel.toggleLandscapeMode() })
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
        RoutineStepData("옷갈아입기", 5, true) // 5분
    )

    dummySharedViewModel.setRoutineCategory("주말 아침 루틴")
    dummySharedViewModel.setSelectedSteps(dummySteps)

    // Preview용 더미 ViewModel 생성
    val dummyViewModel = remember { RoutineFocusViewModel() }

    LandscapeRoutineFocusScreen(
        viewModel = dummyViewModel,
        sharedViewModel = dummySharedViewModel,
        routineId = 501,
        onDismiss = {},
        currentStep = 1,
        onFinishConfirmed = {},
        forceShowFinishPopup = false,
        forceShowResultPopup = false
    )
}
