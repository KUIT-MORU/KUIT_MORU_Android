package com.konkuk.moru.presentation.myroutines.screen

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress // [추가]
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.graphics.graphicsLayer

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
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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


@Composable
private fun MyRoutineDetailContent(
    routine: Routine,
    isEditMode: Boolean,
    onEditModeChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onSave: () -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onAddTag: () -> Unit,
    onDeleteTag: (String) -> Unit,
    onAddStep: () -> Unit,
    onDeleteStep: (Int) -> Unit,
    onMoveStep: (from: Int, to: Int) -> Unit,
    onAddApp: () -> Unit,
    onDeleteApp: (AppInfo) -> Unit,
    onStepNameChange: (index: Int, newName: String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var verticalDragOffset by remember { mutableStateOf(0f) }
    var dragJob by remember { mutableStateOf<Job?>(null) }

    // LazyColumn에서 헤더 아이템의 개수. 이 값은 인덱스 보정에 사용됩니다.
    val headerItemCount = 3

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .background(Color.White)
                .weight(1f)
            // LazyColumn 전체에 적용했던 gestureModifier 제거
        ) {
            item {
                RoutineItemCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    imageUrl = routine.imageUrl,
                    title = routine.title,
                    isEditMode = isEditMode,
                    onDelete = onDelete,
                    description = routine.description,
                    category = routine.category,
                    onDescriptionChange = onDescriptionChange,
                    onCategoryChange = onCategoryChange,
                )
            }

            item {
                RoutineInfo(
                    tags = routine.tags,
                    isEditMode = isEditMode,
                    onAddTag = onAddTag,
                    onDeleteTag = onDeleteTag
                )
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = "STEP",
                        style = MORUTheme.typography.title_B_20,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color.Black.copy(alpha = 0.5f))
                }
            }

            itemsIndexed(items = routine.steps, key = { _, step -> step.id }) { index, item ->
                val isDragging = draggedItemIndex == index
                val offset = if (isDragging) verticalDragOffset else 0f

                val dragHandleModifier = if (isEditMode) {
                    Modifier.pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                draggedItemIndex = index
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                verticalDragOffset += dragAmount.y
                            },
                            onDragEnd = {
                                val currentDraggedIndex = draggedItemIndex
                                if (currentDraggedIndex != null) {
                                    // 드롭된 위치의 새로운 인덱스를 찾음
                                    val newIndex = findNewIndex(
                                        draggedItemIndex = currentDraggedIndex,
                                        verticalDragOffset = verticalDragOffset,
                                        listState = listState,
                                        headerItemCount = headerItemCount
                                    )
                                    // 유효한 인덱스를 찾았고, 위치가 변경되었으면 onMoveStep 호출
                                    if (newIndex != null && newIndex != currentDraggedIndex) {
                                        onMoveStep(currentDraggedIndex, newIndex)
                                    }
                                }
                                // 드래그 상태 초기화
                                draggedItemIndex = null
                                verticalDragOffset = 0f
                            },
                            onDragCancel = {
                                draggedItemIndex = null
                                verticalDragOffset = 0f
                            }
                        )
                    }
                } else Modifier

                Column(
                    modifier = Modifier
                        .zIndex(if (isDragging) 1f else 0f)
                        .graphicsLayer {
                            translationY = offset
                            shadowElevation = if (isDragging) 8f else 0f
                        }
                ) {
                    LocalRoutineStepItem(
                        stepNumber = index + 1,
                        step = item,
                        isEditMode = isEditMode,
                        onDeleteClick = { onDeleteStep(index) },
                        onNameChange = { newName -> onStepNameChange(index, newName) },
                        dragHandleModifier = dragHandleModifier
                    )
                }

                // 구분선 로직 (기존과 동일)
                if (index < routine.steps.lastIndex) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HorizontalDivider(thickness = 1.dp, color = Color.Black.copy(alpha = 0.5f))
                    }
                } else {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                }
            }

            if (isEditMode) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(onClick = onAddStep) {
                            Box(
                                modifier = Modifier
                                    .size(29.dp)
                                    .background(
                                        color = MORUTheme.colors.lightGray,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "STEP 추가",
                                    tint = MORUTheme.colors.mediumGray,
                                    modifier = Modifier.size(21.dp)
                                )
                            }
                        }
                    }
                }
            }

            // UsedApps 섹션
            if (routine.usedApps.isNotEmpty() || isEditMode) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    UsedAppsSection(
                        apps = routine.usedApps,
                        isEditMode = isEditMode,
                        onAddApp = onAddApp,
                        onDeleteApp = onDeleteApp
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }

        MoruButton(
            text = if (isEditMode) "완료하기" else "수정하기",
            onClick = {
                if (isEditMode) {
                    onSave() // 저장 함수 호출
                }
                onEditModeChange(!isEditMode) // 모드 전환
            },
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth(),
            backgroundColor = MORUTheme.colors.limeGreen,
            contentColor = MORUTheme.colors.black,
            shape = RoundedCornerShape(size = 0.dp)
        )
    }
}


