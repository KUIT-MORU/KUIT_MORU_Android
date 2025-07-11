package com.konkuk.moru.core.component.routine

// ... 다른 import 구문들은 그대로 ...
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf // [추가됨]
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.R

@Composable
fun RoutineListItemWithClock(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    routineName: String,
    tags: List<String>,
    likeCount: Int,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onClockClick: () -> Unit // onDeleteClick -> onClockClick 이름 명확화
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val imageResource =
            if (isRunning) R.drawable.ic_routine_square_running else R.drawable.ic_routine_square_stop
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = routineName,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp) // 간격 추가
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically // 수직 정렬 추가
            ) {
                Text(text = routineName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                // [수정됨] 시계 아이콘에 clickable Modifier 추가
                Image(
                    painter = painterResource(id = R.drawable.ic_clock),
                    contentDescription = "시간 설정",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable { onClockClick() } // 클릭 시 콜백 호출
                )
            }
            Text(text = tags.joinToString(" "), color = Color.Gray, fontSize = 12.sp)

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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineSelectionWithClockScreen() {
    data class RoutineInfoWithCheck(
        val id: Int,
        val name: String,
        val tags: List<String>,
        val likes: Int,
        val isLiked: Boolean,
        val isRunning: Boolean,
    )

    val routines = remember {
        listOf(
            RoutineInfoWithCheck(1, "아침 운동", listOf("#모닝루틴", "#스트레칭"), 16, true, true),
            RoutineInfoWithCheck(2, "저녁 독서", listOf("#취미", "#자기계발"), 32, false, false),
            RoutineInfoWithCheck(3, "영어 단어 암기", listOf("#학습", "#외국어"), 8, false, false),
            RoutineInfoWithCheck(4, "요가", listOf("#운동", "#명상"), 50, true, true)
        )
    }

    val likedStates = remember { mutableStateMapOf<Int, Boolean>().apply { routines.forEach { put(it.id, it.isLiked) } } }
    val likeCounts = remember { mutableStateMapOf<Int, Int>().apply { routines.forEach { put(it.id, it.likes) } } }

    // [추가됨] 모달을 띄울 루틴 정보를 저장하는 상태. null이면 모달이 보이지 않음.
    var selectedRoutineForModal by remember { mutableStateOf<RoutineInfoWithCheck?>(null) }

    // [추가됨] 모달(AlertDialog) UI 구현
    if (selectedRoutineForModal != null) {
        AlertDialog(
            onDismissRequest = { selectedRoutineForModal = null }, // 모달 바깥 클릭 시 닫기
            title = {
                Text(text = "'${selectedRoutineForModal!!.name}' 루틴")
            },
            text = {
                Text(text = "여기에 시간 설정과 관련된 UI를 추가할 수 있습니다.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // TODO: 시간 설정 완료 로직
                        selectedRoutineForModal = null // 확인 버튼 클릭 시 닫기
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { selectedRoutineForModal = null } // 취소 버튼 클릭 시 닫기
                ) {
                    Text("취소")
                }
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("루틴 선택") }) }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(routines) { routine ->
                val isLiked = likedStates[routine.id] ?: false
                val currentLikeCount = likeCounts[routine.id] ?: 0

                RoutineListItemWithClock(
                    isRunning = routine.isRunning,
                    routineName = routine.name,
                    tags = routine.tags,
                    likeCount = currentLikeCount,
                    isLiked = isLiked,
                    onLikeClick = {
                        val newLikeStatus = !isLiked
                        likedStates[routine.id] = newLikeStatus
                        likeCounts[routine.id] = if (newLikeStatus) currentLikeCount + 1 else currentLikeCount - 1
                    },
                    // [추가됨] 시계 클릭 시 selectedRoutineForModal 상태에 현재 루틴 정보 저장
                    onClockClick = {
                        selectedRoutineForModal = routine
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoutineSelectionWithClockScreenPreview() {
    MaterialTheme {
        RoutineSelectionWithClockScreen()
    }
}