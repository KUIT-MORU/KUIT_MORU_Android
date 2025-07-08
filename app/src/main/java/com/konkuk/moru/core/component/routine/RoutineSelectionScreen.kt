package com.konkuk.moru.core.component.routine

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.R // 실제 프로젝트의 R 클래스를 임포트하세요.

@Composable
fun RoutineListItem(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    routineName: String,
    tags: List<String>,
    likeCount: Int,
    isLiked: Boolean,
    showCheckbox: Boolean = false,
    isChecked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    onLikeClick: () -> Unit
) {
    // ▼▼▼ [수정됨] Row 전체를 감싸던 clickable Modifier 제거 ▼▼▼
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val imageResource = if (isRunning) R.drawable.ic_routine_square_running else R.drawable.ic_routine_square_stop
        Image(painter = painterResource(id = imageResource), contentDescription = routineName, modifier = Modifier.size(72.dp))

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = routineName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = tags.joinToString(" "), color = Color.Gray, fontSize = 12.sp)

            // 이제 이 Row의 clickable이 정상적으로 동작합니다.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onLikeClick() }
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "좋아요",
                    modifier = Modifier.size(16.dp),
                    tint = if (isLiked) Color.Red else Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = likeCount.toString(),
                    color = if (isLiked) Color.Black else Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        if (showCheckbox) {
            IconButton(onClick = { onCheckedChange(!isChecked) }) {
                val icon = if (isChecked) R.drawable.check_box else R.drawable.empty_box
                Icon(
                    painter = painterResource(icon),
                    contentDescription = "Checkbox",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineSelectionScreen() {
    // 변경점: 데이터 모델에 isLiked 필드 추가
    data class RoutineInfo(
        val id: Int,
        val name: String,
        val tags: List<String>,
        val likes: Int,
        val isLiked: Boolean,
        val isRunning: Boolean,
        val isChecked: Boolean
    )

    val routines = remember {
        listOf(
            RoutineInfo(1, "아침 운동", listOf("#모닝루틴", "#스트레칭"), 16, true, true, true),
            RoutineInfo(2, "저녁 독서", listOf("#취미", "#자기계발"), 32, false, false, false),
            RoutineInfo(3, "영어 단어 암기", listOf("#학습", "#외국어"), 8, false, false, true),
            RoutineInfo(4, "요가", listOf("#운동", "#명상"), 50, true, true, false)
        )
    }

    // 변경점: 체크 상태와 더불어 좋아요 상태/카운트 로컬 관리
    val checkedStates = remember { mutableStateMapOf<Int, Boolean>().apply { routines.forEach { put(it.id, it.isChecked) } } }
    val likedStates = remember { mutableStateMapOf<Int, Boolean>().apply { routines.forEach { put(it.id, it.isLiked) } } }
    val likeCounts = remember { mutableStateMapOf<Int, Int>().apply { routines.forEach { put(it.id, it.likes) } } }


    Scaffold(
        topBar = { TopAppBar(title = { Text("루틴 선택") }) }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(routines) { routine ->
                val isChecked = checkedStates[routine.id] ?: false
                val isLiked = likedStates[routine.id] ?: false
                val currentLikeCount = likeCounts[routine.id] ?: 0

                RoutineListItem(
                    isRunning = routine.isRunning,
                    routineName = routine.name,
                    tags = routine.tags,
                    likeCount = currentLikeCount,
                    isLiked = isLiked,
                    isChecked = isChecked,
                    onCheckedChange = { newCheckedState ->
                        checkedStates[routine.id] = newCheckedState
                        println("서버로 '${routine.name}' 체크 상태 전송: $newCheckedState")
                    },
                    // 변경점: onLikeClick 로직 구현
                    onLikeClick = {
                        val newLikeStatus = !isLiked
                        likedStates[routine.id] = newLikeStatus
                        likeCounts[routine.id] = if (newLikeStatus) currentLikeCount + 1 else currentLikeCount - 1
                        println("서버로 '${routine.name}' 좋아요 상태 전송: $newLikeStatus")
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RoutineSelectionScreenPreview() {
    MaterialTheme {
        RoutineSelectionScreen()
    }
}