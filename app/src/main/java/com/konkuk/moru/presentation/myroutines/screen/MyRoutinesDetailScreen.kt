package com.konkuk.moru.presentation.myroutines.screen


import MyRoutineDetailContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.R
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.BasicTopAppBar
import com.konkuk.moru.ui.theme.MORUTheme
import kotlin.math.abs

data class MyRoutineDetailUiState(
    val routine: Routine? = null,
    val isLoading: Boolean = true,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRoutineDetailScreen(
    routineId: Int,
    onBackClick: () -> Unit,
    viewModel: MyRoutineDetailViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isEditMode by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = routineId) {
        viewModel.loadRoutine(routineId)
    }

    val routine = uiState.routine

    LaunchedEffect(Unit) {
        viewModel.deleteCompleted.collect {
            onBackClick()
        }
    }
    Scaffold(
        topBar = {
            if (routine != null) {
                BasicTopAppBar(
                    title = if (isEditMode) "루틴 수정" else "내 루틴",
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isEditMode) {
                                viewModel.restoreRoutine() // 필요시 주석 해제
                                isEditMode = false
                            } else {
                                onBackClick()
                            }
                        }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = if (isEditMode) painterResource(id = R.drawable.ic_x) else painterResource(
                                    id = R.drawable.left_arrow
                                ),
                                contentDescription = "Back or Close",
                            )
                        }
                    }
                )
            }
        },
        containerColor = Color.White,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                routine == null -> {
                    Text("루틴 정보를 찾을 수 없습니다.", modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    MyRoutineDetailContent(
                        routine = routine,
                        isEditMode = isEditMode,
                        onEditModeChange = { isEditMode = it },
                        onDelete = { viewModel.deleteRoutine(routineId) },
                        onSave = { viewModel.saveChanges() },
                        onDescriptionChange = viewModel::updateDescription,
                        onCategoryChange = viewModel::updateCategory,
                        onAddTag = { viewModel.addTag() }, // TODO: 태그 입력 UI 필요
                        onDeleteTag = viewModel::deleteTag,
                        onAddStep = viewModel::addStep, // TODO: 스텝 입력 UI 필요
                        onDeleteStep = viewModel::deleteStep,
                        onAddApp = viewModel::addApp, // TODO: 앱 선택 UI 필요
                        onDeleteApp = viewModel::deleteApp,
                        onMoveStep = viewModel::moveStep,
                        onStepNameChange = viewModel::updateStepName
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun MyRoutineDetailScreenPreview() {
    val sampleRoutine = DummyData.feedRoutines.find { it.routineId == 501 }
    val navController = rememberNavController()
    MORUTheme {
        if (sampleRoutine != null) {
            // [수정] Preview 전용 isEditMode 상태 추가
            var isEditMode by remember { mutableStateOf(false) }
            Scaffold(
                topBar = {
                    BasicTopAppBar(
                        title = if (isEditMode) "루틴 수정" else "내 루틴",
                        navigationIcon = {
                            IconButton(onClick = {}) {
                                Icon(
                                    painter = if (isEditMode) painterResource(R.drawable.ic_x) else painterResource(
                                        R.drawable.left_arrow
                                    ),
                                    contentDescription = "Back",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    MyRoutineDetailContent(
                        routine = sampleRoutine,
                        isEditMode = isEditMode,
                        onEditModeChange = { isEditMode = it },
                        onDelete = {},
                        onSave = {},
                        onDescriptionChange = {},
                        onCategoryChange = {},
                        onAddTag = {},
                        onDeleteTag = {},
                        onAddStep = {},
                        onDeleteStep = {},
                        onAddApp = {},
                        onDeleteApp = {},
                        onMoveStep = { _, _ -> },
                        onStepNameChange = { _, _ -> }
                    )
                }
            }
        } else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text("프리뷰용 데이터를 찾을 수 없습니다.")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun MyRoutineDetailScreenPreviewwithedit() {
    val sampleRoutine = DummyData.feedRoutines.find { it.routineId == 501 }
    val navController = rememberNavController()
    MORUTheme {
        if (sampleRoutine != null) {
            // [수정] Preview 전용 isEditMode 상태 추가
            var isEditMode by remember { mutableStateOf(true) }
            Scaffold(
                topBar = {
                    BasicTopAppBar(
                        title = if (isEditMode) "루틴 수정" else "내 루틴",
                        navigationIcon = {
                            IconButton(onClick = {}) {
                                Icon(
                                    painter = if (isEditMode) painterResource(R.drawable.ic_x) else painterResource(
                                        R.drawable.left_arrow
                                    ),
                                    contentDescription = "Back",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    MyRoutineDetailContent(
                        routine = sampleRoutine,
                        isEditMode = isEditMode,
                        onEditModeChange = { isEditMode = it },
                        onDelete = {},
                        onSave = {},
                        onDescriptionChange = {},
                        onCategoryChange = {},
                        onAddTag = {},
                        onDeleteTag = {},
                        onAddStep = {},
                        onDeleteStep = {},
                        onAddApp = {},
                        onDeleteApp = {},
                        onMoveStep = { _, _ -> },
                        onStepNameChange = { _, _ -> }
                    )
                }
            }
        } else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text("프리뷰용 데이터를 찾을 수 없습니다.")
            }
        }
    }
}