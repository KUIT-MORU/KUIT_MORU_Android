package com.konkuk.moru.presentation.routinefeed.component.TopAppBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R


/**
 * 홈 화면에서 사용하는 TopAppBar
 * @param hasNotification 새 알림이 있는지 여부
 * @param onLogoClick 로고 클릭 시 동작
 * @param onSearchClick 검색 바 클릭 시 동작
 * @param onNotificationClick 알림 아이콘 클릭 시 동작
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    hasNotification: Boolean,
    onLogoClick: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            MoruSearchBar(
                query = searchQuery,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            )
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onLogoClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_moru),
                    contentDescription = "로고",
                    tint = Color.Unspecified, // 원본 아이콘 색상 사용
                    modifier = Modifier.size(42.dp)
                )
            }
        },

        actions = {
            IconButton(onClick = { onSearch(searchQuery) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "검색",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(onClick = onNotificationClick) {
                Icon(
                    painter = painterResource(
                        id = if (hasNotification) R.drawable.ic_bell_on else R.drawable.ic_bell_off
                    ),
                    tint = Color.Unspecified,
                    contentDescription = "알림",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF212120),
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}


@Preview(name = "HomeTopAppBar Interactive", showBackground = true)
@Composable
fun HomeTopAppBarPreview() {
    // 1. 프리뷰 내에서 사용할 상태 변수를 remember로 생성합니다.
    var searchQuery by remember { mutableStateOf("") }
    var hasNotification by remember { mutableStateOf(true) }

    MaterialTheme {
        HomeTopAppBar(
            searchQuery = searchQuery,
            // 2. onQueryChange가 호출될 때마다 상태 변수(searchQuery)를 업데이트합니다.
            // 이 부분이 글자 입력을 가능하게 하는 핵심입니다.
            onQueryChange = { newQuery ->
                searchQuery = newQuery
            },
            onSearch = { query ->
                // 실제 검색 로직 대신, 프리뷰에서는 동작 확인을 위해 로그를 출력합니다.
                println("Search triggered for: '$query'")
            },
            hasNotification = hasNotification,
            onNotificationClick = {
                // 알림 아이콘 클릭 시 상태를 반전시켜 아이콘 모양이 바뀌는지 확인합니다.
                hasNotification = !hasNotification
            },
            onLogoClick = {}
        )
    }
}

@Preview(name = "HomeTopAppBar Interactive", showBackground = true)
@Composable
fun HomeTopAppBarPreviewWithNoNotification() {
    // 1. 프리뷰 내에서 사용할 상태 변수를 remember로 생성합니다.
    var searchQuery by remember { mutableStateOf("") }
    var hasNotification by remember { mutableStateOf(true) }

    MaterialTheme {
        HomeTopAppBar(
            searchQuery = searchQuery,
            // 2. onQueryChange가 호출될 때마다 상태 변수(searchQuery)를 업데이트합니다.
            // 이 부분이 글자 입력을 가능하게 하는 핵심입니다.
            onQueryChange = { newQuery ->
                searchQuery = newQuery
            },
            onSearch = { query ->
                // 실제 검색 로직 대신, 프리뷰에서는 동작 확인을 위해 로그를 출력합니다.
                println("Search triggered for: '$query'")
            },
            hasNotification = false,
            onNotificationClick = {
                // 알림 아이콘 클릭 시 상태를 반전시켜 아이콘 모양이 바뀌는지 확인합니다.
                hasNotification = !hasNotification
            },
            onLogoClick = {}
        )
    }
}