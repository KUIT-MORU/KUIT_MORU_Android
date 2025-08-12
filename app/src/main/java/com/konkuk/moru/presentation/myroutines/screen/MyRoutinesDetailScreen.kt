package com.konkuk.moru.presentation.myroutines.screen

import MyRoutineDetailContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.konkuk.moru.R
import com.konkuk.moru.core.component.routinedetail.DraggableAppSearchBottomSheet
import com.konkuk.moru.presentation.myroutines.viewmodel.MyRoutineDetailViewModel
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.BasicTopAppBar
import com.konkuk.moru.ui.theme.MORUTheme
import androidx.activity.compose.BackHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRoutineDetailScreen(
    routineId: String,
    onBackClick: () -> Unit,
    viewModel: MyRoutineDetailViewModel = viewModel(),
) {
    // ✨ ViewModel의 UiState를 구독하여 단일 진실 공급원 원칙을 따름
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val allApps by viewModel.availableApps.collectAsStateWithLifecycle()
    val selectedAppList = uiState.routine?.usedApps ?: emptyList()

    LaunchedEffect(Unit) {
        viewModel.loadRoutine(routineId)
        viewModel.loadInstalledApps(context)
    }

    BackHandler(enabled = uiState.isEditMode) {
        viewModel.restoreRoutine()
        viewModel.setEditMode(false)
    }

    LaunchedEffect(Unit) {
        viewModel.deleteCompleted.collect {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            // uiState.routine이 null이 아닐 때만 TopAppBar를 보여줍니다.
            uiState.routine?.let {
                BasicTopAppBar(
                    title = if (uiState.isEditMode) "루틴 수정" else "내 루틴",
                    navigationIcon = {
                        IconButton(onClick = {
                            if (uiState.isEditMode) {
                                viewModel.restoreRoutine()
                                viewModel.setEditMode(false) // ✨ ViewModel에 모드 변경 요청
                            } else {
                                onBackClick()
                            }
                        }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = if (uiState.isEditMode) painterResource(id = R.drawable.ic_x)
                                else painterResource(id = R.drawable.left_arrow),
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

                uiState.routine == null -> {
                    Text("루틴 정보를 찾을 수 없습니다.", modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    // ✨ Content에는 UiState와 ViewModel만 전달하여 구조를 단순화
                    MyRoutineDetailContent(
                        viewModel = viewModel,
                        onOpenBottomSheet = { isBottomSheetOpen = true }
                    )
                }
            }
        }
    }
    DraggableAppSearchBottomSheet(
        isVisible = isBottomSheetOpen,
        onDismiss = { isBottomSheetOpen = false },
        appList = allApps,
        selectedAppList = selectedAppList,
        onAddApp = { app -> viewModel.addApp(app) },
        onRemoveApp = { app -> viewModel.deleteApp(app) }
    )
}



@Preview(showBackground = true, name = "상세 화면 - 보기 모드")
@Composable
private fun MyRoutineDetailScreenPreview_ViewMode() {
    MORUTheme {
        val viewModel: MyRoutineDetailViewModel = viewModel()
        viewModel.loadRoutine("routine-501")

        MyRoutineDetailScreen(
            routineId = "routine-501",
            onBackClick = {},
            viewModel = viewModel
        )
    }
}

@Preview(showBackground = true, name = "상세 화면 - 수정 모드")
@Composable
private fun MyRoutineDetailScreenPreview_EditMode() {
    MORUTheme {
        val viewModel: MyRoutineDetailViewModel = viewModel()
        viewModel.loadRoutine("routine-501")
        viewModel.setEditMode(true) // 프리뷰를 위해 수정 모드로 설정

        MyRoutineDetailScreen(
            routineId = "routine-501",
            onBackClick = {},
            viewModel = viewModel
        )
    }
}