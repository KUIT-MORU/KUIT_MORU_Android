package com.konkuk.moru.presentation.myroutines.viewmodel

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.mapper.toIso8601
import com.konkuk.moru.data.mapper.toMyDetailUi
import com.konkuk.moru.data.model.MyRoutineDetailUi
import com.konkuk.moru.data.model.MyRoutineDetailUiState
import com.konkuk.moru.data.model.RoutineStep
import com.konkuk.moru.data.model.UsedAppInRoutine
import com.konkuk.moru.data.model.placeholderIcon
import com.konkuk.moru.domain.repository.MyRoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.collections.forEach

@HiltViewModel
class MyRoutineDetailViewModel @Inject constructor(
    private val repo: MyRoutineRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private val pendingTags = mutableSetOf<String>()

    private val _uiState = MutableStateFlow(MyRoutineDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _availableApps = MutableStateFlow<List<UsedAppInRoutine>>(emptyList())
    val availableApps = _availableApps.asStateFlow()

    private val _deleteCompleted = MutableSharedFlow<Boolean>()
    val deleteCompleted = _deleteCompleted.asSharedFlow()

    private val _localImageUri = MutableStateFlow<Uri?>(null)
    val localImageUri = _localImageUri.asStateFlow()

    // 상세 원본 스냅샷(취소/복원용)
    private var originalRoutine: MyRoutineDetailUi? = null

    // name -> id (서버 태그 id 캐싱, 필요 시 사용)
    private val tagNameToId = mutableMapOf<String, String>()

    fun updateLocalImage(uri: Uri?) {
        _localImageUri.value = uri
    }

    fun updateDescription(newDescription: String) {
        _uiState.update { st -> st.copy(routine = st.routine?.copy(description = newDescription)) }
    }

    fun updateCategory(newCategory: String) {
        _uiState.update { st -> st.copy(routine = st.routine?.copy(category = newCategory)) }
    }

    /** 특정 routineId의 '내 루틴' 상세를 서버에서 불러옴 */
    fun loadRoutine(routineId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val ui = runCatching {
                repo.getRoutineDetailRaw(routineId).toMyDetailUi()
            }.getOrElse { e ->
                android.util.Log.e("MyRoutineDetailVM", "detail failed", e)
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            // ⬇️ [중요] 스냅샷은 '서버 원본' 그대로 저장
            originalRoutine = ui

            // 화면에는 임시 태그까지 합쳐서 보여줌
            val mergedTags = (ui.tags + pendingTags).distinct()
            _uiState.update { it.copy(routine = ui.copy(tags = mergedTags), isLoading = false) }

            runCatching {
                val serverTags = repo.getRoutineTags(routineId)
                tagNameToId.clear()
                serverTags.forEach { tagNameToId[it.name] = it.id.toString() }
            }.onFailure { e ->
                android.util.Log.w("MyRoutineDetailVM", "getRoutineTags failed (non-fatal)", e)
            }
        }
    }

    fun setEditMode(isEdit: Boolean) {
        _uiState.update { it.copy(isEditMode = isEdit) }
    }

    /** 편집 취소: 원본 복원 + 임시 이미지 제거 */
    fun restoreRoutine() {
        _uiState.update { it.copy(routine = originalRoutine) }
        _localImageUri.value = null
    }

    fun cancelEdits() {
        // 1) 서버 스냅샷 그대로 복원 (임시 태그 섞지 않음)
        originalRoutine?.let { snap ->
            _uiState.update { it.copy(routine = snap) }
        }

        // 2) 임시 상태 정리
        pendingTags.clear()      // 임시로 추가해 둔 태그 버퍼 비움
        _localImageUri.value = null

        // 3) 편집 모드 종료
        setEditMode(false)
    }

    /** 루틴 삭제 (안전 삭제) */
    fun deleteRoutine(routineId: String) {
        viewModelScope.launch {
            val ok = repo.deleteRoutineSafe(routineId)
            _deleteCompleted.emit(ok)
        }
    }

    /** (로컬 상태만) 설명/카테고리 변경 */
    fun updateRoutine(routineId: String, newDescription: String, newCategory: String) {
        _uiState.update { st ->
            st.copy(
                routine = st.routine?.copy(
                    description = newDescription,
                    category = newCategory
                )
            )
        }
    }

    /** 저장(PATCH) */
    fun saveChanges() {
        viewModelScope.launch {
            val current = uiState.value.routine ?: return@launch

            // (선택) 로컬 이미지 업로드
            val pending = _localImageUri.value
            val uploadedUrl: String? =
                if (pending != null) runCatching { uploadLocalImage(pending) }.getOrNull() else null

            // 스텝 → (name, order, iso8601)
            val stepsTriple = current.steps.mapIndexed { idx, step ->
                Triple(step.name, idx + 1, step.duration.toIso8601())
            }

            val selectedApps = current.usedApps.map { it.packageName }
            val isSimple = current.category == "간편"

            runCatching {
                repo.patchRoutine(
                    routineId = current.routineId,
                    title = current.title,
                    imageUrl = uploadedUrl ?: current.imageUrl,
                    tagNames = current.tags,      // 서버가 태그 "이름" 배열도 허용
                    description = current.description,
                    steps = stepsTriple,
                    selectedApps = selectedApps,
                    isSimple = isSimple,
                    isUserVisible = null          // 필요 시 UI 스위치 연결
                )
            }.onSuccess {
                // 성공 시 상세 재조회 & 태그 id 캐시 갱신
                val refreshed = repo.getRoutineDetailRaw(current.routineId).toMyDetailUi()
                val serverTags = repo.getRoutineTags(current.routineId)
                tagNameToId.clear()
                // ✅ id가 Int여도 안전하게 문자열로 보관
                serverTags.forEach { tagNameToId[it.name] = it.id.toString() }

                pendingTags.clear() // [추가] 서버 반영 끝났으니 비움

                originalRoutine = refreshed.copy()
                _uiState.update { it.copy(routine = refreshed) }
                _localImageUri.value = null
                setEditMode(false)
            }
        }
    }

    // === 내부: 이미지 업로드 ===
    private suspend fun uploadLocalImage(uri: Uri): String = withContext(Dispatchers.IO) {
        val resolver: ContentResolver = appContext.contentResolver
        val mime = resolver.getType(uri) ?: "image/jpeg"
        val fileName = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { if (it.moveToFirst()) it.getString(0) else null }
            ?: "upload_${System.currentTimeMillis()}.jpg"

        val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)

        val uploaded = repo.uploadImageAndGetUrl(fileName, bytes, mime)
        android.util.Log.d("MyRoutineDetailVM", "uploaded imageUrl=$uploaded") // [추가]
        uploaded
    }

    // ===== 태그 (UI 즉시 반영: 이름 리스트) =====
    fun deleteTag(tag: String) {
        pendingTags.remove(tag) // [추가]
        _uiState.update { st ->
            val updated = st.routine?.tags?.toMutableList()?.apply { remove(tag) } ?: emptyList()
            st.copy(routine = st.routine?.copy(tags = updated))
        }
    }


    fun addTags(tags: List<String>) {
        if (tags.isEmpty()) return
        val clean = tags.map { it.trim().removePrefix("#") }.filter { it.isNotBlank() }
        if (clean.isEmpty()) return

        pendingTags.addAll(clean) // [추가]

        _uiState.update { st ->
            val merged = (st.routine?.tags.orEmpty() + clean).distinct()
            st.copy(routine = st.routine?.copy(tags = merged))
        }
    }

    // ===== 스텝 편집 =====
    fun deleteStep(index: Int) {
        _uiState.update { st ->
            val list = st.routine?.steps?.toMutableList() ?: return@update st
            if (index !in list.indices) return@update st
            list.removeAt(index)
            st.copy(routine = st.routine?.copy(steps = list))
        }
    }

    fun addStep() {
        _uiState.update { st ->
            val new = RoutineStep(name = "활동명 입력", duration = "00:30:00")
            st.copy(routine = st.routine?.copy(steps = st.routine.steps + new))
        }
    }

    fun updateStepName(index: Int, newName: String) {
        _uiState.update { st ->
            val list = st.routine?.steps?.toMutableList() ?: return@update st
            if (index !in list.indices) return@update st
            list[index] = list[index].copy(name = newName)
            st.copy(routine = st.routine?.copy(steps = list))
        }
    }

    fun updateStepDuration(index: Int, newDuration: String) {
        _uiState.update { st ->
            val list = st.routine?.steps?.toMutableList() ?: return@update st
            if (index !in list.indices) return@update st
            list[index] = list[index].copy(duration = newDuration)
            st.copy(routine = st.routine?.copy(steps = list))
        }
    }

    fun onDragStart(index: Int) {
        _uiState.update { it.copy(draggedStepIndex = index) }
    }

    fun onDrag(offset: Float) {
        _uiState.update { it.copy(draggedStepVerticalOffset = it.draggedStepVerticalOffset + offset) }
    }

    fun finalizeStepReorder(from: Int, to: Int) {
        _uiState.update { st ->
            val list = st.routine?.steps?.toMutableList() ?: return@update st
            val item = list.removeAt(from); list.add(to, item)
            st.copy(
                routine = st.routine?.copy(steps = list),
                draggedStepIndex = null,
                draggedStepVerticalOffset = 0f
            )
        }
    }

    fun cancelDrag() {
        _uiState.update { it.copy(draggedStepIndex = null, draggedStepVerticalOffset = 0f) }
    }

    // ===== 사용 앱 =====
    fun deleteApp(appToDelete: UsedAppInRoutine) {
        _uiState.update { st ->
            val updated =
                st.routine?.usedApps?.filter { it.packageName != appToDelete.packageName }.orEmpty()
            st.copy(routine = st.routine?.copy(usedApps = updated))
        }
    }

    fun addApp(app: UsedAppInRoutine) {
        _uiState.update { st ->
            val cur = st.routine?.usedApps.orEmpty()
            if (cur.any { it.packageName == app.packageName } || cur.size >= 4) return@update st
            st.copy(routine = st.routine?.copy(usedApps = cur + app))
        }
    }

    // (샘플) 매개변수 없는 추가 - 기존 UI 호출 호환용
    fun addApp() {
        _uiState.update { st ->
            val cur = st.routine?.usedApps.orEmpty()
            if (cur.size >= 4) return@update st
            val newApp = UsedAppInRoutine(
                appName = "새로운 앱",
                appIcon = placeholderIcon(),
                packageName = "com.example.newapp"
            )
            st.copy(routine = st.routine?.copy(usedApps = cur + newApp))
        }
    }

    // 설치 앱 로드 → availableApps에 채움
    @SuppressLint("QueryPermissionsNeeded")
    fun loadInstalledApps(context: Context) {
        viewModelScope.launch {
            runCatching {
                val pm = context.packageManager
                pm.getInstalledApplications(PackageManager.GET_META_DATA)
                    .mapNotNull { appInfo ->
                        val label =
                            pm.getApplicationLabel(appInfo)?.toString() ?: return@mapNotNull null
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

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        return when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            is AdaptiveIconDrawable -> {
                val bmp = createBitmap(
                    drawable.intrinsicWidth.coerceAtLeast(1),
                    drawable.intrinsicHeight.coerceAtLeast(1)
                )
                val canvas = Canvas(bmp) // android.graphics.Canvas
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bmp
            }

            else -> {
                val bmp = createBitmap(
                    drawable.intrinsicWidth.coerceAtLeast(1),
                    drawable.intrinsicHeight.coerceAtLeast(1)
                )
                val canvas = Canvas(bmp) // android.graphics.Canvas
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bmp
            }
        }
    }
}