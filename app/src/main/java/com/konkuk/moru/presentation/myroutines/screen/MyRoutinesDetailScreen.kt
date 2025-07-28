package com.konkuk.moru.presentation.myroutines.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.konkuk.moru.R
import com.konkuk.moru.core.component.Switch.CustomToggleSwitch
import com.konkuk.moru.core.component.button.MoruButton
import com.konkuk.moru.core.component.chip.MoruChip
import com.konkuk.moru.data.model.AppInfo
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.model.RoutineStep
import com.konkuk.moru.presentation.routinefeed.component.modale.CenteredInfoDialog
import com.konkuk.moru.presentation.routinefeed.component.modale.CustomDialog
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.BasicTopAppBar
import com.konkuk.moru.ui.theme.MORUTheme
import com.konkuk.moru.ui.theme.moruFontBold
import kotlinx.coroutines.delay


data class MyRoutineDetailUiState(
    val routine: Routine? = null,
    val isLoading: Boolean = true,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRoutineDetailScreen(
    routineId: Int,
    navController: NavController,
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
                    title = if (isEditMode) "루틴 생성" else "내 루틴",
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isEditMode) {
                                isEditMode = false // 수정 모드일 때 X 버튼을 누르면 모드가 꺼짐
                            } else {
                                onBackClick() // 평소에는 뒤로가기
                            }
                        }) {
                            Icon(
                                modifier = Modifier.size(14.dp),
                                // 수정 모드일 때는 'X' 아이콘, 아닐 때는 뒤로가기 아이콘 표시
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
                    // [수정] ViewModel의 데이터를 실제 UI를 그리는 Composable에 전달
                    MyRoutineDetailContent(
                        routine = routine,
                        navController = navController,
                        isEditMode = isEditMode,
                        onEditModeChange = { isEditMode = it },
                        onDelete = { viewModel.deleteRoutine(routineId) },
                        onSave = { newDescription, newCategory ->
                            viewModel.updateRoutine(routine.routineId, newDescription, newCategory)
                        }
                    )
                }
            }
        }
    }
}

// [수정] 실제 UI를 그리는 부분을 별도의 Composable로 분리
@Composable
private fun MyRoutineDetailContent(
    routine: Routine,
    navController: NavController,
    isEditMode: Boolean, // ✨ 부모로부터 상태를 전달 받음
    onEditModeChange: (Boolean) -> Unit, // ✨ 부모의 상태를 변경하는 함수를 전달 받음
    onDelete: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var description by remember(routine.description) {
        mutableStateOf(routine.description.ifEmpty { "설명을 입력해주세요." })
    }
    var category by remember(routine.category) { mutableStateOf(routine.category) }
    var showEditConfirmDialog by remember { mutableStateOf(false) }
    var showEditCompleteDialog by remember { mutableStateOf(false) }

// ✨ 2. "수정 완료하시겠습니까?" 확인 모달을 추가합니다.
    if (showEditConfirmDialog) {
        CustomDialog(
            onDismissRequest = { showEditConfirmDialog = false },
            onConfirmation = {
                showEditConfirmDialog = false // 확인 모달 닫기
                onSave(description, category)           // 데이터 저장
                showEditCompleteDialog = true // "수정되었습니다!" 모달 띄우기
                onEditModeChange(false)     // 수정 모드 종료
            },
            confirmButtonText = "확인", // 버튼 텍스트
            content = {
                Text(
                    text = "수정 완료하시겠습니까?",
                    style = MORUTheme.typography.title_B_20,
                    textAlign = TextAlign.Center
                )
            }
        )
    }

    if (showEditCompleteDialog) {
        CenteredInfoDialog(
            onDismissRequest = { showEditCompleteDialog = false },
            content = {
                Text(
                    text = "수정되었습니다!",
                    color = Color(0xFFE0E0E0),
                    style = MORUTheme.typography.desc_M_14
                )
            }
        )
        LaunchedEffect(Unit) {
            delay(1500)
            showEditCompleteDialog = false
        }
    }


    Column(modifier = Modifier.fillMaxSize()) {
        // [수정] 스크롤이 필요한 모든 컨텐츠를 LazyColumn으로 감쌉니다.
        LazyColumn(
            modifier = Modifier
                .background(Color.White)
                .weight(1f) // LazyColumn이 버튼을 제외한 모든 공간을 차지하도록 합니다.
        ) {
            // 1. 이미지 (오버레이 없는 단순 이미지)
            item {
                RoutineItemCard(
                    // 카드 스타일에 맞게 좌우 패딩을 추가합니다.
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    // 현재 상세 페이지의 routine 객체에서 데이터를 가져와 사용합니다.
                    imageUrl = routine.imageUrl,
                    title = routine.title,
                    routine = routine,
                    isEditMode = isEditMode,
                    onDelete = onDelete,
                    description = description,
                    category = category,
                    onDescriptionChange = { newDescription ->
                        description = newDescription
                    },
                    onCategoryChange = { newCategory ->
                        category = newCategory
                    }
                )
            }

            // 2. 루틴 정보 (이미지 바로 아래)
            item {
                RoutineInfo(routine = routine)
            }

            // 3. STEP 목록
            item {
                RoutineSteps(steps = routine.steps) // 스텝 제목 부분을 분리
            }

            // [수정] "사용앱" 섹션을 LazyColumn의 아이템으로 추가합니다.
            if (routine.usedApps.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    UsedAppsSection(apps = routine.usedApps)
                }
            }

            // 맨 아래 여백
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // [유지] "수정하기" 버튼은 LazyColumn 밖에 위치하여 하단에 고정됩니다.
        MoruButton(
            text = if (isEditMode) "수정 완료" else "수정하기", // ✨ 버튼 텍스트 변경
            onClick = {
                // ✨ [수정 3] "수정 완료" 버튼 클릭 시 모달을 띄우는 로직 추가
                if (isEditMode) {
                    showEditConfirmDialog = true
                } else {
                    onEditModeChange(true)
                } // 부모의 isEditMode 상태를 토글
            }, // ✨ 클릭 시 수정 모드 토글
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth(),
            backgroundColor = MORUTheme.colors.limeGreen,
            contentColor = MORUTheme.colors.black,
            shape = RoundedCornerShape(size = 0.dp)
        )
    }
}


