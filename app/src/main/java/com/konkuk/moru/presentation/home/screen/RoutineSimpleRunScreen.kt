package com.konkuk.moru.presentation.home.screen

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.konkuk.moru.R
import com.konkuk.moru.presentation.home.RoutineStepData
import com.konkuk.moru.presentation.home.component.RoutineResultRow
import com.konkuk.moru.presentation.home.component.RoutineSelectItem
import com.konkuk.moru.presentation.home.viewmodel.SharedRoutineViewModel
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

// 소요시간 포맷 함수
fun formatElapsedTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

// 간편 루틴 진입 화면
@Composable
fun RoutineSimpleRunScreen(
    sharedViewModel: SharedRoutineViewModel,
    onDismiss: () -> Unit //x버튼 눌렀을 시
) {
    // intro에서 받아올 값들
    val routineTitle by sharedViewModel.routineTitle.collectAsState()
    val hashTagList by sharedViewModel.routineTags.collectAsState()
    val steps by sharedViewModel.selectedSteps.collectAsState()
    val hashTag = hashTagList.joinToString(" ") { "#$it" }

    /*---------------- 상태 ----------------*/
    // 선택 여부 상태 관리
    var selectedStates by remember { mutableStateOf(steps.map { false }.toMutableStateList()) }

    // Finish 버튼의 상태 저장
    val isAnySelected = selectedStates.any { it }

    // 팝업 표시 여부
    var showFinishPopup by remember { mutableStateOf(false) }

    // 루틴 시작 시간 저장
    val routineStartTime = remember { System.currentTimeMillis() }

    // 종료 버튼 상태 저장
    var showResultPopup by remember { mutableStateOf(false) }

    //최종 소요시간 저장용
    var finalElapsedTime by remember { mutableStateOf("") }

    //컴포넌트들 배치
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp)
        ) {
            // X 버튼
            Text(
                text = "✕",
                color = colors.black,
                style = typography.body_SB_16,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable { onDismiss() }
            )

            Spacer(modifier = Modifier.height(77.dp))

            // 루틴 제목 및 해시태그
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Image(
                        painter = painterResource(R.drawable.transparentbox),
                        contentDescription = "투명 박스",
                        modifier = Modifier
                            .width(53.dp)
                            .height(52.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = routineTitle,
                            style = typography.head_EB_24,
                            color = colors.black
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = hashTag,
                            style = typography.body_SB_16,
                            color = colors.darkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(129.dp))

            // 루틴 항목들
            steps.forEachIndexed { index, step ->
                RoutineSelectItem(
                    text = step.name,
                    isSelected = selectedStates[index],
                    onClick = {
                        selectedStates = selectedStates.toMutableStateList().apply {
                            this[index] = !this[index]
                        }
                    },
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }
        }

        // Finish 버튼 (절대 좌표 배치)
        Box(
            modifier = Modifier
                .offset(x = 221.dp, y = 387.dp)
                .width(139.dp)
                .height(123.dp)
                .clickable(
                    enabled = isAnySelected,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    showFinishPopup = true
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 체크 박스
                Box(
                    modifier = Modifier
                        .width(133.dp)
                        .height(88.dp)
                        .padding(horizontal = 32.dp, vertical = 7.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isAnySelected)
                                R.drawable.enable_check_icon
                            else
                                R.drawable.disable_check_icon
                        ),
                        contentDescription = "체크 아이콘",
                        modifier = Modifier.size(74.dp),
                        tint = if (isAnySelected) colors.limeGreen else Color.Unspecified
                    )
                }
                //Finish 텍스트
                Box(
                    modifier = Modifier
                        .width(133.dp)
                        .height(35.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "FINISH!",
                        style = typography.body_SB_16.copy(fontWeight = FontWeight.Bold),
                        color = colors.black
                    )
                }
            }
        }

        // Finish 팝업(팝업 1)
        if (showFinishPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000)),
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
                    Spacer(modifier = Modifier.height(21.25.dp))
                    Text(
                        text = "루틴을 종료하시겠습니까?",
                        style = typography.title_B_20.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.black
                    )
                    Spacer(modifier = Modifier.height(5.79.dp))
                    Text(
                        text = "종료한 루틴은 내활동에 저장됩니다.",
                        style = typography.title_B_12,
                        color = colors.darkGray
                    )
                    Spacer(modifier = Modifier.height(19.79.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 돌아가기 버튼
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

                        // 종료 버튼
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
                                    finalElapsedTime = formatElapsedTime(System.currentTimeMillis() - routineStartTime)
                                }
                                .width(123.dp)
                                .height(40.55.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "종료",
                                style = typography.body_SB_16.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 2번째 팝업
        if (showResultPopup) {
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
                        .background(Color.White.copy(alpha = 0.9f))
                        .width(264.dp)
                        .height(276.dp)
                        .padding(horizontal = 6.02.dp, vertical = 5.98.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(14.09.dp))
                    Text(
                        text = "루틴 종료!",
                        style = typography.title_B_20.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.black
                    )
                    Spacer(modifier = Modifier.height(20.07.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(
                                top = 8.03.dp,
                                start = 15.91.dp,
                                end = 12.93.dp,
                                bottom = 8.03.dp
                            )
                    ) {
                        Column() {
                            RoutineResultRow(R.drawable.schedule_icon, "루틴", routineTitle)
                            Spacer(modifier = Modifier.height(16.06.dp))
                            RoutineResultRow(R.drawable.check_icon_gray, "결과", "완료")
                            Spacer(modifier = Modifier.height(16.06.dp))
                            RoutineResultRow(
                                R.drawable.step_icon,
                                "스텝",
                                "${selectedStates.count { it }}/${steps.size}"
                            )
                            Spacer(modifier = Modifier.height(16.06.dp))
                            RoutineResultRow(R.drawable.clock_icon, "시간", finalElapsedTime)
                        }
                    }
                    Spacer(modifier = Modifier.height(29.11.dp))
                    //확인 버튼
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
                                onDismiss()
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
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
)
@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
)
@Composable
private fun RoutineSimpleRunScreenPreview() {
    val dummyViewModel = remember { SharedRoutineViewModel() }

    // 더미 데이터 설정
    val sampleSteps = listOf(
        RoutineStepData("샤워하기", 15, true),
        RoutineStepData("청소하기", 10, true),
        RoutineStepData("밥먹기", 30, true),
        RoutineStepData("옷갈아입기", 8, true)
    )
    dummyViewModel.setRoutineTitle("주말 아침 루틴")
    dummyViewModel.setRoutineTags(listOf("태그", "태그"))
    dummyViewModel.setSelectedSteps(sampleSteps)

    RoutineSimpleRunScreen(
        sharedViewModel = dummyViewModel,
        onDismiss = {}
    )
}
