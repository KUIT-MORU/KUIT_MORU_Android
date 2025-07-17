package com.konkuk.moru.presentation.routinefeed.screen.main

import RoutineDetailTopAppBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.konkuk.moru.R
import com.konkuk.moru.core.component.button.MoruButton
import com.konkuk.moru.core.component.chip.MoruChip
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.model.RoutineStep
import com.konkuk.moru.data.model.SimilarRoutine
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.ui.theme.MORUTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailScreen(
    routine: Routine,
    onBackClick: () -> Unit,
    navController: NavController
) {
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
                item {
                    RoutineHeader(
                        routine = routine,
                        navController = navController
                    )
                }

                item {
                    RoutineStepSection(
                        modifier = Modifier.padding(16.dp),
                        steps = routine.steps
                    )
                }

                item {
                    HorizontalDivider(
                        thickness = 8.dp,
                        color = MORUTheme.colors.veryLightGray
                    )
                }

                item {
                    SimilarRoutinesSection(
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
                onBackClick = onBackClick,
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
fun RoutineHeader(
    routine: Routine,
    navController: NavController
) {
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
        RoutineInfoOverlay(
            modifier = Modifier.padding(16.dp),
            routine = routine,
            navController = navController
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoutineInfoOverlay(
    modifier: Modifier = Modifier, routine: Routine,
    navController: NavController
) {
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
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable {
                // Routine 모델에 authorId가 있다는 가정 하에 작성
                // 만약 없다면 userId를 전달할 방법을 찾아야 합니다.
                navController.navigate(Route.UserProfile.createRoute(routine.authorId))
            }) {
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

                if (index < steps.lastIndex) {
                    Column {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                    }
                } else {
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MORUTheme.colors.veryLightGray)
            .padding(vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { /* TODO: 비슷한 루틴 더보기 화면으로 이동 */ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "이 루틴과 비슷한 루틴",
                style = MORUTheme.typography.title_B_20,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "더보기",
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
        LazyRow(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp, top = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),

            ) {
            items(routines) { routine ->
                SimilarRoutineCard(routine = routine)
            }
        }
    }
}

@Composable
fun SimilarRoutineCard(routine: SimilarRoutine) {
    Column(modifier = Modifier.width(72.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_routine_square_stop),
            contentDescription = routine.name,
            modifier = Modifier
                .size(72.dp)
                .clip(MaterialTheme.shapes.small),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = routine.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            text = routine.tag,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoutineDetailScreenPreview() {
    MORUTheme {
        RoutineDetailScreen(
            routine = DummyData.dummyRoutines.first(),
            onBackClick = {},
            navController = rememberNavController()
        )
    }
}