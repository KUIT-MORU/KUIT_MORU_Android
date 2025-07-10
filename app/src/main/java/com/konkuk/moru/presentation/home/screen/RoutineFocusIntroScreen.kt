package com.konkuk.moru.presentation.home.screen

import android.R.attr.navigationIcon
import android.util.Log.w
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.presentation.home.component.RoutineStepItem
import com.konkuk.moru.ui.theme.MORUTheme
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

data class RoutineStepData(
    val name: String, //루틴명
    val duration: Int, //소요시간
    var isChecked: Boolean = false //실행유무
)

val sampleSteps = listOf(
    RoutineStepData("샤워하기", 15, true),
    RoutineStepData("청소하기", 10, true),
    RoutineStepData("밥먹기", 30, true),
    RoutineStepData("옷갈아입기", 8, true)
)

// FocusType: "집중" or "간편"
enum class FocusType{
    FOCUS,SIMPLE
}

//집중,간편 타입 박스 그리는 함수
@Composable
fun FocusTypeChip(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = colors.paleLime,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 9.5.dp, vertical = 3.5.dp)
    ) {
        Text(
            text = "집중",
            style = typography.body_SB_16,
            color = colors.oliveGreen
        )
    }
}

//메인 화면
@Composable
fun RoutineFocusIntroScreen(
    modifier: Modifier = Modifier,
    routineTitle: String = "주말 아침 루틴",
    hashTag: String = "#태그 #태그",
    focusType: FocusType = FocusType.FOCUS,
    steps: List<RoutineStepData> = sampleSteps,
    onStartClick: () -> Unit,
    onBackClick: () -> Unit
) {

    // 각 루틴의 상태를 기억할 수 있또록 상태로 복사해서 관리
    var stepStates by remember { mutableStateOf(steps.map { it.copy() }) }

    // 스위치가 on인 상태의 루틴의 소요시간만 합해서 총 소요시간 계산에 반영
    val totalDuration = stepStates.filter { it.isChecked }.sumOf { it.duration }

    // 하나라도 on이 되어 있다면 시작하기 버튼 활성화(총 소요시간으로 판단)
    val isStartEnabled = totalDuration > 0

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        //상단 바
        TopAppBar(
            title = {},
            backgroundColor = Color.White,
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.left_arrow),
                        contentDescription = "뒤로가기",
                        modifier = Modifier
                            .width(8.dp)
                            .height(16.dp)
                    )
                }
            }
        )

        // 투명상자/루틴 제목/태그/타입
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.size(1.dp))
            Text(
                text = "시작해볼까요?",
                style = typography.head_EB_24,
                color = colors.darkGray
            )
            Spacer(modifier = Modifier.size(20.dp))
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
                    Spacer(modifier = Modifier.size(12.dp))
                    Column() {
                        Text(
                            text = routineTitle,
                            style = typography.head_EB_24,
                            color = colors.black
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = hashTag,
                            style = typography.body_SB_16,
                            color = colors.darkGray
                        )
                    }
                }
                //집중,간편 타입 박스
                FocusTypeChip(
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            Spacer(modifier = Modifier.size(172.dp))
        }

        //STEP과 루틴들
        Column() {
            //STEP(해야할 루틴들)
            Text(
                text = "STEP",
                style = typography.body_SB_16,
                color = colors.black,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Column() {
                Spacer(modifier = Modifier.size(5.dp))
                stepStates.forEachIndexed { index, step ->
                    RoutineStepItem(
                        index = index,
                        title = step.name,
                        duration = step.duration,
                        isChecked = step.isChecked,
                        showSwitch = focusType == FocusType.FOCUS,
                        showDuration = focusType == FocusType.FOCUS,
                        onCheckedChange = {
                            stepStates = stepStates.toMutableStateList().apply{
                                this[index] = this[index].copy(isChecked = it)
                            }
                        },
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .padding(vertical = 4.dp)
                    )
                    //divider
                    Divider(
                        color = colors.lightGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.5.dp)
                    )
                }
            }
        }
        if(focusType == FocusType.FOCUS) {
            Spacer(modifier = Modifier.size(92.dp))


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
                Spacer(modifier = Modifier.size(21.dp))
            }
        }
        else{
            Spacer(modifier = Modifier.size(175.dp))
        }

        //시작하기 버튼
        Button(
            onClick = onStartClick,
            enabled = isStartEnabled,
            modifier = Modifier
                .fillMaxSize(),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isStartEnabled) colors.limeGreen else colors.veryLightGray,
            )
        ){
            Text(
                text = "시작하기",
                style = typography.body_SB_16,
                color = colors.black
            )
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

    val sampleSteps = listOf(
        RoutineStepData("샤워하기", 15, true),
        RoutineStepData("청소하기", 10, true),
        RoutineStepData("밥먹기", 30, true),
        RoutineStepData("옷갈아입기", 8, true)
    )

    RoutineFocusIntroScreen(
        steps = sampleSteps,
        onStartClick = {}, //시작하기 누르면 다음 화면으로
        onBackClick = {}, //뒤로 가기 구현해야함
        routineTitle = "주말 아침 루틴",
        hashTag = "태그1 태그2",
        //타입에 따라 보이는 컴포넌트 다름
        focusType = FocusType.SIMPLE
    )
}