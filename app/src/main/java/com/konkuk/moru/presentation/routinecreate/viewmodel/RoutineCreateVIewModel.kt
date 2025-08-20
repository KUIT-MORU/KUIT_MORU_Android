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

    val imageUri = mutableStateOf<Uri?>(null)
    val showUser = mutableStateOf(true)
    val isFocusingRoutine = mutableStateOf(true)
    val routineTitle = mutableStateOf("")
    val routineDescription = mutableStateOf("")
    val tagList = mutableStateListOf<String>()
    // [변경] 기본 3개
    val stepList = mutableStateListOf(StepUi(), StepUi(), StepUi())
    val editingStepIndex = mutableStateOf<Int?>(null)
    val appList = mutableStateListOf<UsedAppInRoutine>()
    val selectedAppList = mutableStateListOf<UsedAppInRoutine>() // 최대 4개 (UI/로직에서 제한)

    // UI 상태
    val isSubmitting = mutableStateOf(false)
    val submitError = mutableStateOf<String?>(null)
    val createdRoutineId = mutableStateOf<String?>(null)

    // [변경] 제목 10자 컷
    fun updateTitle(title: String) {
        routineTitle.value = title.take(10)
    }

    fun toggleShowUser() {
        showUser.value = !showUser.value
    }

    fun toggleFocusingRoutine() {
        isFocusingRoutine.value = !isFocusingRoutine.value
    }

    // [변경] 설명 32자 컷
    fun updateDescription(desc: String) {
        routineDescription.value = desc.take(32)
    }

    // [변경] 태그: 1~3개, 각 태그 ≤ 5자
    fun addTag(tag: String) {
        val t = tag.removePrefix("#").trim()
        if (t.isNotBlank() && t.length <= 5 && tagList.size < 3 && !tagList.contains(t)) {
            tagList.add(t)
        }
    }

    fun addTags(tags: List<String>) {
        val normalized = tags.map { it.removePrefix("#").trim() }
            .filter { it.isNotBlank() && it.length <= 5 }

        normalized.forEach { t ->
            if (!tagList.contains(t) && tagList.size < 3) {
                tagList.add(t)
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

    // [변경] 최대 6개
    fun addStep() {
        if (stepList.size < 6) stepList.add(StepUi())
    }

    // [변경] 최소 3개 유지
    fun removeStep(index: Int) {
        if (stepList.size > 3 && index in stepList.indices) stepList.removeAt(index)
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
    private fun hmsToIso(h: Int, m: Int, s: Int): String {
        val sb = StringBuilder("PT")
        if (h > 0) sb.append("${h}H")
        if (m > 0) sb.append("${m}M")
        if (s > 0 || (h == 0 && m == 0)) sb.append("${s}S")
        return sb.toString()
    }

    private fun isoToHmsOrNull(iso: String): String? = try {
        val d = java.time.Duration.parse(iso)
        val total = d.seconds
        val h = (total / 3600).toInt()
        val m = ((total % 3600) / 60).toInt()
        val s = (total % 60).toInt()
        String.format("%02d:%02d:%02d", h, m, s)
    } catch (_: Exception) {
        null
    }

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

    // 앱은 최대 4개
    fun addAppToSelected(app: UsedAppInRoutine) {
        if (selectedAppList.none { it.packageName == app.packageName } && selectedAppList.size < 4) {
            selectedAppList.add(app)
        }
    }

    fun removeAppFromSelected(app: UsedAppInRoutine) {
        selectedAppList.removeAll { it.packageName == app.packageName }
    }

    fun validateForSubmit(): String? {
        // 1) 제목: 1~10자
        val title = routineTitle.value
        if (title.isBlank() || title.length > 10) return "제목은 1~10자여야 해요."

        // 2) 태그: 1~3개, 각 1~5자
        val tags = tagList
        if (tags.isEmpty()) return "태그를 최소 1개 선택해 주세요."
        if (tags.size !in 1..3) return "태그는 1~3개까지만 선택할 수 있어요."
        if (tags.any { it.isBlank() || it.length > 5 }) return "각 태그 길이는 최대 5자예요."

        // 3) 설명: 최대 32자
        if (routineDescription.value.length > 32) return "설명은 최대 32자까지 입력할 수 있어요."

        // 4) STEP: 3~6개, 제목 필수
        val steps = stepList
        if (steps.size !in 3..6) return "STEP은 3~6개여야 해요."
        if (steps.any { it.title.isBlank() }) return "모든 STEP에 제목을 입력해 주세요."

        // 5) 집중 루틴일 때: 각 STEP 시간 필수, 사용 앱 최대 4개
        if (isFocusingRoutine.value) {
            if (steps.any { it.time.isBlank() }) return "집중 루틴은 각 STEP의 소요시간이 필요해요."
            if (selectedAppList.size > 4) return "사용 앱은 최대 4개까지 선택할 수 있어요."
        }

        // 모두 통과
        return null
    }

    // ---- 서버 전송 DTO 빌드 ----
    // [변경] 간편/집중 모두 steps 포함(3~6개). 간편의 시간은 PT0S로 전송.
    fun buildCreateRoutineRequest(imageKey: String?): CreateRoutineRequest {
        val focusing = isFocusingRoutine.value

        val stepsPayload: List<StepDto> =
            stepList.mapIndexed { idx, step ->
                val timeIso: String =
                    if (focusing) {
                        if (step.time.isBlank()) "PT0S" else step.time.toIsoDuration()
                    } else {
                        "PT0S"
                    }
                Log.d(
                    "createroutine",
                    "[build] step idx=${idx + 1} title='${step.title}' time='${step.time}' -> iso=$timeIso focusing=$focusing"
                )
                StepDto(
                    name = step.title,
                    stepOrder = idx + 1,
                    estimatedTime = timeIso
                )
            }

        val appsPayload: List<String> =
            if (focusing) {
                selectedAppList.map { it.packageName }
            } else {
                emptyList()
            }

        Log.d(
            "createroutine",
            "[build] final focusing=$focusing steps=${stepsPayload.size} apps=${appsPayload.size}"
        )

        return CreateRoutineRequest(
            title = routineTitle.value,
            imageKey = imageKey,
            tags = tagList.toList(),
            description = routineDescription.value,
            steps = stepsPayload,          // [변경]
            selectedApps = appsPayload,    // [변경]
            isSimple = !focusing,
            isUserVisible = showUser.value
        )
    }

    private val TAG = "createroutine"
    fun createRoutine(imageKey: String?) {
        val trace = UUID.randomUUID().toString().take(8)

        viewModelScope.launch {
            submitError.value = null
            isSubmitting.value = true

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
                imageKey
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

    fun submitRoutine(imageKey: String?) {
        createRoutine(imageKey)
    }
}