package com.konkuk.moru.presentation.routinefocus.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.konkuk.moru.R
import com.konkuk.moru.core.component.Switch.StatusBarMock
import com.konkuk.moru.presentation.home.component.RoutineResultRow
import com.konkuk.moru.presentation.routinefocus.component.RoutineTimelineItem
import com.konkuk.moru.presentation.routinefocus.component.SettingSwitchGroup
import com.konkuk.moru.presentation.routinefocus.viewmodel.RoutineFocusViewModel
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography


fun formatTotalTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02dm %02ds", minutes, secs)
}

// 스탭 개수에 따라 그리는 함수
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

@Composable
fun PortraitRoutineFocusScreen(
    viewModel: RoutineFocusViewModel = viewModel(),
    onDismiss: () -> Unit,
    routineItems: List<Pair<String, String>>, //(루틴명,소요시간)
    currentStep: Int,
    // Preview용 강제 상태 파라미터 추가
    forceShowFinishPopup: Boolean = false,
    forceShowResultPopup: Boolean = false
) {
    // 스톱워치 정지 유무
    var isTimerRunning = viewModel.isTimerRunning

    // 정지/재생 아이콘 상태
    val isUserPaused = viewModel.isUserPaused

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

    // 문자열을 초 단위로 변환 (예: "15m" → 900초)
    val maxSeconds = parseTimeToSeconds(currentTimeStr)

    // 초과 여부 판별
    var isTimeout = viewModel.isTimeout

    // 종료 팝업 상태 저장 (강제 상태 반영)
    var showFinishPopup by remember { mutableStateOf(forceShowFinishPopup) }

    // 결과 팝업 상태 저장 (강제 상태 반영)
    var showResultPopup by remember { mutableStateOf(forceShowResultPopup) }

    // 설정 팝업 상태 저장
    val showSettingsPopup = viewModel.isSettingsPopupVisible

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
    val showAppIcons = viewModel.isAppIconsVisible

    //1초마다 시간 증가,시간 초과 판단
    LaunchedEffect(currentstep) {
        val stepLimit = parseTimeToSeconds(routineItems.getOrNull(currentstep - 1)?.second ?: "0m")
        viewModel.setStepLimitFromTimeString(stepLimit)
        viewModel.startTimer()
    }


    // 메인 컨텐츠를 Box로 감싸기
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) colors.black else Color.White
            )
    ) {
        // ✅ 1. 시간 초과 시 배경 오버레이 (하단 제외)
        if (isTimeout) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp, bottom = 133.dp) // 하단 영역 제외
                    .background(colors.limeGreen.copy(alpha = 0.5f))
                    .zIndex(1f)
            )
        }

        // 상단 StatusBar, 루틴 타이틀, 타이머, 메모장
        Column() {
            // 상단 상태 바
            if (isDarkMode)
                StatusBarMock(isDarkMode = true)
            else
                StatusBarMock(isDarkMode = false)

            //시간초과시의 영역만 칠하기 위해 또 Column에
            Column(
                modifier = Modifier
                    .background(if (isDarkMode) colors.black else Color.White),
            ) {
                // Top Bar: X 버튼, 설정 버튼
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                            .clickable { onDismiss() }
                    )
                    // 설정 버튼
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gear),
                        contentDescription = "설정",
                        tint = if (isDarkMode) Color.White else colors.black,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                viewModel.toggleSettingsPopup()
                            }
                    )
                }
                // 루틴명
                Text(
                    text = "주말 아침 루틴",
                    style = typography.desc_M_16,
                    color = if (isDarkMode) Color.White else colors.black,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                // 현재 루틴 태스크 이름
                Text(
                    text = currentTitle,
                    style = typography.title_B_20,
                    color = if (isDarkMode && !isTimeout) colors.limeGreen else if (isDarkMode && isTimeout) Color.White else colors.black,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Spacer(modifier = Modifier.height(9.dp))

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

                // 정지 버튼
                Icon(
                    painter = painterResource(id = if (!isUserPaused) R.drawable.ic_pause else R.drawable.baseline_play_arrow_24),
                    contentDescription = if (!isUserPaused) "정지" else "시작",
                    tint = if (isDarkMode) Color.White else colors.black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                        .size(45.dp)
                        .clickable {
                            viewModel.togglePause()
                        }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 타임라인
                    Column(
                        modifier = Modifier
                            .padding(start = 13.dp, top = 18.dp)
                            .height(275.dp)
                    ) {
                        routineItems.forEachIndexed { rawIndex, (title, time) ->
                            val index = rawIndex + 1
                            RoutineTimelineItem(
                                time = time,
                                title = title,
                                index = index, // 1,2,3,4,...
                                currentStep = currentstep,
                                isTimeout = isTimeout,
                                isDarkMode = isDarkMode
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(53.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 139.dp, height = 123.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // 마지막 step 도달 조건
                            val isFinalStep = currentstep == routineItems.size

                            //다음 버튼
                            Box(
                                modifier = Modifier
                                    .size(width = 138.dp, height = 88.dp)
                                    .padding(horizontal = 32.dp, vertical = 7.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = if (isFinalStep) R.drawable.enable_check_icon else R.drawable.ic_next_in_circle),
                                    contentDescription = if (isFinalStep) "완료됨" else "다음 루틴으로",
                                    modifier = Modifier
                                        .size(74.dp)
                                        .clickable {
                                            // 다음 step으로 가는 기능
                                            if (!isFinalStep) {
                                                val nextStepTimeString =
                                                    routineItems.getOrNull(currentstep)?.second
                                                        ?: "0m"
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

                            //시간초과이면 NEXT STEP 텍스트 뜨도록
                            Box(
                                modifier = Modifier
                                    .size(width = 139.dp, height = 35.dp),
                                contentAlignment = Alignment.Center,
                            ) {
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
            }
        }

        // 하단 메뉴 버튼 + TOTAL 영역을 고정하기 위해 따로 Column 씌움
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .wrapContentHeight()
        ) {
            // ✅ 1. 앱 아이콘 (항상 위에)
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

            // ✅ 2. 메모장 (항상 아래에)
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
                        onValueChange = { memoText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        placeholder = {
                            Text(
                                text = "메모 하기...",
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

            // ✅ 3. 하단 메뉴 버튼 줄
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
                            viewModel.toggleAppIcons()
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

            // ✅ 4. TOTAL 영역
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


        // 팝업 1(종료 확인 팝업) - 메인 컨텐츠와 별도로 위치
        if (showFinishPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000))
                    .zIndex(1f), // z-index 추가
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
                    Spacer(modifier = Modifier.size(21.dp))
                    Text(
                        text = "루틴을 종료하시겠습니까?",
                        style = typography.title_B_20,
                        color = colors.black
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "종료한 루틴은 내활동에 저장됩니다.",
                        style = typography.title_B_12.copy(fontWeight = FontWeight.Normal),
                        color = colors.darkGray
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.lightGray)
                                .clickable {
                                    showFinishPopup = false
                                    // 돌아가기 클릭 시 타이머 다시 시작
                                    isTimerRunning = true
                                }
                                .width(123.dp)
                                .height(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "돌아가기",
                                style = typography.body_SB_16,
                                color = colors.mediumGray
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.limeGreen)
                                .clickable {
                                    showFinishPopup = false
                                    showResultPopup = true
                                }
                                .width(123.dp)
                                .height(40.dp),
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
            //전체 오버레이
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
                    //진행도(spacer도 줘야함)
                    Spacer(modifier = Modifier.height(12.04.dp))
                    RoutineProgressBar(
                        stepCount = currentstep, //스탭 개수
                        color = colors.limeGreen
                    )
                    Spacer(modifier = Modifier.height(12.04.dp))
                    //루틴 갯수
                    Text(
                        text = "$currentstep/${routineItems.size}",
                        style = typography.desc_M_14,
                        color = colors.black
                    )
                    Spacer(modifier = Modifier.height(9.03.dp))
                    //정보 박스
                    Column(
                        modifier = Modifier
                            .width(252.dp)
                            .height(102.35.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .padding(top = 8.03.dp, bottom = 8.03.dp, start = 15.99.dp, end = 14.dp)
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

                    //내 기록으로 이동
                    Spacer(modifier = Modifier.width(154.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                        //박스 안에 텍스트 좌우 여백 같게, 상하 여백 같게 하고 싶어요
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 100.dp, height = 14.05.dp),
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

                    //확인 버튼
                    Box(
                        modifier = Modifier
                            .width(253.dp)
                            .height(42.15.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.limeGreen)
                            .clickable {
                                showResultPopup = false
                                onDismiss()
                            },
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
        // 설정 팝업 (showSettingsPopup)은 이제 여기에 위치합니다.
        if (showSettingsPopup) {
            //반투명 오버레이
            Box(
                modifier = Modifier
                    .fillMaxWidth() // 전체 너비 채우기
                    .fillMaxHeight() // 전체 높이 채우기
                    .padding(bottom = 133.dp) // 하단 메뉴(53dp) + TOTAL(80dp) = 133dp 제외
                    .background(Color(0x33000000)) // 반투명 검정 배경
                    .zIndex(10f) // 다른 UI 위에 오도록 zIndex 조정 (가장 위에)
                    .clickable { viewModel.closeSettingsPopup() }, // 오버레이 바깥 클릭 시 팝업 닫기
                contentAlignment = Alignment.TopEnd // 내용을 우측 상단에 정렬
            ) {
                // 설정 스위치 그룹 컨테이너
                Column(
                    modifier = Modifier
                        .padding(top = 80.dp, end = 17.dp) // 상단바 아래, 우측에 패딩
                        .width(149.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFFFFF).copy(alpha = 0.75f))
                        .clickable(
                            // 팝업 내부 클릭 시 팝업이 닫히지 않도록 clickable을 추가하고 아무 동작 안함
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
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun PortraitRoutineFocusScreenPreview() {
    val dummyItems = listOf(
        "샤워하기" to "3s",
        "청소하기" to "10s",
        "밥먹기" to "7s",
        "옷갈아입기" to "5s"
    )

    PortraitRoutineFocusScreen(
        onDismiss = {},
        routineItems = dummyItems,
        currentStep = 1,
        forceShowFinishPopup = false,
        forceShowResultPopup = false
    )
}
