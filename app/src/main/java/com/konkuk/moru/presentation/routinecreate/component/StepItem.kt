package com.konkuk.moru.presentation.routinecreate.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.data.model.Step
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun StepItem(
    step: Step,
    onTitleChange: (String) -> Unit,
    onShowTimePicker: () -> Unit,
    stepCount: Int,
    onDelete: (stepId: String) -> Unit
) {
    var titleInput by remember(step.id) { mutableStateOf(step.title) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(57.dp)
    ) {
        HorizontalDivider(thickness = 1.dp, color = colors.mediumGray)

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_steplist_burger),
                contentDescription = "Step List Icon",
                modifier = Modifier.size(24.dp),
                tint = colors.lightGray
            )
            Spacer(modifier = Modifier.width(21.dp))

            // 활동명 (기본 텍스트 or 입력 필드 전환)
            BasicTextField(
                value = titleInput,
                onValueChange = {
                    titleInput = it
                    onTitleChange(it)
                },
                singleLine = true,
                textStyle = typography.body_SB_14.copy(color = colors.charcoalBlack),
                modifier = Modifier
                    .weight(0.34f)
                    .padding(end = 4.dp),
                decorationBox = { innerTextField ->
                    if (titleInput.isEmpty()) {
                        Text(
                            text = "활동명",
                            style = typography.body_SB_14,
                            color = colors.charcoalBlack,
                        )
                    }
                    innerTextField()
                }
            )

            // 소요 시간 (기본 텍스트, 클릭 시 다이얼 팝업)
            Text(
                text = if (step.time == "") "소요 시간" else step.time,
                style = typography.body_SB_14,
                color = colors.charcoalBlack,
                modifier = Modifier
                    .weight(0.32f)
                    .clickable { onShowTimePicker() }
            )
            if (stepCount > 1) {
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_x),
                    contentDescription = "Step 삭제",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onDelete(step.id) },
                    tint = colors.mediumGray
                )
            }else{
                Spacer(modifier = Modifier.width(26.dp))
            }
        }

        HorizontalDivider(thickness = 1.dp, color = colors.mediumGray)
    }
}

@Preview
@Composable
private fun StepItemPreview() {
    val stepList = remember {
        mutableStateListOf(
            Step(title = "활동명", time = "")
        )
    } // 초기 step 1개
    StepItem(
        step = stepList[0],
        onTitleChange = { stepList[0] = stepList[0].copy(title = it) },
        onShowTimePicker = { },
        stepCount = 2,
        onDelete = { stepList.removeAt(0) }
    )
}