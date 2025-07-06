package com.konkuk.moru.presentation.routinefeed.component.TopAppBar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 다른 화면에서도 재사용 가능한 검색 바 컴포넌트
 * @param onClick 검색 바를 클릭했을 때의 동작
 * @param modifier Modifier
 */
@Composable
fun MoruSearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(32.dp)
            .width(201.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(Color(0xFF595959))
            .clickable { onClick() }
            .padding(horizontal = 11.dp, vertical = 5.dp),
        //contentAlignment = Alignment.CenterStart
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "루틴을 검색해 보세요!",
                fontSize = 12.sp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
@Preview
fun MoruSearchBarScreen() {
    MoruSearchBar(modifier = Modifier, onClick = {})
}