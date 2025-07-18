package com.konkuk.moru.presentation.home.component

import android.R.attr.duration
import android.widget.Switch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.konkuk.moru.ui.theme.LocalMoruColorsProvider
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun RoutineStepItem(
    modifier: Modifier = Modifier,
    index: Int,
    title: String,
    duration: Int,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showDuration: Boolean = true,
    showSwitch: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier=Modifier
                    .width(50.dp)
                    .fillMaxHeight()
                    .padding(start = 18.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "${index + 1}",
                    style = typography.desc_M_14,
                    color = colors.black
                )
            }
            Box(
                modifier= Modifier
                    .width(179.dp)
                    .fillMaxHeight()
                    .padding(start=10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = title,
                    style = typography.desc_M_16,
                    color = colors.black,
                )
            }
            Spacer(modifier= Modifier.width(8.dp))
            if(showDuration) {
                Box(
                    modifier= Modifier
                        .width(59.dp)
                        .fillMaxHeight()
                        .padding(start=2.dp,end=4.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "${duration}m",
                        style = typography.desc_M_14,
                        color = colors.black
                    )
                }
                Spacer(modifier = modifier.size(14.dp))
            }
            if(showSwitch) {
                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    modifier = Modifier.scale(0.8f),
                    colors = SwitchDefaults.colors(
                        uncheckedThumbColor = colors.mediumGray,   // OFF 상태 버튼 색 (회색)
                        uncheckedTrackColor = colors.lightGray,   // OFF 상태 배경 색 (연회색)
                        checkedThumbColor = colors.lightGray,           // ON 상태 버튼 색
                        checkedTrackColor = colors.darkGray         // ON 상태 배경 색
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun RoutineStepItemPreview() {

    RoutineStepItem(
        index = 0,
        title = "샤워하기",
        duration = 15,
        isChecked = false,
        onCheckedChange = {},
        showDuration = true,
        showSwitch = true,
    )
}