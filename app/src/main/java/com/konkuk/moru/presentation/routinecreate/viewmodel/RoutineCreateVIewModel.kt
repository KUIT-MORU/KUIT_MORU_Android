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
import com.konkuk.moru.data.model.Step
import com.konkuk.moru.data.model.UsedAppInRoutine
import androidx.core.graphics.createBitmap

// [유지] 서버 DTO들
data class CreateRoutineRequest(
    val title: String,
    val imageKey: String?,
    val tags: List<String>,
    val description: String,
    val steps: List<StepDto>,
    val selectedApps: List<String>,
    val isSimple: Boolean,
    val isUserVisible: Boolean
)

data class StepDto(
    val name: String,
    val stepOrder: Int,
    val estimatedTime: String // ISO-8601 duration
)

class RoutineCreateViewModel : ViewModel() {

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

    // [유지] HH:MM:SS → PT#H#M#S 변환
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

                // [유지] 어떤 Drawable이 와도 안전하게 비트맵 변환
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
            } catch (_: Exception) {}
        }
    }

    // [유지] Drawable → Bitmap 안전 변환
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

    fun addAppToSelected(app: UsedAppInRoutine) {
        if (selectedAppList.none { it.packageName == app.packageName } && selectedAppList.size < 4) {
            selectedAppList.add(app)
        }
    }

    fun removeAppFromSelected(app: UsedAppInRoutine) {
        selectedAppList.removeAll { it.packageName == app.packageName } // [변경] 패키지 기준
    }

    // [유지] 서버 전송용 DTO 생성기
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
            selectedApps = selectedAppList.map { it.packageName }, // [중요 변경]
            isSimple = !isFocusingRoutine.value, // [중요 매핑]
            isUserVisible = showUser.value
        )
    }

    fun submitRoutine(imageKey: String?) {
        val req = buildCreateRoutineRequest(imageKey)
        Log.d("RoutineCreate", "CreateRoutineRequest → $req")
        Log.d("RoutineCreate", "이미지 URI(Local): ${imageUri.value?.toString()}")
        // TODO: Repository.createRoutine(req) 호출로 네트워크 연결
    }
}