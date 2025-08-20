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
import com.konkuk.moru.data.model.Step
import com.konkuk.moru.data.model.UsedAppInRoutine
import androidx.core.graphics.createBitmap
import com.konkuk.moru.data.dto.request.CreateRoutineRequest
import com.konkuk.moru.data.dto.request.StepDto
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

    val imageUri = mutableStateOf<Uri?>(null)
    val showUser = mutableStateOf(true)
    val isFocusingRoutine = mutableStateOf(true)
    val routineTitle = mutableStateOf("")
    val routineDescription = mutableStateOf("")
    val tagList = mutableStateListOf<String>()
    val stepList = mutableStateListOf(Step(title = "", time = ""))
    val editingStepId = mutableStateOf<String?>(null)
    val appList = mutableStateListOf<UsedAppInRoutine>()
    val selectedAppList = mutableStateListOf<UsedAppInRoutine>()

    // [추가] UI 상태
    val isSubmitting = mutableStateOf(false)
    val submitError = mutableStateOf<String?>(null)
    val createdRoutineId = mutableStateOf<String?>(null)

    fun updateTitle(title: String) {
        routineTitle.value = title
    }

    fun toggleShowUser() {
        showUser.value = !showUser.value
    }

    fun toggleFocusingRoutine() {
        isFocusingRoutine.value = !isFocusingRoutine.value
    }

    fun updateDescription(desc: String) {
        routineDescription.value = desc
    }

    fun addTag(tag: String) {
        tagList.add(tag) // TODO: 중복 체크 로직 추가 필요
    }

    fun addTags(tags: List<String>) {
        val normalized = tags.map { it.removePrefix("#").trim() }
            .filter { it.isNotBlank() }

        normalized.forEach { t ->
            if (!tagList.contains(t) && tagList.size < 3) {
                tagList.add(t)
            }
        }
    }

    fun removeTag(tag: String) {
        tagList.remove(tag)
    }

    fun updateStepTitle(stepId: String, newTitle: String) {
        val index = stepList.indexOfFirst { it.id == stepId }
        if (index != -1) {
            stepList[index] = stepList[index].copy(title = newTitle)
        }
    }

    fun removeStep(stepId: String) {
        val index = stepList.indexOfFirst { it.id == stepId }
        if (index != -1) {
            stepList.removeAt(index)
        }
    }

    fun addStep() {
        stepList.add(Step(title = "", time = ""))
    }

    fun setEditingStep(stepId: String) {
        editingStepId.value = stepId
    }

    fun getEditingStepTime(): String? {
        val id = editingStepId.value ?: return null
        return stepList.find { it.id == id }?.time
    }

    // HH:MM:SS → PT#H#M#S 변환
    private fun String.toIsoDuration(): String {
        val parts = split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val s = parts.getOrNull(2)?.toIntOrNull() ?: 0
        return buildString {
            append("PT")
            if (h > 0) append("${h}H")
            if (m > 0) append("${m}M")
            if (s > 0 || (h == 0 && m == 0)) append("${s}S")
        }
    }

    @SuppressLint("DefaultLocale")
    fun confirmTime(hour: Int, minute: Int, second: Int) {
        val formatted = String.format("%02d:%02d:%02d", hour, minute, second)
        val stepId = editingStepId.value ?: return
        val index = stepList.indexOfFirst { it.id == stepId }
        if (index != -1) {
            stepList[index] = stepList[index].copy(time = formatted)
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
            } catch (_: Exception) {
            }
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
        if (selectedAppList.none { it.packageName == app.packageName } && selectedAppList.size < 4) {
            selectedAppList.add(app)
        }
    }

    fun removeAppFromSelected(app: UsedAppInRoutine) {
        selectedAppList.removeAll { it.packageName == app.packageName } // 패키지 기준
    }

    // 서버 전송용 DTO 생성기 (기존 유지)
    fun buildCreateRoutineRequest(imageKey: String?): CreateRoutineRequest {
        val stepsDto = stepList.mapIndexed { idx, step ->
            StepDto(
                name = step.title,
                stepOrder = idx + 1,
                estimatedTime = (step.time.takeIf { it.isNotBlank() } ?: "00:00:00").toIsoDuration()
            )
        }
        return CreateRoutineRequest(
            title = routineTitle.value,
            imageKey = imageKey,
            tags = tagList.toList(),
            description = routineDescription.value,
            steps = stepsDto,
            selectedApps = selectedAppList.map { it.packageName },
            isSimple = !isFocusingRoutine.value,
            isUserVisible = showUser.value
        )
    }

    private val TAG = "createroutine"
    fun createRoutine(imageKey: String?) {
        val trace = UUID.randomUUID().toString().take(8)

        viewModelScope.launch {
            submitError.value = null
            isSubmitting.value = true

            // [추가] 이미지 업로드 (있으면) → imageKey 얻기
            val finalImageKey = try {
                val localUri = imageUri.value
                if (localUri != null) {
                    Log.d(TAG, "[$trace] try upload image uri=$localUri")
                    val file = copyUriToCache(localUri) // [추가]
                    val key = crImageRepository.uploadImage(file).getOrThrow() // [추가]
                    Log.d(TAG, "[$trace] image uploaded. key=$key name=${file.name} size=${file.length()}")
                    key
                } else {
                    imageKey // 그대로 사용(null 가능)
                }
            } catch (e: Exception) {
                Log.d(TAG, "[$trace] image upload failed: ${e.message}")
                Log.d(TAG, Log.getStackTraceString(e))
                null // 업로드 실패 시 이미지 없이 진행(서버가 필수라면 400로 확인 가능)
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

    // [추가] content:// URI → 캐시 파일 복사
    private fun copyUriToCache(uri: Uri): File {
        val ctx = appContext // ← 아래 전체 코드에 Application 전달 주입 추가
        val file = File.createTempFile("moru_upload_", ".jpg", ctx.cacheDir)
        ctx.contentResolver.openInputStream(uri).use { inS ->
            file.outputStream().use { outS -> inS!!.copyTo(outS) }
        }
        return file
    }

    fun submitRoutine(imageKey: String?) {
        createRoutine(imageKey)
    }
}