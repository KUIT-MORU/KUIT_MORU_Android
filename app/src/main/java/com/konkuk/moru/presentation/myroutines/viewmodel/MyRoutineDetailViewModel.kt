package com.konkuk.moru.presentation.myroutines.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.model.AppInfo
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.data.model.MyRoutineDetailUiState
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.model.RoutineStep
import com.konkuk.moru.data.model.UsedAppInRoutine
import com.konkuk.moru.data.model.placeholderIcon
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


class MyRoutineDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyRoutineDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _availableApps = MutableStateFlow<List<UsedAppInRoutine>>(emptyList())  // [추가]
    val availableApps = _availableApps.asStateFlow()

    // 삭제 완료 후 이전 화면으로 돌아가기 위한 신호(Event)
    private val _deleteCompleted = MutableSharedFlow<Boolean>()
    val deleteCompleted = _deleteCompleted.asSharedFlow()

    private var originalRoutine: Routine? = null

    private val _localImageUri = MutableStateFlow<Uri?>(null)
    val localImageUri = _localImageUri.asStateFlow()

    fun updateLocalImage(uri: Uri?) { _localImageUri.value = uri }

    /**
     * 특정 routineId를 가진 '내 루틴'을 불러옵니다.
     */
    fun loadRoutine(routineId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // '내 루틴'만 찾도록 authorId 조건을 추가합니다.
            val routine = DummyData.feedRoutines.find {
                it.routineId == routineId && it.authorId == DummyData.MY_USER_ID
            }
            originalRoutine = routine?.copy()
            _uiState.update { it.copy(routine = routine, isLoading = false) }
        }
    }


    fun setEditMode(isEdit: Boolean) {
        _uiState.update { it.copy(isEditMode = isEdit) }
    }

    /**
     * 특정 routineId를 가진 루틴을 DummyData에서 삭제합니다.
     */
    fun restoreRoutine() {
        _uiState.update { it.copy(routine = originalRoutine) }
        _localImageUri.value = null              // [변경] 임시 이미지 버리기 (원복)
    }

    fun cancelEdits() {
        restoreRoutine()                         // 원본으로 되돌림 + 임시 이미지 초기화
        setEditMode(false)                       // 편집모드 종료
    }

    fun deleteRoutine(routineId: String) {
        viewModelScope.launch {
            DummyData.feedRoutines.removeAll { it.routineId == routineId }
            _deleteCompleted.emit(true) // 삭제 완료 신호를 보냅니다.
        }
    }

    /**
     * 루틴의 설명과 카테고리를 업데이트합니다. -> 수정예정
     */
    fun updateRoutine(routineId: String, newDescription: String, newCategory: String) {
        val index = DummyData.feedRoutines.indexOfFirst { it.routineId == routineId }
        if (index != -1) {
            val originalRoutine = DummyData.feedRoutines[index]
            // description과 category를 업데이트합니다.
            DummyData.feedRoutines[index] = originalRoutine.copy(
                description = newDescription,
                category = newCategory
            )
        }
    }

    fun saveChanges() {
        viewModelScope.launch {
            val current = uiState.value.routine ?: return@launch

            // [추가] 편집 중 이미지가 있으면 서버 업로드 → routine 반영
            val pending = _localImageUri.value
            val withImageApplied = if (pending != null) {
                val uploadedUrl = uploadImageToServer(pending) // [추가] 업로드
                current.copy(imageUrl = uploadedUrl)          // 필요 시 imageKey 필드에 넣어도 됨
            } else {
                current
            }

            val index = DummyData.feedRoutines.indexOfFirst { it.routineId == withImageApplied.routineId }
            if (index != -1) {
                DummyData.feedRoutines[index] = withImageApplied
            }
            // [추가] 화면 상태/원본 스냅샷 업데이트 & 임시 이미지 초기화
            _uiState.update { it.copy(routine = withImageApplied) }
            originalRoutine = withImageApplied.copy()
            _localImageUri.value = null
        }
    }

    // [추가] 실제 서버 연동 자리 (샘플 구현)
    private suspend fun uploadImageToServer(uri: Uri): String = withContext(Dispatchers.IO) {
        // TODO: 실제 업로드 로직으로 교체 (Retrofit/Multipart 등)
        delay(300) // 업로드 대기 시뮬레이션
        // 서버가 반환한 이미지 접근 URL(or imageKey)을 반환한다고 가정
        "https://cdn.moru.app/uploads/${System.currentTimeMillis()}.jpg"
    }

    fun updateDescription(newDescription: String) {
        _uiState.update { state ->
            state.copy(routine = state.routine?.copy(description = newDescription))
        }
    }

    fun updateCategory(newCategory: String) {
        _uiState.update { state ->
            state.copy(routine = state.routine?.copy(category = newCategory))
        }
    }

    fun deleteTag(tag: String) {
        _uiState.update { state ->
            val updatedTags = state.routine?.tags?.toMutableList()?.apply { remove(tag) }
            state.copy(routine = state.routine?.copy(tags = updatedTags ?: emptyList()))
        }
    }

    // TODO: 실제 앱에서는 다이얼로그 등을 통해 태그 이름을 입력받아야 합니다.
    fun addTag(tag: String = "새 태그") {
        if (tag.isBlank()) return
        _uiState.update { state ->
            val updatedTags = state.routine?.tags?.plus(tag)
            state.copy(routine = state.routine?.copy(tags = updatedTags ?: listOf(tag)))
        }
    }

    fun deleteStep(index: Int) {
        _uiState.update { state ->
            val currentSteps = state.routine?.steps?.toMutableList()
            currentSteps?.removeAt(index)
            state.copy(routine = state.routine?.copy(steps = currentSteps ?: emptyList()))
        }
    }

    // TODO: 실제 앱에서는 스텝 추가 화면으로 이동하거나 다이얼로그를 띄워야 합니다.
    fun addStep() {
        _uiState.update { state ->
            val newStep = RoutineStep(name = "활동명 입력", duration = "00:30")
            val updatedSteps = state.routine?.steps?.plus(newStep)
            state.copy(routine = state.routine?.copy(steps = updatedSteps ?: listOf(newStep)))
        }
    }


    fun updateStepName(index: Int, newName: String) {
        _uiState.update { state ->
            state.routine?.let { routine ->
                val currentSteps = routine.steps.toMutableList()
                // 인덱스가 유효한 범위 내에 있는지 확인
                if (index in currentSteps.indices) {
                    // 해당 인덱스의 스텝을 새로운 이름으로 교체
                    currentSteps[index] = currentSteps[index].copy(name = newName)
                }
                // 업데이트된 스텝 리스트로 routine 상태를 갱신
                state.copy(routine = routine.copy(steps = currentSteps))
            } ?: state // routine이 null이면 기존 상태 반환
        }
    }


    fun onDragStart(index: Int) {
        _uiState.update { it.copy(draggedStepIndex = index) }
    }

    fun onDrag(offset: Float) {
        // 드래그 중인 아이템의 Y축 오프셋을 업데이트
        _uiState.update { it.copy(draggedStepVerticalOffset = it.draggedStepVerticalOffset + offset) }
    }


    // [수정] 설치 앱 로드: 기존 appList 참조 제거하고 StateFlow 채우기
    @SuppressLint("QueryPermissionsNeeded")
    fun loadInstalledApps(context: Context) {
        viewModelScope.launch {
            runCatching {
                val pm = context.packageManager
                pm.getInstalledApplications(PackageManager.GET_META_DATA)
                    .mapNotNull { appInfo ->
                        val label = pm.getApplicationLabel(appInfo)?.toString() ?: return@mapNotNull null
                        val drawable = pm.getApplicationIcon(appInfo.packageName)
                        val bitmap = drawableToBitmap(drawable)
                        val imageBitmap = bitmap.asImageBitmap()
                        UsedAppInRoutine(
                            appName = label,
                            appIcon = imageBitmap,
                            packageName = appInfo.packageName
                        )
                    }
                    .sortedBy { it.appName.lowercase() }
            }.onSuccess { list -> _availableApps.value = list }
                .onFailure { _availableApps.value = emptyList() }
        }
    }

    // [추가] 실제 선택앱 추가(중복/최대 4개 방지)
    fun addApp(app: UsedAppInRoutine) {
        _uiState.update { state ->
            val current = state.routine?.usedApps ?: emptyList()
            if (current.any { it.packageName == app.packageName } || current.size >= 4) {
                return@update state
            }
            val updated = current + app
            state.copy(routine = state.routine?.copy(usedApps = updated) ?: state.routine)
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        return when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            is AdaptiveIconDrawable -> {
                val bmp = createBitmap(
                    drawable.intrinsicWidth.coerceAtLeast(1),
                    drawable.intrinsicHeight.coerceAtLeast(1)
                )
                val canvas = Canvas(bmp) // [중요] android.graphics.Canvas 사용
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bmp
            }

            else -> {
                val bmp = createBitmap(
                    drawable.intrinsicWidth.coerceAtLeast(1),
                    drawable.intrinsicHeight.coerceAtLeast(1)
                )
                val canvas = Canvas(bmp) // [중요] android.graphics.Canvas 사용
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bmp
            }
        }
    }


    fun finalizeStepReorder(from: Int, to: Int) {
        _uiState.update { currentState ->
            val currentSteps = currentState.routine?.steps?.toMutableList()
                ?: return@update currentState

            // 1. 리스트 순서 변경
            val movedItem = currentSteps.removeAt(from)
            currentSteps.add(to, movedItem)

            // 2. 순서 변경된 리스트와 드래그 상태 초기화를 포함한 새 상태 반환
            currentState.copy(
                routine = currentState.routine.copy(steps = currentSteps),
                draggedStepIndex = null,
                draggedStepVerticalOffset = 0f
            )
        }
    }

    fun cancelDrag() {
        _uiState.update { currentState ->
            currentState.copy(
                draggedStepIndex = null,
                draggedStepVerticalOffset = 0f
            )
        }
    }

    fun deleteApp(appToDelete: UsedAppInRoutine) {
        _uiState.update { state ->
            val updatedApps = state.routine?.usedApps?.filter { it.appName != appToDelete.appName }
            state.copy(routine = state.routine?.copy(usedApps = updatedApps ?: emptyList()))
        }
    }


    fun addApp() {
        _uiState.update { state ->
            // 예시로 새 앱 추가
            val newApp = UsedAppInRoutine(
                appName = "새로운 앱",
                appIcon = placeholderIcon(), // 더미 아이콘 사용
                packageName = "com.example.newapp"
            )
            val updatedApps = state.routine?.usedApps?.plus(newApp)
            state.copy(routine = state.routine?.copy(usedApps = updatedApps ?: listOf(newApp)))
        }
    }
}