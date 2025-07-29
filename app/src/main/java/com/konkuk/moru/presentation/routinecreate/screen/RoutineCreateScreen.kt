package com.konkuk.moru.presentation.routinecreate.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.R
import com.konkuk.moru.core.component.Switch.CustomToggleSwitch
import com.konkuk.moru.core.component.Switch.RoutineSimpleFocusSwitch
import com.konkuk.moru.core.component.routinedetail.RoutineDescriptionField
import com.konkuk.moru.core.component.routinedetail.ShowUserCheckbox
import com.konkuk.moru.data.model.Step
import com.konkuk.moru.presentation.routinecreate.component.StepItem
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun RoutineCreateScreen() {
    val focusManager = LocalFocusManager.current
    var isFocusingRoutine by remember { mutableStateOf(false) }
    var showUser by remember { mutableStateOf(false) }
    var routineDescription by remember { mutableStateOf("") }
    val stepList = remember { mutableStateListOf(Step("", "00:00:00")) } // 초기 step 1개

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.veryLightGray)
            .systemBarsPadding()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(color = colors.veryLightGray),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_x),
                    contentDescription = "Close",
                    modifier = Modifier
                        .padding(start = 16.dp, end = 22.dp)
                        .size(18.dp)
                )
                Text(
                    text = "루틴 생성",
                    style = typography.desc_M_16
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = Color.White)
                    .padding(horizontal = 16.dp)
                    .padding(top = 17.dp),
            ) {
                // 이미지 박스 + 제목/설명
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .width(105.dp)
                            .fillMaxHeight()
                            .background(colors.veryLightGray, RoundedCornerShape(4.dp))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_routine_image_default),
                            contentDescription = "이미지",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(24.dp),
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("루틴 제목", style = typography.title_B_24, color = colors.darkGray)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ShowUserCheckbox(
                                showUser = showUser,
                                onClick = { showUser = !showUser }
                            )
                            RoutineSimpleFocusSwitch(
                                checked = isFocusingRoutine,
                                onClick = { isFocusingRoutine = it }
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        // 루틴 설명 입력 필드
                        RoutineDescriptionField(
                            value = routineDescription,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            onValueChange = { routineDescription = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 태그 추가 버튼
                Row(
                    modifier = Modifier
                        .height(30.dp)
                        .background(color = colors.charcoalBlack, shape = RoundedCornerShape(15.dp))
                        .clickable { }
                        .padding(horizontal = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("#태그 추가", color = colors.limeGreen, style = typography.time_R_14)
                    Icon(
                        painter = painterResource(id = R.drawable.ic_createroutine_addtag_arrow),
                        contentDescription = "태그 추가 아이콘",
                        modifier = Modifier
                            .padding(start = 9.dp),
                        tint = colors.limeGreen
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // STEP 영역
                Text("STEP", style = typography.title_B_20)
                Spacer(modifier = Modifier.height(8.dp))
                stepList.forEachIndexed { index, step ->
                    StepItem(
                        step = step,
                        onTitleChange = { newTitle -> stepList[index] = step.copy(title = newTitle) },
                        onTimeChange = { newTime -> stepList[index] = step.copy(time = newTime) }
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Box( //circle 버튼
                    modifier = Modifier
                        .size(29.dp)
                        .background(color = colors.lightGray, shape = CircleShape)
                        .clickable { stepList.add(Step("", "00:00:00")) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_routinecreate_addstep),
                        contentDescription = "스텝 추가",
                        tint = Color.Unspecified
                    )
                }

                // 사용앱
                if (isFocusingRoutine) {
                    Text("사용앱", style = typography.title_B_20)
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .border(1.dp, colors.lightGray, RoundedCornerShape(6.dp))
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_add_24),
                            contentDescription = "앱 추가",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            // complete button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(color = colors.lightGray)
                    .clickable(
                        indication = null,
                        interactionSource = null
                    ) {},
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "완료하기",
                    style = typography.body_SB_16,
                    color = colors.darkGray
                )
            }
        }
    }
}

@Preview
@Composable
private fun RoutineCreateScreenPreview() {
    RoutineCreateScreen()
}