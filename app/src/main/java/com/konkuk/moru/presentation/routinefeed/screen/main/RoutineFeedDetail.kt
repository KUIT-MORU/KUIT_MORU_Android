package com.konkuk.moru.presentation.routinefeed.screen.main // 패키지 경로는 맞게 수정해주세요

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.konkuk.moru.R // R 파일 경로는 맞게 수정해주세요
import com.konkuk.moru.core.component.button.MoruButton
import com.konkuk.moru.core.component.chip.MoruChip
import com.konkuk.moru.presentation.routinefeed.data.RoutineDetail
import com.konkuk.moru.presentation.routinefeed.data.RoutineStep
import com.konkuk.moru.presentation.routinefeed.data.SimilarRoutine

/* --- 1. 데이터 모델 (초기 버전 기준) --- */


/* --- 2. 화면 전체를 구성하는 메인 컴포저블 --- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailScreen(routineDetail: RoutineDetail) {
    var isLiked by remember { mutableStateOf(routineDetail.isLiked) }
    var likeCount by remember { mutableStateOf(routineDetail.likeCount) }
    var isBookmarked by remember { mutableStateOf(routineDetail.isBookmarked) }

    Scaffold(
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
            // 1, 2번 요청 반영: 이미지와 정보를 합친 헤더
            item { RoutineHeader(routineDetail = routineDetail) }

            // STEP 섹션
            item {
                RoutineStepSection(
                    modifier = Modifier.padding(16.dp),
                    steps = routineDetail.steps
                )
            }

            // 비슷한 루틴 섹션
            item {
                SimilarRoutinesSection(
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
                    routines = routineDetail.similarRoutines
                )
            }
        }
    }
}


/* --- 3. 화면을 구성하는 하위 컴포저블들 --- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailTopAppBar(
    likeCount: Int,
    isLiked: Boolean,
    isBookmarked: Boolean,
    onLikeClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { /* 제목 없음 */ },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
            }
        },
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "좋아요",
                        tint = if (isLiked) Color.Red else Color.Gray
                    )
                }
                Text(text = "$likeCount", style = MaterialTheme.typography.bodyLarge)
            }
            IconButton(onClick = onBookmarkClick) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = "북마크",
                    // 3번 요청 반영: 북마크 활성화 색상 변경
                    tint = if (isBookmarked) Color.Black else Color.Gray
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun RoutineHeader(routineDetail: RoutineDetail) {
    Box(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        contentAlignment = Alignment.BottomStart
    ) {
        AsyncImage(
            model = routineDetail.imageUrl,
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
            routineDetail = routineDetail
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoutineInfoOverlay(modifier: Modifier = Modifier, routineDetail: RoutineDetail) {
    val contentColor = Color.White
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AsyncImage(model = routineDetail.authorProfileUrl, contentDescription = "작성자 프로필", modifier = Modifier.size(32.dp).clip(CircleShape))
            Text(text = routineDetail.authorName, color = contentColor, fontWeight = FontWeight.Bold)
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = routineDetail.routineTitle, color = contentColor, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            MoruChip(
                text = routineDetail.routineCategory,
                onClick = {},
                isSelected = true,
                selectedBackgroundColor = Color(0xFFD9F7A2),
                selectedContentColor = Color.Black,
                unselectedBackgroundColor = Color.Transparent,
                unselectedContentColor = Color.Transparent
            )
        }
        Text(text = routineDetail.routineDescription, color = contentColor, style = MaterialTheme.typography.bodyMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            routineDetail.tags.forEach { tag ->
                MoruChip(
                    text = "#$tag",
                    onClick = {},
                    isSelected = true,
                    selectedBackgroundColor = Color.White.copy(alpha = 0.2f),
                    selectedContentColor = contentColor,
                    unselectedBackgroundColor = Color.Transparent,
                    unselectedContentColor = Color.Transparent
                )
            }
        }
    }
}

@Composable
fun RoutineStepSection(modifier: Modifier = Modifier, steps: List<RoutineStep>) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("STEP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            MoruButton(
                text = "내 루틴에 추가",
                onClick = { /* ... */ },
                backgroundColor = Color(0xFFF1F3F5),
                contentColor = Color.Black,
                fontSize = 12.sp,
                iconContent = { Icon(Icons.Default.CalendarToday, "캘린더", Modifier.size(16.dp)) }
            )
        }
        Spacer(Modifier.height(16.dp))
        Column {
            steps.forEachIndexed { index, step ->
                RoutineStepItem(stepNumber = index + 1, step = step)
                // 5번 요청 반영: 구분선 추가
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
        Text(text = step.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(text = step.duration, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SimilarRoutinesSection(modifier: Modifier = Modifier, routines: List<SimilarRoutine>) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "이 루틴과 비슷한 루틴", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "더보기", modifier = Modifier.rotate(180f))
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
        // 4번 요청 반영: 보내주신 아이콘 사용
        Image(
            painter = painterResource(id = R.drawable.ic_routine_square_stop),
            contentDescription = routine.name,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(8.dp))
        Text(text = routine.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text(text = routine.tag, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

/* --- 4. Preview --- */
@Preview(showBackground = true)
@Composable
fun RoutineDetailScreenPreview() {
    val sampleData = RoutineDetail(
        imageUrl = null, // Preview에서는 기본 아이콘 표시됨
        authorName = "모루",
        authorProfileUrl = null,
        routineTitle = "집중력을 높이는 아침 루틴",
        routineCategory = "집중",
        routineDescription = "이 루틴은 당신의 아침을 활기차게 만들어 줄 것입니다.",
        tags = listOf("명상", "독서", "아침"),
        likeCount = 16,
        isLiked = true,
        isBookmarked = false,
        steps = listOf(
            RoutineStep("물 한잔 마시기", "00:30"),
            RoutineStep("5분 명상하기", "05:00"),
            RoutineStep("책 10페이지 읽기", "10:00")
        ),
        similarRoutines = List(5) {
            SimilarRoutine(null, "루틴명명명", "#운동하자")
        }
    )
    MaterialTheme {
        RoutineDetailScreen(routineDetail = sampleData)
    }
}