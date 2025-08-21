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
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.dto.request.CreateRoutineFocusRequest
import com.konkuk.moru.data.dto.request.CreateRoutineSimpleRequest
import com.konkuk.moru.data.dto.request.FocusStepDto
import com.konkuk.moru.data.dto.request.SimpleStepDto
import com.konkuk.moru.data.model.StepUi
import com.konkuk.moru.data.model.UsedAppInRoutine
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

    // ---- UI State ----
    val imageUri = mutableStateOf<Uri?>(null)
    val showUser = mutableStateOf(true)
    val isFocusingRoutine = mutableStateOf(true)
    val routineTitle = mutableStateOf("")
    val routineDescription = mutableStateOf("")
    val tagList = mutableStateListOf<String>()
    val stepList = mutableStateListOf(StepUi(), StepUi(), StepUi()) // 기본 3개
    val editingStepIndex = mutableStateOf<Int?>(null)
    val appList = mutableStateListOf<UsedAppInRoutine>()
    val selectedAppList = mutableStateListOf<UsedAppInRoutine>() // 최대 4개

    // ---- Submit State ----
    val isSubmitting = mutableStateOf(false)
    val submitError = mutableStateOf<String?>(null)
    val createdRoutineId = mutableStateOf<String?>(null)

    // ---- Log ----
    private val TAG = "createroutine" // [변경] 일관된 TAG

    // ---- Helper: 현재 상태 스냅샷 로깅 ----
    // [변경] 상태를 한 번에 로깅하는 유틸
    private fun logState(prefix: String) {
        val stepsStr = stepList.joinToString(" | ") { s -> "('${s.title}', time='${s.time}')" }
        val appsStr = selectedAppList.joinToString(",") { it.packageName }
        Log.d(TAG, "[$prefix] title='${routineTitle.value}', desc='${routineDescription.value}', isFocusing=${isFocusingRoutine.value}, showUser=${showUser.value}")
        Log.d(TAG, "[$prefix] tags(${tagList.size})=${tagList.joinToString(",")}")
        Log.d(TAG, "[$prefix] steps(${stepList.size})=$stepsStr")
        Log.d(TAG, "[$prefix] selectedApps(${selectedAppList.size})=$appsStr")
        Log.d(TAG, "[$prefix] imageUri=${imageUri.value}")
    }

    // ---- Title / Description / Toggles ----
    // [변경] 제목 변경 시 로깅
    fun updateTitle(title: String) {
        val cut = title.take(10)
        if (cut != routineTitle.value) {
            Log.d(TAG, "[vm] updateTitle: '${routineTitle.value}' -> '$cut'")
            routineTitle.value = cut
        }
    }

    // [변경] 설명 변경 시 로깅
    fun updateDescription(desc: String) {
        val cut = desc.take(32)
        if (cut != routineDescription.value) {
            Log.d(TAG, "[vm] updateDescription: '${routineDescription.value}' -> '$cut'")
            routineDescription.value = cut
        }
    }

    // [변경] 사용자 표시 토글 로깅
    fun toggleShowUser() {
        showUser.value = !showUser.value
        Log.d(TAG, "[vm] toggleShowUser -> ${showUser.value}")
    }

    // [변경] 집중/간편 토글 로깅
    fun toggleFocusingRoutine() {
        isFocusingRoutine.value = !isFocusingRoutine.value
        Log.d(TAG, "[vm] toggleFocusingRoutine -> ${isFocusingRoutine.value}")
    }

    // ---- Tag Ops ----
    // [변경] 태그 추가/배치/삭제 로깅
    fun addTag(tag: String) {
        val t = tag.removePrefix("#").trim()
        Log.d(TAG, "[vm] addTag(raw='$tag') -> norm='$t'")
        if (t.isNotBlank() && t.length <= 5 && tagList.size < 3 && !tagList.contains(t)) {
            tagList.add(t)
            Log.d(TAG, "[vm] addTag: added '$t' (count=${tagList.size})")
        } else {
            Log.d(TAG, "[vm] addTag: rejected '$t' (valid=${t.isNotBlank() && t.length <= 5}, size=${tagList.size})")
        }
    }

    fun addTags(tags: List<String>) {
        Log.d(TAG, "[vm] addTags: raw=${tags}")
        val normalized = tags.map { it.removePrefix("#").trim() }
            .filter { it.isNotBlank() && it.length <= 5 }
        normalized.forEach { t ->
            if (!tagList.contains(t) && tagList.size < 3) {
                tagList.add(t)
                Log.d(TAG, "[vm] addTags: added '$t' (count=${tagList.size})")
            } else {
                Log.d(TAG, "[vm] addTags: skipped '$t' (dup=${tagList.contains(t)}, size=${tagList.size})")
            }
        }
    }

    fun removeTag(tag: String) {
        val removed = tagList.remove(tag)
        Log.d(TAG, "[vm] removeTag('$tag') -> $removed (count=${tagList.size})")
    }

    // ---- Step Ops ----
    fun setEditingStep(index: Int) {
        editingStepIndex.value = index
        Log.d(TAG, "[vm] setEditingStep -> $index")
    }

    fun getEditingStepTime(): String? =
        editingStepIndex.value?.let { idx -> stepList.getOrNull(idx)?.time }

    // [변경] 스텝 추가/삭제 로깅
    fun addStep() {
        if (stepList.size < 6) {
            stepList.add(StepUi())
            Log.d(TAG, "[vm] addStep -> size=${stepList.size}")
        } else {
            Log.d(TAG, "[vm] addStep: rejected (size=${stepList.size})")
        }
    }

    fun removeStep(index: Int) {
        if (stepList.size > 3 && index in stepList.indices) {
            val removed = stepList[index]
            stepList.removeAt(index)
            Log.d(TAG, "[vm] removeStep($index) -> '${removed.title}' (size=${stepList.size})")
        } else {
            Log.d(TAG, "[vm] removeStep: rejected (index=$index, size=${stepList.size})")
        }
    }

    // [변경] 스텝 제목 변경 로깅
    fun updateStepTitle(index: Int, newTitle: String) {
        if (index in stepList.indices) {
            val old = stepList[index].title
            stepList[index] = stepList[index].copy(title = newTitle)
            Log.d(TAG, "[vm] updateStepTitle[$index]: '$old' -> '$newTitle'")
        } else {
            Log.d(TAG, "[vm] updateStepTitle: invalid index $index")
        }
    }

    // [변경] 스텝 시간 확정 로깅
    @SuppressLint("DefaultLocale")
    fun confirmTime(hour: Int, minute: Int, second: Int) {
        val i = editingStepIndex.value ?: run {
            Log.d(TAG, "[vm] confirmTime: no editing index")
            return
        }
        if (i !in stepList.indices) {
            Log.d(TAG, "[vm] confirmTime: invalid index $i")
            return
        }
        val formatted = String.format("%02d:%02d:%02d", hour, minute, second)
        val old = stepList[i].time
        stepList[i] = stepList[i].copy(time = formatted)
        Log.d(TAG, "[vm] confirmTime[$i]: '$old' -> '$formatted'")
    }

    // ---- 시간 유틸 ----
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

    // ---- 앱 조회/선택 ----
    @SuppressLint("QueryPermissionsNeeded")
    fun loadInstalledApps(context: Context) {
        Log.d(TAG, "[vm] loadInstalledApps: start")
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        appList.clear()
        var added = 0

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
                    added++
                }
            } catch (e: Exception) {
                Log.d(TAG, "[vm] loadInstalledApps: skip ${appInfo.packageName} - ${e.message}")
            }
        }

        Log.d(TAG, "[vm] loadInstalledApps: done added=$added total=${appList.size}")
        if (appList.isNotEmpty()) {
            val sample = appList.take(5).joinToString { it.packageName }
            Log.d(TAG, "[vm] loadInstalledApps: sample=$sample ...")
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

    // 앱 선택/해제
    fun addAppToSelected(app: UsedAppInRoutine) {
        if (selectedAppList.none { it.packageName == app.packageName } && selectedAppList.size < 4) {
            selectedAppList.add(app)
            Log.d(TAG, "[vm] addAppToSelected: '${app.packageName}' (size=${selectedAppList.size})")
        } else {
            Log.d(TAG, "[vm] addAppToSelected: rejected '${app.packageName}' (dup=${selectedAppList.any { it.packageName == app.packageName }}, size=${selectedAppList.size})")
        }
    }

    fun removeAppFromSelected(app: UsedAppInRoutine) {
        val before = selectedAppList.size
        selectedAppList.removeAll { it.packageName == app.packageName }
        val after = selectedAppList.size
        Log.d(TAG, "[vm] removeAppFromSelected: '${app.packageName}' ($before -> $after)")
    }

    // ---- 검증 ----
    // [변경] 진입/실패 사유/성공 로그
    fun validateForSubmit(): String? {
        Log.d(TAG, "[vm] validateForSubmit: start")
        // 1) 제목: 1~10자
        val title = routineTitle.value
        if (title.isBlank() || title.length > 10) {
            Log.d(TAG, "[vm] validateForSubmit: fail - title invalid ('$title')")
            return "제목은 1~10자여야 해요."
        }

        // 2) 태그: 1~3개, 각 1~5자
        val tags = tagList
        if (tags.isEmpty()) {
            Log.d(TAG, "[vm] validateForSubmit: fail - no tags")
            return "태그를 최소 1개 선택해 주세요."
        }
        if (tags.size !in 1..3) {
            Log.d(TAG, "[vm] validateForSubmit: fail - tags size ${tags.size}")
            return "태그는 1~3개까지만 선택할 수 있어요."
        }
        if (tags.any { it.isBlank() || it.length > 5 }) {
            Log.d(TAG, "[vm] validateForSubmit: fail - tag length exceeded")
            return "각 태그 길이는 최대 5자예요."
        }

        // 3) 설명: 최대 32자
        if (routineDescription.value.length > 32) {
            Log.d(TAG, "[vm] validateForSubmit: fail - desc length ${routineDescription.value.length}")
            return "설명은 최대 32자까지 입력할 수 있어요."
        }

        // 4) STEP: 3~6개, 제목 필수
        val steps = stepList
        if (steps.size !in 3..6) {
            Log.d(TAG, "[vm] validateForSubmit: fail - steps size ${steps.size}")
            return "STEP은 3~6개여야 해요."
        }
        if (steps.any { it.title.isBlank() }) {
            Log.d(TAG, "[vm] validateForSubmit: fail - step title blank")
            return "모든 STEP에 제목을 입력해 주세요."
        }

        // 5) 집중 루틴일 때: 각 STEP 시간 필수, 사용 앱 최대 4개
        if (isFocusingRoutine.value) {
            if (steps.any { it.time.isBlank() }) {
                Log.d(TAG, "[vm] validateForSubmit: fail - focusing but time missing")
                return "집중 루틴은 각 STEP의 소요시간이 필요해요."
            }
            if (selectedAppList.size > 4) {
                Log.d(TAG, "[vm] validateForSubmit: fail - selected apps ${selectedAppList.size}")
                return "사용 앱은 최대 4개까지 선택할 수 있어요."
            }
        }

        Log.d(TAG, "[vm] validateForSubmit: success")
        return null
    }

    // ---- 요청 DTO 빌드 ----
    // [변경] 반환 타입 Any로 변경. Simple/Focus 분기 + 상세 로깅
    fun buildCreateRoutineRequest(imageKey: String?): Any {
        val focusing = isFocusingRoutine.value
        Log.d(TAG, "[vm] buildCreateRoutineRequest: start focusing=$focusing imageKey=$imageKey")
        logState("buildCreateRoutineRequest/before")

        return if (focusing) {
            // 집중 루틴: 시간 포함
            val stepsPayload: List<FocusStepDto> =
                stepList.mapIndexed { idx, step ->
                    val timeIso = step.time.takeIf { it.isNotBlank() }?.toIsoDuration() ?: "PT0S"
                    Log.d(TAG, "[vm] build/focus step[$idx]: title='${step.title}', time='${step.time}' -> iso='$timeIso'")
                    FocusStepDto(
                        name = step.title,
                        stepOrder = idx + 1,
                        estimatedTime = timeIso
                    )
                }

            val appsPayload: List<String> = selectedAppList.map { it.packageName }
            Log.d(TAG, "[vm] build/focus apps(${appsPayload.size})=$appsPayload")

            val dto = CreateRoutineFocusRequest(
                title = routineTitle.value,
                imageKey = imageKey,
                tags = tagList.toList(),
                description = routineDescription.value,
                steps = stepsPayload,
                selectedApps = appsPayload,
                isSimple = false,
                isUserVisible = showUser.value
            )
            Log.d(TAG, "[vm] buildCreateRoutineRequest: done -> CreateRoutineFocusRequest(steps=${stepsPayload.size}, apps=${appsPayload.size})")
            dto
        } else {
            // 간편 루틴: 시간 필드 없음
            val stepsPayload: List<SimpleStepDto> =
                stepList.mapIndexed { idx, step ->
                    Log.d(TAG, "[vm] build/simple step[$idx]: title='${step.title}'")
                    SimpleStepDto(
                        name = step.title,
                        stepOrder = idx + 1
                    )
                }

            val dto = CreateRoutineSimpleRequest(
                title = routineTitle.value,
                imageKey = imageKey,
                tags = tagList.toList(),
                description = routineDescription.value,
                steps = stepsPayload,
                selectedApps = null, // [중요] null → JSON 키 제외
                isSimple = true,
                isUserVisible = showUser.value
            )
            Log.d(TAG, "[vm] buildCreateRoutineRequest: done -> CreateRoutineSimpleRequest(steps=${stepsPayload.size})")
            dto
        }
    }

    // ---- 전송 ----
    fun createRoutine(imageKey: String?) {
        val trace = UUID.randomUUID().toString().take(8)
        Log.d(TAG, "[$trace] createRoutine: start imageKey=$imageKey")
        logState("$trace/createRoutine/before")

        viewModelScope.launch {
            submitError.value = null
            isSubmitting.value = true

            // 이미지 업로드
            val tUploadStart = System.nanoTime()
            val finalImageKey = try {
                val localUri = imageUri.value
                if (localUri != null) {
                    Log.d(TAG, "[$trace] upload: try uri=$localUri")
                    val file = copyUriToCache(localUri)
                    Log.d(TAG, "[$trace] upload: copied file=${file.name} size=${file.length()}")
                    val key = crImageRepository.uploadImage(file).getOrThrow()
                    val tElapsedMs = (System.nanoTime() - tUploadStart) / 1_000_000
                    Log.d(TAG, "[$trace] upload: success key=$key elapsed=${tElapsedMs}ms")
                    key
                } else {
                    Log.d(TAG, "[$trace] upload: skip (no local image)")
                    imageKey
                }
            } catch (e: Exception) {
                val tElapsedMs = (System.nanoTime() - tUploadStart) / 1_000_000
                Log.d(TAG, "[$trace] upload: failed elapsed=${tElapsedMs}ms err=${e.message}")
                Log.d(TAG, Log.getStackTraceString(e))
                imageKey
            }

            // DTO 빌드
            val req: Any = buildCreateRoutineRequest(finalImageKey)
            when (req) {
                is CreateRoutineFocusRequest -> {
                    Log.d(TAG, "[$trace] request type=Focus steps=${req.steps.size} apps=${req.selectedApps.size} isSimple=${req.isSimple}")
                }
                is CreateRoutineSimpleRequest -> {
                    Log.d(TAG, "[$trace] request type=Simple steps=${req.steps.size} isSimple=${req.isSimple}")
                }
                else -> Log.d(TAG, "[$trace] request type=Unknown ${req::class.java.simpleName}")
            }

            // 전송
            val tApiStart = System.nanoTime()
            Log.d(TAG, "[$trace] API: POST /api/routines (send)")
            val result = createRoutineRepository.createRoutine(req)
            val apiElapsedMs = (System.nanoTime() - tApiStart) / 1_000_000
            Log.d(TAG, "[$trace] API: done elapsed=${apiElapsedMs}ms")

            isSubmitting.value = false
            result
                .onSuccess { resp ->
                    Log.d(TAG, "[$trace] success: id=${resp.id} createdAt=${resp.createdAt}")
                    createdRoutineId.value = resp.id
                }
                .onFailure { e ->
                    Log.d(TAG, "[$trace] failure: ${e::class.java.simpleName} msg=${e.message}")
                    Log.d(TAG, Log.getStackTraceString(e))
                    submitError.value = e.message ?: "루틴 생성에 실패했어요."
                }

            logState("$trace/createRoutine/after")
            Log.d(TAG, "[$trace] createRoutine: end")
        }
    }

    // content:// URI → 캐시 파일 복사
    private fun copyUriToCache(uri: Uri): File {
        val ctx = appContext
        val file = File.createTempFile("moru_upload_", ".jpg", ctx.cacheDir)
        ctx.contentResolver.openInputStream(uri).use { inS ->
            file.outputStream().use { outS -> inS!!.copyTo(outS) }
        }
        Log.d(TAG, "[vm] copyUriToCache: ${file.absolutePath} size=${file.length()}")
        return file
    }

    fun submitRoutine(imageKey: String?) {
        Log.d(TAG, "[vm] submitRoutine: imageKey=$imageKey")
        createRoutine(imageKey)
    }
}