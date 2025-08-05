package com.konkuk.moru.presentation.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.presentation.home.FocusType
import com.konkuk.moru.presentation.home.RoutineStepData
import com.konkuk.moru.presentation.home.component.RoutineHeaderBox
import com.konkuk.moru.presentation.home.component.RoutineStepItem
import com.konkuk.moru.presentation.home.viewmodel.SharedRoutineViewModel
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

val sampleSteps = listOf(
    RoutineStepData("샤워하기", 15, true),
    RoutineStepData("청소하기", 10, true),
    RoutineStepData("밥먹기", 30, true),
    RoutineStepData("옷갈아입기", 8, true)
)

//메인 화면
@Composable
fun RoutineFocusIntroScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: SharedRoutineViewModel,
    onStartClick:(selectedSteps: List<RoutineStepData>)->Unit,
    onBackClick: () -> Unit,
) {
    // 받아올 정보들
    val routineTitle by sharedViewModel.routineTitle.collectAsState()
    val hashTagList by sharedViewModel.routineTags.collectAsState()
    val focusType by sharedViewModel.focusType.collectAsState()
    val steps by sharedViewModel.selectedSteps.collectAsState()
    val hashTag = hashTagList.joinToString(" ") { "#$it" }

    // 각 루틴의 상태를 기억할 수 있또록 상태로 복사해서 관리
    var stepStates by remember { mutableStateOf(steps.map { it.copy() }) }

    // 스위치가 on인 상태의 루틴의 소요시간만 합해서 총 소요시간 계산에 반영
    val totalDuration = stepStates.filter { it.isChecked }.sumOf { it.duration }

    // 하나라도 on이 되어 있다면 시작하기 버튼 활성화(총 소요시간으로 판단)
    val isStartEnabled = totalDuration > 0
    Scaffold(
        //시작하기 버튼
        bottomBar = {
            Button(
                onClick = {
                    onStartClick(stepStates.filter { it.isChecked })
                },
                enabled = isStartEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isStartEnabled) colors.limeGreen else colors.veryLightGray,
                )
            ) {
                Text(
                    text = "시작하기",
                    style = typography.body_SB_16,
                    color = colors.black
                )
            }
        }
    )
    { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
//            item {
//                // 상단 상태 바
//                StatusBarMock(isDarkMode = false)
//            }
            item {
                //상단 바
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp) // TopAppBar 기본 높이
                        .background(Color.White)
                ) {
                    IconButton(
                        onClick = onBackClick,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.left_arrow),
                            contentDescription = "뒤로가기",
                            modifier = Modifier.size(width = 8.dp, height = 16.dp)
                        )
                    }
                }
            }

            item {
                // 투명상자/루틴 제목/태그/타입
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = "시작해볼까요?",
                        style = typography.head_EB_24.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.darkGray
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    RoutineHeaderBox(
                        routineTitle = routineTitle,
                        hashTag = hashTag,
                        focusType = focusType
                    )
                }
                Spacer(modifier = Modifier.height(172.dp))
            }
            //STEP과 루틴들
            item {
                //STEP(해야할 루틴들)
                Text(
                    text = "STEP",
                    style = typography.body_SB_16.copy(fontWeight = FontWeight.Bold),
                    color = colors.black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(5.dp))
            }
            itemsIndexed(stepStates) { index, step ->
                RoutineStepItem(
                    index = index,
                    title = step.name,
                    duration = step.duration,
                    isChecked = step.isChecked,
                    showSwitch = focusType == FocusType.FOCUS,
                    showDuration = focusType == FocusType.FOCUS,
                    onCheckedChange = {
                        stepStates = stepStates.toMutableStateList().apply {
                            this[index] = this[index].copy(isChecked = it)
                        }
                    }
                )
                //divider
                Divider(
                    color = colors.lightGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.5.dp)
                )
            }
            // TOTAL 소요시간 섹션 (FocusType.FOCUS일 경우만)
            if (focusType == FocusType.FOCUS) {
                item {
                    Spacer(modifier = Modifier.height(92.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        //루틴 총 소요시간
                        Text(
                            text = "TOTAL",
                            style = typography.body_SB_16,
                            color = colors.black
                        )
                        Text(
                            text = "${totalDuration}m 00s",
                            style = typography.head_EB_24,
                            color = colors.black
                        )
                        Spacer(modifier = Modifier.height(21.dp))
                    }
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(175.dp))
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
@Composable
private fun RoutineFocusIntroScreenPreview() {
    val dummyViewModel = remember { SharedRoutineViewModel() }

    // 가짜 데이터 설정
    val sampleSteps = listOf(
        RoutineStepData("샤워하기", 15, true),
        RoutineStepData("청소하기", 10, true),
        RoutineStepData("밥먹기", 30, true),
        RoutineStepData("옷갈아입기", 8, true)
    )
    dummyViewModel.setSelectedSteps(sampleSteps)
    dummyViewModel.setFocusType(FocusType.FOCUS)

    // 추가적으로 제목과 태그도 미리 설정
    dummyViewModel.apply {
        setRoutineTitle("주말 아침 루틴")
        setRoutineTags(listOf("태그1", "태그2"))
    }

    RoutineFocusIntroScreen(
        sharedViewModel = dummyViewModel,
        onStartClick = {},  // 시작 버튼 클릭 동작
        onBackClick = {},   // 뒤로 가기 동작
    )
}
