package com.konkuk.moru.presentation.routinecreate.component

import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.ui.theme.MORUTheme.colors

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
                .width(300.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberPicker(value = selectedHour, range = hours) { selectedHour = it }
                Text(":", fontSize = 24.sp)
                NumberPicker(value = selectedMinute, range = minutes) { selectedMinute = it }
                Text(":", fontSize = 24.sp)
                NumberPicker(value = selectedSecond, range = seconds) { selectedSecond = it }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(
                    "닫기",
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onDismiss() },
                    color = colors.lightGray,
                    fontSize = 18.sp
                )
                Text(
                    "확인",
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onConfirm(selectedHour, selectedMinute, selectedSecond)
                        },
                    color = colors.limeGreen,
                    fontSize = 18.sp
                )
            }
        }
    }
}