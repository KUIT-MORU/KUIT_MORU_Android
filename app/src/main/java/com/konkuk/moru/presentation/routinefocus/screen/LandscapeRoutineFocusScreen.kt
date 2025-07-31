package com.konkuk.moru.presentation.routinefocus.screen

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.konkuk.moru.R
import com.konkuk.moru.presentation.home.component.RoutineResultRow
import com.konkuk.moru.presentation.routinefocus.component.RoutineTimelineItem
import com.konkuk.moru.presentation.routinefocus.component.SettingSwitchGroup
import com.konkuk.moru.presentation.routinefocus.viewmodel.RoutineFocusViewModel
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography


@Composable
fun LandscapeRoutineFocusScreen(
    viewModel: RoutineFocusViewModel = viewModel(),
    onDismiss: () -> Unit,
    routineItems: List<Pair<String, String>>,
    currentStep: Int,
    forceShowFinishPopup: Boolean = false,
    forceShowResultPopup: Boolean = false
) {
    // 스톱워치 정지 유무
    var isTimerRunning = viewModel.isTimerRunning

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

    // 문자열을 초 단위로 변환 (예: "15m" → 900초)
    val maxSeconds = parseTimeToSeconds(currentTimeStr)

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

    // 메모장 팝업 상태 저장
    var showMemoPad by remember { mutableStateOf(false) }

    // 메모장 내용 저장
    var memoText by remember { mutableStateOf("") }

    // 앱 아이콘 팝업 상태 저장
    var showAppIcons by remember { mutableStateOf(false) }

    // 가로/세로 모드 상태 저장
    val isLandscapeMode = viewModel.isLandscapeMode


    // 타이머 동작/멈춤
    LaunchedEffect(currentstep) {
        val stepLimit = parseTimeToSeconds(routineItems.getOrNull(currentstep - 1)?.second ?: "0m")
        viewModel.setStepLimitFromTimeString(stepLimit)
        viewModel.startTimer()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_x),
                contentDescription = "종료",
                tint = if (isDarkMode) Color.White else colors.black,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onDismiss() }
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_gear),
                contentDescription = "설정",
                tint = if (isDarkMode) Color.White else colors.black,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showSettingsPopup = !showSettingsPopup }
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "주말 아침 루틴",
                style = typography.desc_M_16,
                color = if (isDarkMode) Color.White else colors.black,
            )
            Text(
                text = formatTotalTime(totalElapsedSeconds + elapsedSeconds),
                style = typography.body_SB_16,
                color = colors.limeGreen
            )
        }
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            // ✅ 타임라인 영역 - 현재 step부터 4개 표시
            Box(
                modifier = Modifier
                    .height(200.dp) // 4개 아이템 높이 (50dp * 4)
                    .width(220.dp)
                    .padding(start = 31.dp)
            ) {
                // 현재 step부터 4개를 표시 (currentstep을 맨 위에)
                val startIndex = currentstep - 1 // 0-based index로 변환
                val visibleItems = routineItems.drop(startIndex).take(4)

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    visibleItems.forEachIndexed { visibleIndex, (title, time) ->
                        val actualIndex = startIndex + visibleIndex + 1 // 실제 step 번호 (1-based)
                        Box(
                            modifier = Modifier.height(42.dp) // 각 아이템 높이
                        ) {
                            RoutineTimelineItem(
                                time = time,
                                title = title,
                                index = actualIndex,
                                currentStep = currentstep,
                                isTimeout = isTimeout,
                                isDarkMode = isDarkMode
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(89.5.dp))

            // 정지/재생 버튼
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = formatTime(elapsedSeconds),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!isDarkMode && isTimeout) colors.oliveGreen else if (isDarkMode) Color.White else Color.Black,
                )
                Icon(
                    painter = painterResource(id = if (!isUserPaused) R.drawable.ic_pause else R.drawable.baseline_play_arrow_24),
                    contentDescription = if (!isUserPaused) "정지" else "시작",
                    tint = if (isDarkMode) Color.White else colors.black,
                    modifier = Modifier
                        .size(45.dp)
                        .clickable {
                            viewModel.togglePause()
                        }
                )
            }

            Spacer(modifier = Modifier.width(111.dp))

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val isFinalStep = currentstep == routineItems.size
                    Box(
                        modifier = Modifier
                            .size(width = 138.dp, height = 88.dp)
                            .padding(horizontal = 32.dp, vertical = 7.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = if (isFinalStep) R.drawable.enable_check_icon else R.drawable.ic_next_in_circle),
                            contentDescription = if (isFinalStep) "완료됨" else "다음 루틴으로",
                            modifier = Modifier.clickable {
                                // 다음 step으로 가는 기능
                                if (!isFinalStep) {
                                    val nextStepTimeString =
                                        routineItems.getOrNull(currentstep)?.second ?: "0m"
                                    viewModel.nextStep(nextStepTimeString)
                                } else {
                                    viewModel.pauseTimer()
                                    showFinishPopup = true
                                }
                            },
                            tint = if (!isDarkMode && isTimeout) colors.oliveGreen else if (!isDarkMode) colors.limeGreen else Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(width = 139.dp, height = 35.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        when {
                            isFinalStep -> Text(
                                text = "FINISH !",
                                style = typography.body_SB_16.copy(fontWeight = FontWeight.Bold),
                                color = if (isDarkMode) Color.White else if (isTimeout) colors.oliveGreen else colors.black
                            )

                            isTimeout -> Text(
                                text = "NEXT STEP",
                                style = typography.body_SB_16.copy(fontWeight = FontWeight.Bold),
                                color = if (isDarkMode) Color.White else colors.oliveGreen
                            )
                        }
                    }
                }
            }
        }

        // 하단 표시줄
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(53.dp)
                .background(if (isDarkMode) colors.black else colors.veryLightGray)
                .padding(horizontal = 24.dp, vertical = 14.5.dp), // vertical padding 줄임
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.menu_icon),
                contentDescription = "메뉴 아이콘",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showAppIcons = !showAppIcons },
                colorFilter = ColorFilter.tint(if (isDarkMode) colors.mediumGray else colors.black)
            )
            Image(
                painter = painterResource(id = R.drawable.chatting_icon),
                contentDescription = "채팅 아이콘",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showMemoPad = !showMemoPad },
                colorFilter = ColorFilter.tint(if (isDarkMode) colors.mediumGray else colors.black)
            )
        }
    }
    // 설정 버튼 클릭 시 띄울 모드들
    if (showSettingsPopup) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x33000000)) // 반투명 오버레이
                .clickable { showSettingsPopup = false }, // 바깥 클릭 시 닫기
            contentAlignment = Alignment.TopEnd
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 80.dp, end = 17.dp)
                    .width(149.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.75f))
                    .clickable { /* 내부 클릭 무시 */ }
            ) {
                SettingSwitchGroup(
                    settings = listOf(
                        Triple("다크 모드", { viewModel.isDarkMode }, { viewModel.toggleDarkMode() }),
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
                //진행도
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
}

@Preview(showBackground = true, widthDp = 800, heightDp = 360)
@Composable
private fun LandscapeRoutineFocusScreenPreview() {
    val dummyItems = listOf(
        "샤워하기" to "3s",
        "청소하기" to "10s",
        "밥먹기" to "7s",
        "옷갈아입기" to "5s"
    )
    LandscapeRoutineFocusScreen(
        onDismiss = {},
        routineItems = dummyItems,
        currentStep = 1,
        forceShowFinishPopup = false,
        forceShowResultPopup = false
    )
}