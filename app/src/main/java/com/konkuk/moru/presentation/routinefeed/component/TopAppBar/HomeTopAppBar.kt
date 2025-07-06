package com.konkuk.moru.presentation.routinefeed.component.TopAppBar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
    hasNotification: Boolean,
    onLogoClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            MoruSearchBar(onClick = onSearchClick)
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onLogoClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_moru),
                    contentDescription = "로고",
                    tint = Color.Unspecified // 원본 아이콘 색상 사용
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "검색"
                )
            }
            IconButton(onClick = onNotificationClick) {
                Icon(
                    painter = painterResource(
                        id = if (hasNotification) R.drawable.ic_bell_on else R.drawable.ic_bell_off
                    ),
                    contentDescription = "알림"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.DarkGray,
            actionIconContentColor = Color.White
        )
    )
}

@Preview(name = "알림 있을 때", showBackground = true)
@Composable
private fun HomeTopAppBarWithNotificationPreview() {
    MaterialTheme {
        HomeTopAppBar(
            hasNotification = true,
            onLogoClick = {},
            onSearchClick = {},
            onNotificationClick = {}
        )
    }
}

@Preview(name = "알림 없을 때", showBackground = true)
@Composable
private fun HomeTopAppBarWithoutNotificationPreview() {
    MaterialTheme {
        HomeTopAppBar(
            hasNotification = false,
            onLogoClick = {},
            onSearchClick = {},
            onNotificationClick = {}
        )
    }
}