private fun findNewIndex(
    draggedItemIndex: Int,
    verticalDragOffset: Float,
    listState: LazyListState,
    headerItemCount: Int
): Int? {
    val layoutInfo = listState.layoutInfo
    // 실제 LazyColumn에서의 아이템 인덱스 (헤더 개수 더하기)
    val absoluteDraggedItemIndex = draggedItemIndex + headerItemCount

    val draggedItem = layoutInfo.visibleItemsInfo.find { it.index == absoluteDraggedItemIndex } ?: return null
    val draggedItemCenter = draggedItem.offset + draggedItem.size / 2 + verticalDragOffset

    return layoutInfo.visibleItemsInfo
        .filter {
            // STEP 리스트 범위 내에 있고, 자기 자신이 아닌 아이템만 필터링
            it.index >= headerItemCount && it.index < layoutInfo.totalItemsCount && it.index != absoluteDraggedItemIndex
        }
        .minByOrNull {
            val itemCenter = it.offset + it.size / 2
            abs(itemCenter - draggedItemCenter)
        }
        // 찾은 아이템의 인덱스에서 헤더 개수를 빼서 실제 데이터 리스트의 인덱스로 변환
        ?.index?.minus(headerItemCount)
}

@Composable
private fun LocalRoutineStepItem(
    stepNumber: Int,
    step: RoutineStep,
    isEditMode: Boolean,
    onDeleteClick: () -> Unit,
    onNameChange: (String) -> Unit,
    dragHandleModifier: Modifier = Modifier // 드래그 핸들 Modifier를 파라미터로 받음
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isEditMode) {
            Icon(
                painter = painterResource(id = R.drawable.ic_menu),
                contentDescription = "Drag Handle",
                // 부모로부터 전달받은 Modifier를 여기에 적용
                modifier = Modifier
                    .size(24.dp)
                    .then(dragHandleModifier),
                tint = MORUTheme.colors.darkGray
            )
        } else {
            Text(
                text = "%02d".format(stepNumber),
                style = MORUTheme.typography.title_B_12,
                color = MORUTheme.colors.darkGray,
                modifier = Modifier.width(24.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.width(24.dp))

        if (isEditMode) {
            // 수정 모드일 때: BasicTextField를 보여줌
            BasicTextField(
                value = step.name,
                onValueChange = onNameChange, // 타이핑할 때마다 onNameChange 콜백 호출
                modifier = Modifier.weight(1f),
                textStyle = MORUTheme.typography.body_SB_14.copy(color = MORUTheme.colors.black),
                singleLine = true // 한 줄 입력 필드로 설정
            )
        } else {
            // 보기 모드일 때: 기존처럼 Text를 보여줌
            Text(
                text = step.name,
                style = MORUTheme.typography.body_SB_14,
                color = MORUTheme.colors.black,
                modifier = Modifier.weight(1f),
            )
        }

        /*Text(
            text = step.name,
            style = MORUTheme.typography.body_SB_14,
            color = MORUTheme.colors.black,
            modifier = Modifier.weight(1f),
        )*/

        Text(
            text = step.duration,
            style = MORUTheme.typography.body_SB_14,
            color = MORUTheme.colors.darkGray,
        )

        if (isEditMode) {
            Spacer(Modifier.width(16.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_delete_gray),
                contentDescription = "Delete Step",
                modifier = Modifier
                    .size(14.dp)
                    .clickable(onClick = onDeleteClick),
                tint = MORUTheme.colors.mediumGray
            )
        }
    }
}

