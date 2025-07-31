import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.konkuk.moru.core.component.button.MoruButton
import com.konkuk.moru.core.component.routinedetail.MyRoutineTag
import com.konkuk.moru.data.model.RoutineStepActions
import com.konkuk.moru.presentation.myroutines.component.RoutineItemCard
import com.konkuk.moru.presentation.myroutines.component.UsedAppsSection
import com.konkuk.moru.core.component.routinedetail.routineStepEditableList
import com.konkuk.moru.presentation.myroutines.viewmodel.MyRoutineDetailViewModel
import com.konkuk.moru.ui.theme.MORUTheme


@Composable
fun MyRoutineDetailContent(
    viewModel: MyRoutineDetailViewModel,
) {
    val listState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val routine = uiState.routine ?: return
    val isEditMode = uiState.isEditMode

    // LazyColumn에서 실제 스텝 리스트가 시작되기 전의 아이템 개수 (Card, Tag, "STEP" 헤더)
    val headerItemCount = 3

    val routineStepActions = RoutineStepActions(
        onDragStart = viewModel::onDragStart,
        onDrag = viewModel::onDrag,
        onReorderComplete = viewModel::finalizeStepReorder, // finalizeStepReorder 함수로 변경
        onReorderCancel = viewModel::cancelDrag,           // cancelDrag 함수로 변경
        onDeleteStep = viewModel::deleteStep,
        onStepNameChange = viewModel::updateStepName,
        onAddStep = viewModel::addStep
    )


    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .background(Color.White)
                .weight(1f)
        ) {
            item {
                RoutineItemCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    imageUrl = routine.imageUrl,
                    title = routine.title,
                    isEditMode = isEditMode,
                    onDelete = { viewModel.deleteRoutine(routine.routineId) },
                    description = routine.description,
                    category = routine.category,
                    onDescriptionChange = viewModel::updateDescription,
                    onCategoryChange = viewModel::updateCategory,
                )
            }

            item {
                MyRoutineTag(
                    tags = routine.tags,
                    isEditMode = isEditMode,
                    onAddTag = { viewModel.addTag() }, // ViewModel 함수 직접 호출
                    onDeleteTag = viewModel::deleteTag
                )
            }

            routineStepEditableList(
                steps = routine.steps,
                isEditMode = isEditMode,
                listState = listState,
                draggedStepIndex = uiState.draggedStepIndex,
                draggedStepVerticalOffset = uiState.draggedStepVerticalOffset,
                headerItemCount = headerItemCount,
                actions = routineStepActions
            )

            if (routine.usedApps.isNotEmpty() || isEditMode) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    UsedAppsSection(
                        apps = routine.usedApps,
                        isEditMode = isEditMode,
                        onAddApp = viewModel::addApp,
                        onDeleteApp = viewModel::deleteApp
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }

        MoruButton(
            text = if (isEditMode) "완료하기" else "수정하기",
            onClick = {
                if (isEditMode) {
                    viewModel.saveChanges()
                }
                // ✨ ViewModel에 모드 변경을 요청
                viewModel.setEditMode(!isEditMode)
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

/**
 * '보기 모드'일 때의 UI를 미리 보여줍니다.
 * 사용자는 루틴 정보를 조회만 할 수 있으며, 수정 관련 UI(삭제 버튼 등)는 보이지 않습니다.
 */
@Preview(showBackground = true, name = "상세 화면 - 보기 모드")
@Composable
private fun MyRoutineDetailContentPreview_ViewMode() {
    val viewModel: MyRoutineDetailViewModel = viewModel()
    // '나'의 첫 번째 루틴(ID: 501)을 불러와서 상태를 설정합니다.
    viewModel.loadRoutine(501)

    MORUTheme {
        MyRoutineDetailContent(
            viewModel = viewModel,
        )
    }
}

/**
 * '수정 모드'일 때의 UI를 미리 보여줍니다.
 * 정보 수정, 태그/스텝/사용 앱 추가 및 삭제 등 편집과 관련된 UI가 활성화됩니다.
 */
@Preview(showBackground = true, name = "상세 화면 - 수정 모드")
@Composable
private fun MyRoutineDetailContentPreview_EditMode() {
    val viewModel: MyRoutineDetailViewModel = viewModel()
    // '나'의 첫 번째 루틴(ID: 501)을 불러와서 상태를 설정합니다.
    viewModel.loadRoutine(501)

    MORUTheme {
        MyRoutineDetailContent(
            viewModel = viewModel,
        )
    }
}
