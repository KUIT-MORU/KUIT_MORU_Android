package com.konkuk.moru.presentation.routinefeed.screen.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.core.component.routine.RoutineListItem
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.BasicTopAppBar
import com.konkuk.moru.ui.theme.MORUTheme

/* ---------- 모델 ---------- */
data class HotRoutine(
    val id: Int,
    val name: String,
    val tags: List<String>,
    val likes: Int,
    val isLiked: Boolean,
    val isRunning: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotRoutineListScreen(
    title: String,             // ← 홈에서 선택한 섹션 제목
    onBack: () -> Unit = {}
) {
    /* 더미 데이터 (TODO: 서버에서 받아오기) */
    val routines = remember {
        List(20) {
            HotRoutine(
                id        = it,
                name      = "아침 운동",
                tags      = listOf("#모닝루틴", "#스트레칭"),
                likes     = 16,
                isLiked   = false,
                isRunning = it % 2 == 0
            )
        }
    }

    /* 좋아요 UI 상태 */
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
                title = title,                        // ✔ 동적 타이틀
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
                    containerColor = Color(0xFF212120),             // 배경: 블랙
                    titleContentColor = MORUTheme.colors.limeGreen, // 타이틀: 라임
                    navigationIconContentColor = Color.White,       // 아이콘: 흰색
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
                    isRunning    = r.isRunning,
                    routineName  = r.name,
                    tags         = r.tags,
                    likeCount    = likeCounts[r.id] ?: 0,
                    isLiked      = liked,
                    showCheckbox = false,             // ✔ 체크박스 숨김
                    onLikeClick  = {
                        val newState = !liked
                        likeStates[r.id] = newState
                        likeCounts[r.id] = (likeCounts[r.id] ?: 0) +
                                if (newState) 1 else -1
                        // TODO: 서버에 좋아요 상태 전송
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