// ------------------- 주요 변경 부분 끝 -------------------


@Composable
fun RoutineItemCard(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    title: String,
    isEditMode: Boolean,
    onDelete: () -> Unit,
    description: String,
    category: String,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteCompleteDialog by remember { mutableStateOf(false) }
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
                    color = Color.LightGray,
                    style = MORUTheme.typography.desc_M_14
                )
            }
        )
        LaunchedEffect(Unit) {
            delay(1500)
            showDeleteCompleteDialog = false
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
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

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Row(
                    modifier = Modifier.clickable { isUserChecked = !isUserChecked },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
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

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((74.dp))
                    .background(
                        color = MORUTheme.colors.veryLightGray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(10.dp)
            ) {
                if (isEditMode) {
                    BasicTextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MORUTheme.typography.time_R_14.copy(color = Color.Black)
                    )
                } else {
                    Text(
                        text = if (description.isNotBlank()) description else "설명을 입력해주세요.",
                        modifier = Modifier.fillMaxWidth(),
                        style = MORUTheme.typography.time_R_14.copy(
                            color = if (description.isNotBlank()) Color.Black else Color.Gray
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


@Composable
private fun RoutineInfo(
    tags: List<String>,
    isEditMode: Boolean,
    onAddTag: () -> Unit,
    onDeleteTag: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 17.dp, end = 17.dp, top = 25.dp, bottom = 30.dp),
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(tags) { tag ->
                MoruChip(
                    text = "#$tag",
                    isSelected = true,
                    onClick = {
                        if (isEditMode) {
                            onDeleteTag(tag)
                        }
                    },
                    selectedBackgroundColor = Color.Black,
                    selectedContentColor = MORUTheme.colors.limeGreen,
                    unselectedBackgroundColor = Color.White,
                    unselectedContentColor = Color.Black,
                    endIconContent = if (isEditMode) {
                        {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Tag",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    } else null
                )
            }

            if (isEditMode) {
                item {
                    IconButton(
                        onClick = onAddTag,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(
                                    color = MORUTheme.colors.lightGray,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "태그 추가",
                                tint = Color.Black,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}


@Composable
private fun UsedAppsSection(
    apps: List<AppInfo>,
    isEditMode: Boolean,
    onAddApp: () -> Unit,
    onDeleteApp: (AppInfo) -> Unit
) {
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text("사용 앱", style = MORUTheme.typography.title_B_20, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(apps) { app ->
                Box {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = app.iconUrl,
                            contentDescription = app.name,
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    MORUTheme.colors.veryLightGray,
                                    shape = RoundedCornerShape(size = 6.dp)
                                )
                                .padding(8.dp),
                            placeholder = painterResource(id = R.drawable.ic_reset),
                            error = painterResource(id = R.drawable.ic_info)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(app.name, style = MORUTheme.typography.time_R_12)
                    }
                    if (isEditMode) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Delete App",
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopStart)
                                .clip(CircleShape)
                                .background(MORUTheme.colors.lightGray)
                                .clickable { onDeleteApp(app) },
                            tint = MORUTheme.colors.darkGray
                        )
                    }
                }
            }

            if (isEditMode) {
                item {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MORUTheme.colors.veryLightGray,
                                shape = RoundedCornerShape(size = 6.dp)
                            )
                            .clickable(onClick = onAddApp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add App",
                            tint = MORUTheme.colors.darkGray
                        )
                    }
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