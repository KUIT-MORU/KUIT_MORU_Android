package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinePaceInfo(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    onDetailClick: () -> Unit,
    renewalDate: String = "2025.01.01",
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        dragHandle = null,
        scrimColor = colors.black.copy(alpha = 0.5f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(30.dp)
                    .height(5.dp)
                    .background(
                        color = colors.lightGray,
                        RoundedCornerShape(100.dp)
                    )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ){
                Text(
                    text = "루틴 페이스",
                    style = typography.body_SB_16
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "루틴 페이스는 일주일간의 실천 데이터를 바탕으로,\n당신의 루틴 실천 리듬을 분석 합니다.\n"+
                        "매주 월요일, 지금 나의 루틴 페이스는 어떤지 알 수 있어요.",
                style = typography.time_R_12.copy(
                    lineHeight = 20.sp
                ),
                color = colors.mediumGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(208.dp)
                    .background(color = colors.veryLightGray, RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("최근 갱신 일자: ", style = typography.time_R_12, color = colors.mediumGray)
                Text(text = renewalDate, style = typography.time_R_12, color = colors.mediumGray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onDetailClick,
                colors = ButtonDefaults.buttonColors(containerColor = colors.limeGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(bottom = 10.dp)
            ) {
                Text(
                    text = "자세히 보기",
                    color = colors.black,
                    style = typography.body_SB_16,
                )
            }
        }
    }
}