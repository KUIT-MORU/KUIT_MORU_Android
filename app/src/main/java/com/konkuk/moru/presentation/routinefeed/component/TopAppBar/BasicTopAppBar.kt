package com.konkuk.moru.presentation.routinefeed.component.TopAppBar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview

/**
 * 가장 기본적인 형태의 TopAppBar
 * @param title 중앙에 표시될 텍스트
 * @param navigationIcon 왼쪽에 표시될 아이콘 컴포저블 (뒤로가기 등)
 * @param actions 오른쪽에 표시될 아이콘 리스트 컴포저블
 * @param modifier Modifier
 * @param colors 앱 바의 배경색, 글자색 등을 지정
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTopAppBar(
    title: String,
    navigationIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable (RowScope.() -> Unit) = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.DarkGray,
        titleContentColor = Color.White,
        navigationIconContentColor = Color.White,
        actionIconContentColor = Color.White
    ),
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge
) {
    TopAppBar(
        title = { Text(text = title, style = titleStyle) },
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = colors
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun BasicTopAppBarPreview() {
    MaterialTheme {
        BasicTopAppBar(
            title = "루틴명을 검색해보세요",
            navigationIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기"
                    )
                }
            }
        )
    }
}