@Composable
fun RoutineItemCard(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    title: String,
    isEditMode: Boolean,
    routine: Routine,
    onDelete: () -> Unit,
    description: String,
    category: String,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit
) {
// 1. 설명 텍스트와 수정 모드 상태

    // 2. 삭제 확인 및 완료 모달 표시 상태
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteCompleteDialog by remember { mutableStateOf(false) }
    // 체크박스의 클릭 상태를 기억하는 변수
    var isUserChecked by remember { mutableStateOf(false) }

    if (showDeleteConfirmDialog) {
        CustomDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            onConfirmation = {
                showDeleteConfirmDialog = false
                showDeleteCompleteDialog = true
                onDelete()
            },
            content = {
                Text(
                    text = "루틴을 삭제하시겠습니까?",
                    style = MORUTheme.typography.title_B_20,
                    textAlign = TextAlign.Center
                )
            }
        )
    }
    if (showDeleteCompleteDialog) {
        CenteredInfoDialog(
            onDismissRequest = { showDeleteCompleteDialog = false },
            content = {
                Text(
                    text = "삭제되었습니다!",
                    color = Color(0xFFE0E0E0),
                    style = MORUTheme.typography.desc_M_14
                )
            }
        )
        // 1.5초 후에 자동으로 닫히도록 설정
        LaunchedEffect(Unit) {
            delay(1500)
            showDeleteCompleteDialog = false
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // 아이템의 전체 높이를 고정
            .background(Color.White, shape = RoundedCornerShape(12.dp)),
        //.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // 1. 왼쪽 이미지 (AsyncImage)
        AsyncImage(
            model = imageUrl,
            contentDescription = "루틴 대표 이미지",
            modifier = Modifier
                .width(105.dp)
                .height(140.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_routine_card_basic),
            error = painterResource(id = R.drawable.ic_routine_card_basic)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 2. 오른쪽 정보 섹션 (Column)
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f) // 남은 공간을 모두 차지
        ) {
            // 2-1. 루틴 제목
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontFamily = moruFontBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!isEditMode) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { showDeleteConfirmDialog = true },
                        tint = MORUTheme.colors.mediumGray,
                        painter = painterResource(R.drawable.ic_trash),
                        contentDescription = " 쓰레기통"
                    )
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2-2. 카테고리 칩
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White
                    ),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Row(modifier = Modifier) {
                    Icon(
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { isUserChecked = !isUserChecked },
                        painter = painterResource(if (isUserChecked) R.drawable.ic_checkbox_uncheck else R.drawable.ic_checkbox_gray),
                        contentDescription = "체크",
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "사용자 표시",
                        color = MORUTheme.colors.darkGray,
                        style = MORUTheme.typography.time_R_12
                    )
                }
                if (!isEditMode) {
                    MoruChip(
                        modifier = Modifier.height(28.dp),
                        text = category,
                        onClick = {},
                        isSelected = true,
                        selectedBackgroundColor = Color(0xFFEBFFC0),
                        selectedContentColor = Color(0xFF8CCD00),
                        unselectedBackgroundColor = Color.Transparent,
                        unselectedContentColor = Color.Transparent
                    )

                } else {
                    var checked1 by remember { mutableStateOf(false) }
                    CustomToggleSwitch(
                        checked = category == "집중",
                        onCheckedChange = { isChecked ->
                            onCategoryChange(if (isChecked) "집중" else "간편")
                        },
                        leftText = "간편",
                        rightText = "집중",
                        containerColor = Color(0xFFE8E8E8),
                        thumbColor = Color(0xFFEBFFC0),
                        checkedTextColor = Color(0xFF8CCD00),
                        uncheckedTextColor = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .width(95.dp)
                            .height(26.dp)
                    )

                }

            }

            // 2-3. 하단 사용자 정보로 밀어내기 위한 Spacer
            //Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((74.dp))
                    .background(
                        color = MORUTheme.colors.veryLightGray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(10.dp) // ✨ [수정 1] Box에 전체적으로 10.dp 패딩 적용
            ) {
                if (isEditMode) {
                    // --- 수정 모드일 때: BasicTextField ---
                    BasicTextField(
                        value = description, // ✨ 파라미터로 받은 description 사용
                        onValueChange = onDescriptionChange, // ✨ 파라미터로 받은 onDescriptionChange 사용
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MORUTheme.typography.time_R_14.copy(color = Color.Black)
                    )
                } else {
                    // --- 평소 상태일 때: Text (읽기 전용) ---
                    Text(
                        text = description,
                        modifier = Modifier.fillMaxWidth(),
                        style = MORUTheme.typography.time_R_14.copy(
                            color = if (description == "설명을 입력해주세요.") Color.Gray else Color.Black
                        ),
                        maxLines = 3, // ✨ 3줄까지만 보이도록 제한
                        overflow = TextOverflow.Ellipsis // ✨ 범위 넘어가면 '...' 처리
                    )
                }
            }
        }
    }
}


