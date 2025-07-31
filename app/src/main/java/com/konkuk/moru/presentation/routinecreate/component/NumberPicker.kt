// ✅ 파일명: NumberPicker.kt

package com.konkuk.moru.presentation.routinecreate.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.ui.theme.MORUTheme
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun NumberPicker(
    value: Int,
    range: List<Int>,
    onValueChange: (Int) -> Unit
) {
    val itemHeight = 38.5.dp
    val visibleItems = listOf(
        range.getOrNull(value - 1), // 위 숫자
        range.getOrNull(value),     // 선택된 숫자 (2번째 줄)
        range.getOrNull(value + 1), // 아래 숫자 1
        range.getOrNull(value + 2)  // 아래 숫자 2
    )

    Column(
        modifier = Modifier
            .height(154.dp)
            .width(60.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        visibleItems.forEachIndexed { index, item ->
            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth()
                    .clickable(enabled = item != null && item != value) {
                        item?.let { onValueChange(it) }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (item != null) {
                    Text(
                        text = "%02d".format(item),
                        fontSize = 24.sp,
                        color = if (index == 1) Color.Black else MORUTheme.colors.mediumGray,
                        style = typography.time_R_24
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NumberPickerPreview() {
    var selected by remember { mutableStateOf(0) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        NumberPicker(
            value = selected,
            range = (0..59).toList(),
            onValueChange = { selected = it }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "선택된 값: %02d".format(selected),
            fontSize = 20.sp,
            color = MORUTheme.colors.darkGray,
            style = typography.body_SB_14
        )
    }
}