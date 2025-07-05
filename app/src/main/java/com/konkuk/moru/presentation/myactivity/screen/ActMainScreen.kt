package com.konkuk.moru.presentation.myactivity.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
    }
}

@Preview
@Composable
private fun ActMainScreenPreview() {
    ActMainScreen(modifier = Modifier)
}