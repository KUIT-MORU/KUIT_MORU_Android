package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.konkuk.moru.ui.theme.LocalMoruColorsProvider
import com.konkuk.moru.ui.theme.LocalMoruTypographyProvider
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun RoutineStepItem(modifier: Modifier = Modifier) {

    var isChecked by remember { mutableStateOf(false)} //로글 스위치 on/off 여부

    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "1",
                style = LocalMoruTypographyProvider.current.desc_M_14,
                color = LocalMoruColorsProvider.current.black
            )
            Spacer(modifier = modifier.size(29.dp))
            Text(
                text = "샤워하기",
                style = LocalMoruTypographyProvider.current.desc_M_16,
                color = LocalMoruColorsProvider.current.black
            )
            Spacer(modifier = modifier.size(148.dp))
            Text(
                text = "15m",
                style = LocalMoruTypographyProvider.current.desc_M_14,
                color = LocalMoruColorsProvider.current.black
            )
            Spacer(modifier = modifier.size(20.dp))
            Switch(
                checked = isChecked,
                onCheckedChange = { isChecked = it},
                colors = SwitchDefaults.colors(
                    uncheckedThumbColor = Color(0xFF9E9E9E),   // OFF 상태 버튼 색 (회색)
                    uncheckedTrackColor = Color(0xFFE0E0E0),   // OFF 상태 배경 색 (연회색)
                    checkedThumbColor = Color.White,           // ON 상태 버튼 색
                    checkedTrackColor = Color.DarkGray         // ON 상태 배경 색
                )
            )
        }
    }
}

@Preview
@Composable
private fun RoutineStepItemPreview() {
    RoutineStepItem()
}