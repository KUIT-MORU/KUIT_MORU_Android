package com.konkuk.moru.presentation.home.component

import android.R.attr.contentDescription
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter.State.Empty.painter
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.R

@Composable
fun HomeFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(63.dp),
        contentAlignment = Alignment.Center
    ) {
        FloatingActionButton(
            onClick = onClick,
            backgroundColor = colors.limeGreen,
            contentColor = Color.White,
            modifier = Modifier.size(63.dp) // ✅ 고정 Modifier 사용
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_add_24),
                contentDescription = "루틴 생성하기 버튼",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}


@Preview
@Composable
fun HomeFabPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        HomeFloatingActionButton(onClick = {})
    }
}