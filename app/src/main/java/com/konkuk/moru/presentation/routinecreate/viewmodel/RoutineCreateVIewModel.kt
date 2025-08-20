package com.konkuk.moru.presentation.routinecreate.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.model.UsedAppInRoutine
import androidx.core.graphics.createBitmap
import com.konkuk.moru.data.dto.request.CreateRoutineRequest
import com.konkuk.moru.data.dto.request.StepDto
import com.konkuk.moru.data.model.StepUi
import com.konkuk.moru.domain.repository.CRImageRepository
import com.konkuk.moru.domain.repository.CreateRoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

@HiltViewModel
class RoutineCreateViewModel @Inject constructor(
    private val createRoutineRepository: CreateRoutineRepository,
    private val crImageRepository: CRImageRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private companion object {
        const val TITLE_MAX = 10      // [추가]
        const val TAG_MAX_COUNT = 3   // [추가]
        const val TAG_LABEL_MAX = 5   // [추가]
        const val DESC_MAX = 32       // [추가]
        const val STEP_MIN = 3        // [추가]
        const val STEP_MAX = 6        // [추가]
        const val APP_MAX = 4         // [추가]
    }

    val imageUri = mutableStateOf<Uri?>(null)
    val showUser = mutableStateOf(true)
    val isFocusingRoutine = mutableStateOf(true) // false면 간편 루틴
    val routineTitle = mutableStateOf("")
    val routineDescription = mutableStateOf("")
    val tagList = mutableStateListOf<String>()
    val stepList = mutableStateListOf(StepUi())
    val editingStepIndex = mutableStateOf<Int?>(null)
    val appList = mutableStateListOf<UsedAppInRoutine>()
    val selectedAppList = mutableStateListOf<UsedAppInRoutine>()

    val isSubmitting = mutableStateOf(false)
    val submitError = mutableStateOf<String?>(null)
    val createdRoutineId = mutableStateOf<String?>(null)

    fun updateTitle(title: String) {
        routineTitle.value = title.take(TITLE_MAX) // [변경] 최대 10자
    }

    fun toggleShowUser() {
        showUser.value = !showUser.value
    }

    fun toggleFocusingRoutine() {
        isFocusingRoutine.value = !isFocusingRoutine.value
        // 간편 루틴으로 바꿔도 UI 목록은 보존(페이로드에서는 생략)
        // 필요 시 여기서 selectedAppList.clear() 등의 정책 적용 가능
    }

    fun updateDescription(desc: String) {
        routineDescription.value = desc.take(DESC_MAX) // [변경] 최대 32자
    }

    fun addTag(tag: String) {
        val t = tag.removePrefix("#").trim()
        if (t.isNotBlank() && t.length <= TAG_LABEL_MAX && tagList.size < TAG_MAX_COUNT && !tagList.contains(t)) {
            tagList.add(t) // [변경] 길이/개수 제한
        }
    }

    fun addTags(tags: List<String>) {
        val normalized = tags.map { it.removePrefix("#").trim() }
            .filter { it.isNotBlank() && it.length <= TAG_LABEL_MAX } // [변경]

        normalized.forEach { t ->
            if (!tagList.contains(t) && tagList.size < TAG_MAX_COUNT) {
                tagList.add(t) // [변경]
            }
        }
    }

    fun removeTag(tag: String) {
        tagList.remove(tag)
    }

    fun setEditingStep(index: Int) {
        editingStepIndex.value = index
    }

    fun getEditingStepTime(): String? =
        editingStepIndex.value?.let { idx -> stepList.getOrNull(idx)?.time }

    fun addStep() {
        if (stepList.size < STEP_MAX) { // [변경] 최대 6개
            stepList.add(StepUi())
        }
    }

    fun removeStep(index: Int) {
        if (index in stepList.indices) stepList.removeAt(index)
    }

    fun updateStepTitle(index: Int, newTitle: String) {
        if (index in stepList.indices) {
            stepList[index] = stepList[index].copy(title = newTitle)
        }
    }

    @SuppressLint("DefaultLocale")
    fun confirmTime(hour: Int, minute: Int, second: Int) {
        val i = editingStepIndex.value ?: return
        if (i !in stepList.indices) return
        val formatted = String.format("%02d:%02d:%02d", hour, minute, second)
        stepList[i] = stepList[i].copy(time = formatted)
    }

    // ---- 시간 변환 유틸 ----
    private fun String.toIsoDuration(): String {
        val p = split(":")
        val h = p.getOrNull(0)?.toIntOrNull() ?: 0
        val m = p.getOrNull(1)?.toIntOrNull() ?: 0
        val s = p.getOrNull(2)?.toIntOrNull() ?: 0
        return buildString {
            append("PT")
            if (h > 0) append("${h}H")
            if (m > 0) append("${m}M")
            if (s > 0 || (h == 0 && m == 0)) append("${s}S")
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun loadInstalledApps(context: Context) {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        appList.clear()
        apps.forEach { appInfo ->
            try {
                val label = pm.getApplicationLabel(appInfo).toString()
                val drawable = pm.getApplicationIcon(appInfo)
                val bitmap = drawableToBitmap(drawable)
                val imageBitmap = bitmap.asImageBitmap()
                val pkg = appInfo.packageName
                if (appList.none { it.packageName == pkg }) {
                    appList.add(
                        UsedAppInRoutine(
                            appName = label,
                            appIcon = imageBitmap,
                            packageName = pkg
                        )
                    )
                }
            } catch (_: Exception) { }
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
                val canvas = Canvas(bmp)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bmp
            }
            else -> {
                val bmp = createBitmap(
                    drawable.intrinsicWidth.coerceAtLeast(1),
                    drawable.intrinsicHeight.coerceAtLeast(1)
                )
                val canvas = Canvas(bmp)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bmp
            }
        }
    }

    fun addAppToSelected(app: UsedAppInRoutine) {
        if (selectedAppList.none { it.packageName == app.packageName } && selectedAppList.size < APP_MAX) { // [변경]
            selectedAppList.add(app)
        }
    }

    fun removeAppFromSelected(app: UsedAppInRoutine) {
        selectedAppList.removeAll { it.packageName == app.packageName }
    }

    // ---- 제출 검증 ----
    data class SubmitValidation(val isValid: Boolean, val reason: String? = null)

    fun validateForSubmit(): SubmitValidation {
        if (routineTitle.value.isBlank()) return SubmitValidation(false, "title blank")
        if (routineTitle.value.length > TITLE_MAX) return SubmitValidation(false, "title too long")
        if (tagList.isEmpty() || tagList.size > TAG_MAX_COUNT) return SubmitValidation(false, "tag count invalid")
        if (tagList.any { it.length > TAG_LABEL_MAX }) return SubmitValidation(false, "tag label too long")
        if (routineDescription.value.length > DESC_MAX) return SubmitValidation(false, "desc too long")

        return if (isFocusingRoutine.value) {
            if (stepList.size < STEP_MIN || stepList.size > STEP_MAX)
                return SubmitValidation(false, "step count invalid")
            if (stepList.any { it.title.isBlank() })
                return SubmitValidation(false, "step title blank exists")
            if (stepList.any { it.time.isBlank() })
                return SubmitValidation(false, "step time blank exists")
            if (selectedAppList.size > APP_MAX)
                return SubmitValidation(false, "too many apps")
            SubmitValidation(true)
        } else {
            SubmitValidation(true) // 간편 루틴: steps/apps 생략
        }
    }

    // ---- 서버 전송 DTO 빌드 ----
    fun buildCreateRoutineRequest(imageKey: String?): CreateRoutineRequest {
        val simple = !isFocusingRoutine.value

        val stepsPayload: List<StepDto>? =
            if (simple) {
                null // [변경] 간편 루틴은 steps 미포함
            } else {
                stepList.mapIndexed { idx, step ->
                    val timeIso = step.time.toIsoDuration()
                    Log.d("createroutine", "[build] step ${idx + 1} title='${step.title}' time='${step.time}' -> $timeIso simple=$simple")
                    StepDto(
                        name = step.title,
                        stepOrder = idx + 1,
                        estimatedTime = timeIso
                    )
                }
            }

        val appsPayload: List<String>? =
            if (simple) {
                null // [변경] 간편 루틴은 apps 미포함
            } else {
                selectedAppList.map { it.packageName }
            }

        return CreateRoutineRequest(
            title = routineTitle.value,
            imageKey = imageKey,
            tags = tagList.toList(),
            description = routineDescription.value,
            steps = stepsPayload,
            selectedApps = appsPayload,
            isSimple = simple,
            isUserVisible = showUser.value
        )
    }

    private val TAG = "createroutine"

    fun createRoutine(imageKey: String?) {
        val trace = UUID.randomUUID().toString().take(8)
        viewModelScope.launch {
            submitError.value = null
            isSubmitting.value = true

            // 사전 검증 가드
            val validation = validateForSubmit()
            if (!validation.isValid) {
                isSubmitting.value = false
                submitError.value = "제출 불가: ${validation.reason}"
                Log.d(TAG, "[$trace] blocked: ${validation.reason}")
                return@launch
            }

            // 이미지 업로드(있으면)
            val finalImageKey = try {
                val localUri = imageUri.value
                if (localUri != null) {
                    Log.d(TAG, "[$trace] try upload image uri=$localUri")
                    val file = copyUriToCache(localUri)
                    val key = crImageRepository.uploadImage(file).getOrThrow()
                    Log.d(TAG, "[$trace] image uploaded. key=$key name=${file.name} size=${file.length()}")
                    key
                } else {
                    imageKey
                }
            } catch (e: Exception) {
                Log.d(TAG, "[$trace] image upload failed: ${e.message}")
                Log.d(TAG, Log.getStackTraceString(e))
                null
            }

            val req = buildCreateRoutineRequest(finalImageKey)
            Log.d(TAG, "[$trace] vm request(final)=$req")

            val result = createRoutineRepository.createRoutine(req)
            isSubmitting.value = false
            result
                .onSuccess { resp ->
                    Log.d(TAG, "[$trace] vm success id=${resp.id} createdAt=${resp.createdAt}")
                    createdRoutineId.value = resp.id
                }
                .onFailure { e ->
                    Log.d(TAG, "[$trace] vm failure ${e::class.java.simpleName}: ${e.message}")
                    Log.d(TAG, Log.getStackTraceString(e))
                    submitError.value = e.message ?: "루틴 생성에 실패했어요."
                }
        }
    }

    // content:// URI → 캐시 파일 복사
    private fun copyUriToCache(uri: Uri): File {
        val ctx = appContext
        val file = File.createTempFile("moru_upload_", ".jpg", ctx.cacheDir)
        ctx.contentResolver.openInputStream(uri).use { inS ->
            file.outputStream().use { outS -> inS!!.copyTo(outS) }
        }
        return file
    }
}