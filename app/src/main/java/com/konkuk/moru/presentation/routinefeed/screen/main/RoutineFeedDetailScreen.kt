package com.konkuk.moru.presentation.routinefeed.screen.main

import RoutineDetailTopAppBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.presentation.routinefeed.component.RoutineDetail.RoutineHeader
import com.konkuk.moru.presentation.routinefeed.component.RoutineDetail.RoutineStepSection
import com.konkuk.moru.presentation.routinefeed.component.RoutineDetail.SimilarRoutinesSection
import com.konkuk.moru.presentation.routinefeed.viewmodel.RoutineDetailViewModel
import com.konkuk.moru.ui.theme.MORUTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailScreen(
    routineId: String,
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: RoutineDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = routineId) {
        viewModel.loadRoutine(routineId)
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val routine = uiState.routine
    if (routine == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("루틴 정보를 불러오지 못했습니다.")
        }
        return
    }

    var isLiked by remember(routine.routineId) { mutableStateOf(routine.isLiked) }
    var likeCount by remember(routine.routineId) { mutableIntStateOf(routine.likes) }
    var isBookmarked by remember(routine.routineId) { mutableStateOf(routine.isBookmarked) }

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
                        onProfileClick = { authorId ->
                            navController.navigate(Route.UserProfile.createRoute(authorId))
                        }
                    )
                }

                item {
                    RoutineStepSection(
                        modifier = Modifier.padding(16.dp),
                        routine = routine,
                        showAddButton = uiState.canBeAddedToMyRoutines,
                        onAddToMyRoutineClick = { viewModel.copyRoutineToMyList() }
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
                        routines = uiState.similarRoutines,
                        onRoutineClick = { clickedRoutineId ->
                            navController.navigate(
                                Route.RoutineFeedDetail.createRoute(
                                    clickedRoutineId
                                )
                            )
                        },
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp) // TopAppBar 높이보다 약간 넉넉하게 설정
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent)
                        )
                    )
            )


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




@Preview(showBackground = true)
@Composable
fun RoutineDetailScreenPreview() {
    MORUTheme {
        // [수정] routineId를 전달하도록 Preview 수정
        RoutineDetailScreen(
            routineId = DummyData.feedRoutines.first().routineId,
            onBackClick = {},
            navController = rememberNavController(),
        )
    }
}