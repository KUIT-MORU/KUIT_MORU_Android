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
    val stepList = mutableStateListOf(StepUi())
    val editingStepIndex = mutableStateOf<Int?>(null)
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

    fun setEditingStep(index: Int) {
        editingStepIndex.value = index
    }

    fun getEditingStepTime(): String? =
        editingStepIndex.value?.let { idx -> stepList.getOrNull(idx)?.time }

    fun addStep() {
        stepList.add(StepUi())
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

//    private fun reassignStepOrders() {
//        stepList.forEachIndexed { i, s ->
//            if (s.stepOrder != i + 1) {
//                stepList[i] = s.copy(stepOrder = i + 1)
//            }
//        }
//    }

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

    // HH:MM:SS -> ISO-8601 (PT#H#M#S)
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

    fun addAppToSelected(app: UsedAppInRoutine) {
        if (selectedAppList.none { it.packageName == app.packageName } && selectedAppList.size < 4) {
            selectedAppList.add(app)
        }
    }

    fun removeAppFromSelected(app: UsedAppInRoutine) {
        selectedAppList.removeAll { it.packageName == app.packageName } // 패키지 기준
    }

    // ---- 서버 전송 DTO 빌드 ----
    fun buildCreateRoutineRequest(imageKey: String?): CreateRoutineRequest {
        val simple = !isFocusingRoutine.value

        // 1) steps payload
        val stepsPayload: List<StepDto>? =
            if (simple) {
                // ✅ 간편 루틴: steps 필드 자체를 생략해야 하므로 null
                null
            } else {
                // 집중 루틴: 시간 규칙 준수 (비었으면 PT0S, 있으면 HH:MM:SS -> PT#H#M#S)
                stepList.mapIndexed { idx, step ->
                    val timeIso: String =
                        if (step.time.isBlank()) "PT0S" else step.time.toIsoDuration()
                    Log.d(
                        "createroutine",
                        "[build] step idx=${idx + 1} title='${step.title}' time='${step.time}' -> iso=$timeIso simple=$simple"
                    )
                    StepDto(
                        name = step.title,
                        stepOrder = idx + 1,
                        estimatedTime = timeIso
                    )
                }
            }

        // 2) selectedApps payload
        val appsPayload: List<String>? =
            if (simple) {
                // ✅ 간편 루틴: selectedApps 필드 자체 생략
                null
            } else {
                // 집중 루틴: 선택 앱이 없으면 빈 배열로 보냄([]) — 서버가 허용
                selectedAppList.map { it.packageName }
            }

        Log.d(
            "createroutine",
            "[build] final isSimple=$simple steps=${stepsPayload?.size ?: 0}(null means omitted) " +
                    "anyTimeSent=${stepsPayload?.any { it.estimatedTime != null } ?: false} " +
                    "apps=${appsPayload?.size ?: 0}(null means omitted)"
        )

        return CreateRoutineRequest(
            title = routineTitle.value,
            imageKey = imageKey,
            tags = tagList.toList(),
            description = routineDescription.value,
            steps = stepsPayload,          // ✅ 간편이면 null → JSON에서 아예 빠짐
            selectedApps = appsPayload,    // ✅ 간편이면 null → JSON에서 아예 빠짐
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

            // [추가] 이미지 업로드 (있으면) → imageKey 얻기
            val finalImageKey = try {
                val localUri = imageUri.value
                if (localUri != null) {
                    Log.d(TAG, "[$trace] try upload image uri=$localUri")
                    val file = copyUriToCache(localUri) // [추가]
                    val key = crImageRepository.uploadImage(file).getOrThrow() // [추가]
                    Log.d(
                        TAG,
                        "[$trace] image uploaded. key=$key name=${file.name} size=${file.length()}"
                    )
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