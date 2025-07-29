package com.konkuk.moru.presentation.routinecreate.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun NumberPicker(
    value: Int,
    range: List<Int>,
    onValueChange: (Int) -> Unit
) {
    // 추후 AndroidView or LazyColumn을 이용한 Picker로 교체 가능
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = if (value > 0) "%02d".format(value - 1) else "", color = Color.Gray)
        Text(text = "%02d".format(value), color = Color.Black, fontSize = 20.sp)
        Text(text = if (value < range.last()) "%02d".format(value + 1) else "", color = Color.Gray)
    }
}