@Composable
private fun RoutineInfo(routine: Routine) {

    Column(
        modifier = Modifier.padding(start = 17.dp, top = 25.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(routine.tags) { tag ->
                MoruChip(
                    text = "#$tag",
                    onClick = {},
                    isSelected = true,
                    selectedBackgroundColor = MORUTheme.colors.charcoalBlack,
                    selectedContentColor = Color(0xFFB8EE44),
                    unselectedBackgroundColor = Color.Transparent,
                    unselectedContentColor = Color.Transparent,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

    }
}


@Composable
private fun RoutineSteps(steps: List<RoutineStep>) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = "STEP",
            style = MORUTheme.typography.title_B_20,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column {
            HorizontalDivider(thickness = 1.dp, color = Color.Black.copy(alpha = 0.5f))

            steps.forEachIndexed { index, step ->
                LocalRoutineStepItem(stepNumber = index + 1, step = step)

                if (index < steps.lastIndex) {
                    Column {
                        HorizontalDivider(thickness = 1.dp, color = Color.Black.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color.Black.copy(alpha = 0.5f))
                    }
                } else {
                    HorizontalDivider(thickness = 1.dp, color = Color.Black.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
private fun LocalRoutineStepItem(stepNumber: Int, step: RoutineStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "%2d".format(stepNumber),
            style = MORUTheme.typography.title_B_12,
            color = MORUTheme.colors.darkGray,
        )
        Spacer(Modifier.width(36.dp))
        Text(
            text = step.name,
            style = MORUTheme.typography.body_SB_14,
            color = MORUTheme.colors.black,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = step.duration,
            style = MORUTheme.typography.body_SB_14,
            color = MORUTheme.colors.darkGray,
        )
    }
}

@Composable
private fun UsedAppsSection(apps: List<AppInfo>) {
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text("사용 앱", style = MORUTheme.typography.title_B_20, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(apps) { app ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = app.iconUrl,
                        contentDescription = app.name,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MORUTheme.colors.veryLightGray)
                            .padding(8.dp),
                        placeholder = painterResource(id = R.drawable.ic_reset), // 예시: 로딩 중 아이콘
                        error = painterResource(id = R.drawable.ic_info)       // 예시: 에러 아이콘
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(app.name, style = MORUTheme.typography.time_R_12)
                }
            }
        }
    }
}


// [수정] 프리뷰는 ViewModel 없이 동작하도록 수정
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun MyRoutineDetailScreenPreview() {
    val sampleRoutine = DummyData.feedRoutines.find { it.routineId == 501 }
    val navController = rememberNavController()
    MORUTheme {
        if (sampleRoutine != null) {
            Scaffold(
                topBar = {
                    BasicTopAppBar(
                        title = sampleRoutine.title,
                        navigationIcon = {
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    // [수정] 프리뷰에서는 UI Content Composable을 직접 호출
                    MyRoutineDetailContent(
                        routine = sampleRoutine,
                        navController = navController,
                        isEditMode = false,
                        onEditModeChange = {},
                        onDelete = {},
                        onSave = { _, _ -> }
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