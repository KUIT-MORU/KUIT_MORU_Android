import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.konkuk.moru.core.component.button.MoruButton
import com.konkuk.moru.data.model.AppInfo
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.presentation.myroutines.component.LocalRoutineStepItem
import com.konkuk.moru.presentation.myroutines.component.MyRoutineTag
import com.konkuk.moru.presentation.myroutines.component.RoutineItemCard
import com.konkuk.moru.presentation.myroutines.component.UsedAppsSection
import com.konkuk.moru.presentation.myroutines.component.findNewIndex
import com.konkuk.moru.ui.theme.MORUTheme
import kotlinx.coroutines.Job


@Composable
fun MyRoutineDetailContent(
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
                MyRoutineTag(
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
                        .padding(bottom = 8.dp)
                        .zIndex(if (isDragging) 1f else 0f)
                        .graphicsLayer {
                            translationY = offset
                            shadowElevation = if (isDragging) 8f else 0f
                        }
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 1.dp, color = Color.Black.copy(alpha = 0.5f)
                    )

                    LocalRoutineStepItem(
                        stepNumber = index + 1,
                        step = item,
                        isEditMode = isEditMode,
                        onDeleteClick = { onDeleteStep(index) },
                        onNameChange = { newName -> onStepNameChange(index, newName) },
                        dragHandleModifier = dragHandleModifier
                    )

                    // 🎨 2. 아이템 아래쪽 구분선 (항상 표시)
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 1.dp, color = Color.Black.copy(alpha = 0.5f)
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

@Preview(showBackground = true, name = "상세 화면 - 보기 모드")
@Composable
private fun MyRoutineDetailContentPreview_ViewMode() {
    // DummyData에서 '나'의 루틴 중 하나를 가져와서 사용합니다.
    val sampleRoutine = DummyData.feedRoutines.find { it.authorId == DummyData.MY_USER_ID }

    if (sampleRoutine != null) {
        MORUTheme {
            MyRoutineDetailContent(
                routine = sampleRoutine,
                isEditMode = false, // 보기 모드
                onEditModeChange = {},
                onDelete = {},
                onSave = {},
                onDescriptionChange = {},
                onCategoryChange = {},
                onAddTag = {},
                onDeleteTag = {},
                onAddStep = {},
                onDeleteStep = {},
                onMoveStep = { _, _ -> },
                onAddApp = {},
                onDeleteApp = {},
                onStepNameChange = { _, _ -> }
            )
        }
    }
}

