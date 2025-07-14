// 🎯 아래 코드를 복사해서 HotRoutineListScreen.kt 파일 전체에 붙여넣으세요.

package com.konkuk.moru.presentation.routinefeed.screen.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.core.component.routine.RoutineListItem
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.BasicTopAppBar
import com.konkuk.moru.ui.theme.MORUTheme
// [수정] 통합 Routine 모델을 임포트하고, 기존 HotRoutine 임포트는 삭제합니다.
import com.konkuk.moru.data.model.Routine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotRoutineListScreen(
    title: String,
    onBack: () -> Unit = {}
) {
    // [수정] 더미 데이터를 통합 Routine 모델로 변경합니다.
    val routines = remember {
        List(20) {
            Routine(
                id = it,
                title = "아침 운동", // name -> title
                tags = listOf("#모닝루틴", "#스트레칭"),
                likes = 16,
                isLiked = false,
                isRunning = it % 2 == 0,
                // -- 통합 모델에 필요한 나머지 필드 추가 --
                description = "상쾌한 아침을 위한 운동",
                imageUrl = null,
                category = "건강",
                authorName = "모루",
                authorProfileUrl = null,
                isBookmarked = false
            )
        }
    }

    /* 좋아요 UI 상태 (수정 필요 없음) */
    val likeStates = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            routines.forEach { put(it.id, it.isLiked) }
        }
    }

    val likeCounts = remember {
        mutableStateMapOf<Int, Int>().apply {
            routines.forEach { put(it.id, it.likes) }
        }
    }

    Scaffold(
        topBar = {
            BasicTopAppBar(
                title = title,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "뒤로가기",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF212120),
                    titleContentColor = MORUTheme.colors.limeGreen,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                titleStyle = MORUTheme.typography.time_R_16
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
            items(routines) { r ->
                val liked = likeStates[r.id] ?: false
                RoutineListItem(
                    isRunning = r.isRunning,
                    routineName = r.title, // [수정] name -> title
                    tags = r.tags,
                    likeCount = likeCounts[r.id] ?: 0,
                    isLiked = liked,
                    showCheckbox = false,
                    onLikeClick = {
                        val newState = !liked
                        likeStates[r.id] = newState
                        likeCounts[r.id] = (likeCounts[r.id] ?: 0) +
                                if (newState) 1 else -1
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun HotPreview() {
    MORUTheme {
        HotRoutineListScreen("지금 가장 핫한 루틴은?")
    }
}