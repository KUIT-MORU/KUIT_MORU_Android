package com.konkuk.moru.presentation.routinecreate.component

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun TimePickerDialog(
    onConfirm: (Int, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val hours = (0..23).toList()
    val minutes = (0..59).toList()
    val seconds = (0..59).toList()

    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }
    var selectedSecond by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000)), // 반투명 배경
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.73f)
                .height(264.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(top = 28.dp, bottom = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .height(153.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                NumberPicker(value = selectedHour, range = hours) { selectedHour = it }
                Column {
                    Spacer( modifier = Modifier.height(43.dp)) // 위쪽 여백
                    Text(":", fontSize = 24.sp)
                }
                NumberPicker(value = selectedMinute, range = minutes) { selectedMinute = it }
                Column {
                    Spacer( modifier = Modifier.height(43.dp)) // 위쪽 여백
                    Text(":", fontSize = 24.sp)
                }
                NumberPicker(value = selectedSecond, range = seconds) { selectedSecond = it }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .padding(horizontal = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(color = colors.lightGray, shape = RoundedCornerShape(10.dp))
                        .clickable(
                            indication = null,
                            interactionSource = null
                        ) { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "닫기",
                        style = typography.desc_M_16,
                        color = colors.mediumGray
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(color = colors.limeGreen, shape = RoundedCornerShape(10.dp))
                        .clickable(
                            indication = null,
                            interactionSource = null
                        ) { onConfirm(selectedHour, selectedMinute, selectedSecond) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "확인",
                        style = typography.desc_M_16,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun TimePickerDialogPreview() {
    TimePickerDialog(
        onConfirm = { h, m, s -> /* Do something with the selected time */ },
        onDismiss = { /* Dismiss the dialog */ }
    )
}