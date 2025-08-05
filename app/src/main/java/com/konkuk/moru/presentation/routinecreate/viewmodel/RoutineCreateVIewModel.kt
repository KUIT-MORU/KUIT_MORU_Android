package com.konkuk.moru.presentation.routinecreate.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import com.konkuk.moru.data.model.Step
import com.konkuk.moru.data.model.UsedAppInRoutine

class RoutineCreateViewModel : ViewModel() {

    val imageUri = mutableStateOf<Uri?>(null)
    val showUser = mutableStateOf(true)
    val isFocusingRoutine = mutableStateOf(true)
    val routineTitle = mutableStateOf("")
    val routineDescription = mutableStateOf("")
    val tagList = mutableStateListOf<String>()
    val stepList = mutableStateListOf(Step(title = "", time = "")) // ✅ 초기에도 id 생성
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
//        if (tag.isNotBlank() && !tagList.contains(tag)) {
//            tagList.add(tag)
//        }
        tagList.add(tag) //Todo: 중복 체크 로직 추가 필요
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

        appList.clear() // 중복 방지

        apps.forEach { appInfo ->
            try {
                val label = pm.getApplicationLabel(appInfo).toString()
                val iconDrawable = pm.getApplicationIcon(appInfo)

                val bitmap = (iconDrawable as BitmapDrawable).bitmap
                val imageBitmap = bitmap.asImageBitmap()

                // 이미 추가된 앱은 건너뜀
                if (appList.none { it.appName == label }) {
                    appList.add(UsedAppInRoutine(appName = label, appIcon = imageBitmap))
                }
            } catch (_: Exception) {}
        }
    }

    fun addAppToSelected(app: UsedAppInRoutine) {
        if (app !in selectedAppList && selectedAppList.size < 4) {
            selectedAppList.add(app)
        }
    }
    fun removeAppFromSelected(app: UsedAppInRoutine) {
        selectedAppList.remove(app)
    }

    fun submitRoutine() {
        val routineData = mapOf(
            "title" to routineTitle.value,
            "description" to routineDescription.value,
            "isFocused" to isFocusingRoutine.value,
            "showUser" to showUser.value,
            "tagList" to tagList.toList(),
            "steps" to stepList.map { it.copy() },
            "apps" to appList.map { it.copy() },
            "imageUri" to imageUri.value?.toString()
        )

        // ✅ Log 출력 추가
        Log.d("RoutineCreate", "루틴 제목: ${routineTitle.value}")
        Log.d("RoutineCreate", "설명: ${routineDescription.value}")
        Log.d("RoutineCreate", "집중 루틴 여부: ${isFocusingRoutine.value}")
        Log.d("RoutineCreate", "사용자 공개 여부: ${showUser.value}")
        Log.d("RoutineCreate", "태그: ${tagList.toList()}")
        Log.d("RoutineCreate", "스텝: ${stepList.map { "${it.title} - ${it.time}" }}")
        Log.d("RoutineCreate", "사용 앱: ${appList.map { "${it.appName} - ${it.appIcon}" }}")
        Log.d("RoutineCreate", "이미지 URI: ${imageUri.value?.toString()}")
    }
}