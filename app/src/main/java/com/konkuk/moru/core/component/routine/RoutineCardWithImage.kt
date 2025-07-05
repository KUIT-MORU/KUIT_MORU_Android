package com.konkuk.moru.core.component.routine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.R // 실제 프로젝트의 R 클래스를 임포트하세요.

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                RoutineListWithImageScreen()
            }
        }
    }
}

@Composable
fun RoutineCardWithImage(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    routineName: String,
    tag: String,
    likeCount: Int,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier.clickable { onClick() }
    ) {
        val imageResource = if (isRunning) {
            R.drawable.ic_routine_rectangle_running
        } else {
            R.drawable.ic_routine_rectangle_stop
        }

        Image(
            painter = painterResource(id = imageResource),
            contentDescription = routineName,
            modifier = Modifier
                .width(98.dp)
                .height(130.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = routineName,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "#$tag",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onLikeClick() }
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "좋아요",
                tint = if (isLiked) Color.Red else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = likeCount.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isLiked) Color.Black else Color.Gray
            )
        }
    }
}

@Composable
fun RoutineListWithImageScreen() {
    // 1. 서버에서 받아온 초기 데이터 모델
    data class RoutineInfo(
        val id: Int,
        val name: String,
        val tag: String,
        val likes: Int,
        val isRunning: Boolean,
        val isLiked: Boolean
    )

    // 2. 서버에서 받았다고 가정한 샘플 데이터
    val routines = remember {
        listOf(
            RoutineInfo(1, "매일 조깅하기", "운동", 16, isRunning = true, isLiked = true),
            RoutineInfo(2, "아침 책읽기", "독서", 25, isRunning = false, isLiked = false),
            RoutineInfo(3, "영어 공부", "학습", 8, isRunning = true, isLiked = false),
            RoutineInfo(4, "요리 배우기", "취미", 112, isRunning = false, isLiked = true),
        )
    }

    // 3. '좋아요' 상태와 카운트를 UI에서 즉시 반영하기 위한 로컬 상태
    //    서버에서 받은 초기값으로 한 번만 초기화합니다.
    val likedStates = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            routines.forEach { put(it.id, it.isLiked) }
        }
    }
    val likeCounts = remember {
        mutableStateMapOf<Int, Int>().apply {
            routines.forEach { put(it.id, it.likes) }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            Text(
                text = "루틴카드 (좋아요 기능 적용)",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(16.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(routines) { routine ->
                    // UI에 표시할 값은 로컬 상태에서 가져옵니다.
                    val isLiked = likedStates[routine.id] ?: routine.isLiked
                    val currentLikeCount = likeCounts[routine.id] ?: routine.likes

                    RoutineCardWithImage(
                        isRunning = routine.isRunning,
                        routineName = routine.name,
                        tag = routine.tag,
                        likeCount = currentLikeCount,
                        isLiked = isLiked,
                        onClick = {
                            // TODO: 상세 화면으로 이동하는 로직 구현
                            println("${routine.name} 카드 클릭됨!")
                        },
                        onLikeClick = {
                            // 로컬 UI 상태를 즉시 업데이트 (Optimistic UI)
                            val newLikeStatus = !isLiked
                            likedStates[routine.id] = newLikeStatus
                            likeCounts[routine.id] = if (newLikeStatus) currentLikeCount + 1 else currentLikeCount - 1

                            // TODO: ViewModel을 통해 서버에 API 요청을 보내는 로직 구현
                            println("서버로 '${routine.name}' 좋아요 상태($newLikeStatus) 전송 요청")
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoutineListWithImageScreenPreview() {
    MaterialTheme {
        RoutineListWithImageScreen()
    }
}