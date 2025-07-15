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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.konkuk.moru.R
import com.konkuk.moru.core.component.button.MoruButton
import com.konkuk.moru.core.component.chip.MoruChip
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.model.RoutineStep
import com.konkuk.moru.data.model.SimilarRoutine
import com.konkuk.moru.ui.theme.MORUTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailScreen(routine: Routine) {
    var isLiked by remember { mutableStateOf(routine.isLiked) }
    var likeCount by remember { mutableIntStateOf(routine.likes) }
    var isBookmarked by remember { mutableStateOf(routine.isBookmarked) }

    Scaffold(
        containerColor = Color.White,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item { RoutineHeader(routine = routine) }

                item {
                    RoutineStepSection(
                        modifier = Modifier.padding(16.dp),
                        steps = routine.steps
                    )
                }

                // 👇 2. 섹션 사이에 두꺼운 구분선 추가
                item {
                    HorizontalDivider(
                        thickness = 8.dp,
                        color = MORUTheme.colors.veryLightGray
                    )
                }

                item {
                    SimilarRoutinesSection(
                        // 👇 modifier의 top padding 제거
                        modifier = Modifier.padding(bottom = 16.dp),
                        routines = routine.similarRoutines
                    )
                }
            }

            RoutineDetailTopAppBar(
                likeCount = likeCount,
                isLiked = isLiked,
                isBookmarked = isBookmarked,
                onLikeClick = {
                    isLiked = !isLiked
                    if (isLiked) likeCount++ else likeCount--
                },
                onBookmarkClick = { isBookmarked = !isBookmarked },
                onBackClick = { /* 뒤로가기 */ },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    titleContentColor = Color.White
                )
            )
        }
    }
}

@Composable
fun RoutineHeader(routine: Routine) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.BottomStart
    ) {
        AsyncImage(
            model = routine.imageUrl,
            contentDescription = "루틴 대표 이미지",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_profile_with_background),
            error = painterResource(id = R.drawable.ic_launcher_background)
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
        // 이 부분은 이전과 동일하게 유지
        RoutineInfoOverlay(
            modifier = Modifier.padding(16.dp),
            routine = routine
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoutineInfoOverlay(modifier: Modifier = Modifier, routine: Routine) {
    val contentColor = Color.White

    val displayTitle = if (routine.title.length > 8) {
        "${routine.title.take(8)}..."
    } else {
        routine.title
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = routine.authorProfileUrl,
                    contentDescription = "작성자 프로필",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    placeholder = painterResource(id = R.drawable.ic_profile_with_background),
                    error = painterResource(id = R.drawable.ic_profile_with_background)
                )
                Text(
                    text = routine.authorName,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = displayTitle,
                        color = Color(0xFF595959),
                        style = MORUTheme.typography.head_EB_24,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    MoruChip(
                        text = routine.category,
                        onClick = {},
                        isSelected = true,
                        selectedBackgroundColor = Color(0xFFD9F7A2),
                        selectedContentColor = Color(0xFF8CCD00),
                        unselectedBackgroundColor = Color.Transparent,
                        unselectedContentColor = Color.Transparent
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        routine.tags.forEach { tag ->
                            MoruChip(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .height(19.dp),
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
        }

        Text(
            text = routine.description,
            color = contentColor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@Composable
fun RoutineStepSection(modifier: Modifier = Modifier, steps: List<RoutineStep>) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("STEP", style = MORUTheme.typography.title_B_20, fontWeight = FontWeight.Bold)
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
            // 👇 이 부분이 수정되었습니다.
            // 리스트 맨 위의 구분선은 한 줄로 유지합니다.
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Black.copy(alpha = 0.5f)
            )
            steps.forEachIndexed { index, step ->
                RoutineStepItem(
                    stepNumber = steps.indexOf(step) + 1,
                    step = step,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // 마지막 아이템이 아닐 경우에만 두 줄 구분선을 그립니다.
                if (index < steps.lastIndex) {
                    Column {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(6.dp)) // 두 줄 사이의 간격
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    // 마지막 아이템 아래에는 한 줄 구분선만 그립니다.
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun RoutineStepItem(stepNumber: Int, step: RoutineStep, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))
        Text(text = "$stepNumber", style = MORUTheme.typography.body_SB_14, color = Color.Gray)
        Spacer(Modifier.width(41.dp))
        Text(
            text = step.name,
            style = MORUTheme.typography.body_SB_14,
            modifier = Modifier.weight(1f)
        )
        Text(text = step.duration, style = MORUTheme.typography.body_SB_14)
        Spacer(Modifier.width(12.dp))

    }
}


@Composable
fun SimilarRoutinesSection(modifier: Modifier = Modifier, routines: List<SimilarRoutine>) {
    // 👇 1. Column에 배경색과 패딩을 직접 적용
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MORUTheme.colors.veryLightGray)
            .padding(vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "이 루틴과 비슷한 루틴",
                style = MORUTheme.typography.title_B_20,
                fontWeight = FontWeight.Bold
            )
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "더보기",
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
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
    Column(modifier = Modifier.width(72.dp).height(115.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_routine_square_stop),
            contentDescription = routine.name,
            modifier = Modifier
                .height(72.dp)
                .aspectRatio(1f),
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
    MORUTheme {
        RoutineDetailScreen(routine = sampleData)
    }
}