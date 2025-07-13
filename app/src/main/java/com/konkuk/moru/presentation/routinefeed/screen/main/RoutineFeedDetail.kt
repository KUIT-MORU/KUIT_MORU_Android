package com.konkuk.moru.presentation.routinefeed.screen.main

import RoutineDetailTopAppBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.konkuk.moru.R
import com.konkuk.moru.core.component.button.MoruButton
import com.konkuk.moru.core.component.chip.MoruChip
import com.konkuk.moru.ui.theme.MORUTheme
// [수정] 통합 Routine 모델 및 관련 클래스 임포트
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.model.RoutineStep
import com.konkuk.moru.data.model.SimilarRoutine


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailScreen(routine: Routine) { // [수정] routineDetail -> routine, 타입 변경
    // [수정] 통합 Routine 모델의 필드를 사용하여 초기 상태 설정
    var isLiked by remember { mutableStateOf(routine.isLiked) }
    var likeCount by remember { mutableStateOf(routine.likes) } // likes 필드 사용
    var isBookmarked by remember { mutableStateOf(routine.isBookmarked) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            RoutineDetailTopAppBar(
                likeCount = likeCount,
                isLiked = isLiked,
                isBookmarked = isBookmarked,
                onLikeClick = {
                    isLiked = !isLiked
                    if (isLiked) likeCount++ else likeCount--
                },
                onBookmarkClick = { isBookmarked = !isBookmarked },
                onBackClick = { /* 뒤로가기 */ }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {
            item { RoutineHeader(routine = routine) } // [수정] routine 전달

            item {
                RoutineStepSection(
                    modifier = Modifier.padding(16.dp),
                    steps = routine.steps // [수정] routine에서 steps 사용
                )
            }

            item {
                SimilarRoutinesSection(
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
                    routines = routine.similarRoutines // [수정] routine에서 similarRoutines 사용
                )
            }
        }
    }
}

@Composable
fun RoutineHeader(routine: Routine) { // [수정] 파라미터 타입 변경
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.BottomStart
    ) {
        AsyncImage(
            model = routine.imageUrl, // [수정] 필드명 변경
            contentDescription = "루틴 대표 이미지",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.ic_antenna_color),
            placeholder = painterResource(id = R.drawable.ic_person_standing)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 400f
                    )
                )
        )
        RoutineInfoOverlay(
            modifier = Modifier.padding(16.dp),
            routine = routine // [수정] routine 전달
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoutineInfoOverlay(modifier: Modifier = Modifier, routine: Routine) { // [수정] 파라미터 타입 변경
    val contentColor = Color.White
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = routine.authorProfileUrl, // [수정] 필드명 변경
                contentDescription = "작성자 프로필",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
            Text(
                text = routine.authorName, // [수정] 필드명 변경
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = routine.title, // [수정] 필드명 변경
                color = contentColor,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            MoruChip(
                text = routine.category, // [수정] 필드명 변경
                onClick = {},
                isSelected = true,
                selectedBackgroundColor = Color(0xFFD9F7A2),
                selectedContentColor = Color.Black,
                unselectedBackgroundColor = Color.Transparent,
                unselectedContentColor = Color.Transparent
            )
        }
        Text(
            text = routine.description, // [수정] 필드명 변경
            color = contentColor,
            style = MaterialTheme.typography.bodyMedium
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            routine.tags.forEach { tag ->
                MoruChip(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(33.dp),
                    text = "#$tag",
                    onClick = {},
                    isSelected = true,
                    selectedBackgroundColor = MORUTheme.colors.darkGray,
                    selectedContentColor = MORUTheme.colors.limeGreen,
                    unselectedBackgroundColor = Color.Transparent,
                    unselectedContentColor = Color.Transparent,
                    contentPadding = PaddingValues(horizontal = 5.dp, vertical = 1.4.dp)
                )
            }
        }
    }
}

// ... RoutineStepSection, RoutineStepItem, SimilarRoutinesSection, SimilarRoutineCard 함수는 수정할 필요 없음 ...
// (이미 통합 모델의 RoutineStep, SimilarRoutine과 구조가 동일하기 때문)
@Composable
fun RoutineStepSection(modifier: Modifier = Modifier, steps: List<RoutineStep>) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("STEP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(16.dp))
            MoruButton(
                text = ("내 루틴에 추가"),
                onClick = { /* ... */ },
                backgroundColor = MORUTheme.colors.limeGreen,
                contentColor = Color.White,
                textStyle = MORUTheme.typography.body_SB_14,
                iconContent = { Icon(Icons.Default.CalendarToday, "캘린더", Modifier.size(16.dp)) }
            )
        }
        Spacer(Modifier.height(16.dp))
        Column {
            steps.forEachIndexed { index, step ->
                RoutineStepItem(stepNumber = index + 1, step = step)
                if (index < steps.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun RoutineStepItem(stepNumber: Int, step: RoutineStep) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$stepNumber", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        Spacer(Modifier.width(16.dp))
        Text(
            text = step.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(text = step.duration, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SimilarRoutinesSection(modifier: Modifier = Modifier, routines: List<SimilarRoutine>) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "이 루틴과 비슷한 루틴",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "더보기",
                modifier = Modifier.rotate(180f)
            )
        }
        Spacer(Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(routines.size) { index ->
                SimilarRoutineCard(routine = routines[index])
            }
        }
    }
}

@Composable
fun SimilarRoutineCard(routine: SimilarRoutine) {
    Column(modifier = Modifier.width(140.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_routine_square_stop),
            contentDescription = routine.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = routine.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = routine.tag, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}


@Preview(showBackground = true)
@Composable
fun RoutineDetailScreenPreview() {
    // [수정] Preview용 샘플 데이터를 통합 Routine 클래스로 생성
    val sampleData = Routine(
        id = 0,
        title = "집중력을 높이는 아침 루틴",
        description = "이 루틴은 당신의 아침을 활기차게 만들어 줄 것입니다.",
        imageUrl = null,
        category = "집중",
        tags = listOf("명상", "독서", "아침"),
        authorName = "모루",
        authorProfileUrl = null,
        likes = 16,
        isLiked = true,
        isBookmarked = false,
        isRunning = false,
        steps = listOf(
            RoutineStep("물 한잔 마시기", "00:30"),
            RoutineStep("5분 명상하기", "05:00"),
            RoutineStep("책 10페이지 읽기", "10:00")
        ),
        similarRoutines = List(5) {
            SimilarRoutine(null, "루틴명명명", "#운동하자")
        }
    )
    MORUTheme { // MORUTheme으로 감싸서 Preview 확인
        RoutineDetailScreen(routine = sampleData)
    }
}