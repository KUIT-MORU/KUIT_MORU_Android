package com.konkuk.moru.presentation.myactivity.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.presentation.myactivity.component.ActMyInfo
import com.konkuk.moru.presentation.myactivity.component.MyProfileTitle
import com.konkuk.moru.ui.theme.MORUTheme.colors

@Composable
fun ActMainScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(start = 16.dp, end = 16.dp)
    ) {
        MyProfileTitle()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.lightGray)
        ){}
        Spacer(modifier = Modifier.height(16.dp))
        ActMyInfo(4, 628, 221, "정해찬", "루틴을 꼭 지키고 말겠어! 저는 정해찬입니다. 아주 긴 문장을 쓰기 위해 아무 말이나 작성 중입니다.")
    }
}

@Preview
@Composable
private fun ActMainScreenPreview() {
    ActMainScreen(modifier = Modifier)
}