package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.presentation.home.FocusType
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

//집중,간편 타입 박스 그리는 함수
@Composable
fun RoutineHeaderBox(
    routineTitle: String,
    hashTag: String,
    focusType: FocusType,
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.transparentbox),
                contentDescription = "투명 박스",
                modifier = Modifier
                    .width(53.dp)
                    .height(52.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
            ){
                Text(
                    text = routineTitle,
                    style = typography.head_EB_24,
                    color = colors.black,
                    maxLines = 2,
                    softWrap = true
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = hashTag,
                    style = typography.body_SB_16,
                    color = colors.darkGray
                )
            }
            // 집중 or 간편
            FocusTypeChip(
                focusType = focusType,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun RoutineHeaderBoxPreview() {
    RoutineHeaderBox(
        routineTitle = "주말 아침 루틴",
        hashTag = "#화이팅 #루틴",
        focusType = FocusType.FOCUS
    )
}