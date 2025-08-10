package com.konkuk.moru.presentation.routinecreate.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.konkuk.moru.R
import com.konkuk.moru.core.component.ImageChoiceOptionButtonScreen
import com.konkuk.moru.core.component.Switch.RoutineSimpleFocusSwitch
import com.konkuk.moru.core.component.routinedetail.AddStepButton
import com.konkuk.moru.core.component.routinedetail.DraggableAppSearchBottomSheet
import com.konkuk.moru.core.component.routinedetail.MyRoutineTagInCreateRoutine
import com.konkuk.moru.core.component.routinedetail.RoutineDescriptionField
import com.konkuk.moru.core.component.routinedetail.RoutineImageSelectBox
import com.konkuk.moru.core.component.routinedetail.ShowUserCheckbox
import com.konkuk.moru.core.component.routinedetail.appdisplay.AddAppBox
import com.konkuk.moru.core.component.routinedetail.appdisplay.SelectedAppNoText
import com.konkuk.moru.presentation.routinecreate.component.StepItem
import com.konkuk.moru.presentation.routinecreate.component.TimePickerDialog
import com.konkuk.moru.presentation.routinecreate.viewmodel.RoutineCreateViewModel
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun RoutineCreateScreen(
    navController: NavHostController,
    viewModel: RoutineCreateViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    val isFocusingRoutine by viewModel.isFocusingRoutine
    val showUser by viewModel.showUser
    val routineDescription by viewModel.routineDescription
    val tagList = viewModel.tagList
    val stepList = viewModel.stepList

    var isImageOptionVisible by remember { mutableStateOf(false) }
    var isTimePickerVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val cameraImageUriState = remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.imageUri.value = cameraImageUriState.value
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createImageUri(context)
            cameraImageUriState.value = uri
            takePictureLauncher.launch(uri)
        } else {
            // 권한 거부 시: 따로 안내 필요하면 스낵바/토스트 등 처리
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.imageUri.value = uri
    }

    // 상단 선언부
    val imageUriState = remember { mutableStateOf<Uri?>(null) }
    //val appList = remember { mutableStateListOf(UsedAppInRoutine("YouTube", "https://www.youtube.com/logo.png")) }
    val stepListScrollState = rememberLazyListState()

    val isSubmitEnabled = viewModel.routineTitle.value.isNotBlank() &&
            viewModel.stepList.any { it.title.isNotBlank() && it.time.isNotBlank() } &&
            viewModel.tagList.isNotEmpty()
    //val isSubmitEnabled = true

    // 사용앱 바텀시트 관련
    val coroutineScope = rememberCoroutineScope()
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val allApps = viewModel.appList
    val selectedAppList = viewModel.selectedAppList
    // 개발용 임시 리스트
//    val dummyBitmap = createBitmap(64, 64).apply {
//        eraseColor(0xFFF1F3F5.toInt())
//    }.asImageBitmap()
//    val selectedAppList = listOf<UsedAppInRoutine>(
//        UsedAppInRoutine("YouTube", dummyBitmap),
//        UsedAppInRoutine("Instagram", dummyBitmap),
//        UsedAppInRoutine("Twitter", dummyBitmap)
//    )

    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.veryLightGray)
            .systemBarsPadding()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(color = colors.veryLightGray),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_x),
                    contentDescription = "Close",
                    modifier = Modifier
                        .padding(start = 16.dp, end = 22.dp)
                        .size(18.dp)
                        .clickable(
                            indication = null,
                            interactionSource = null
                        ) { navController.popBackStack() }
                )
                Text(text = "루틴 생성", style = typography.desc_M_16)
            }

            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
                    .padding(horizontal = 16.dp),
                state = stepListScrollState,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(17.dp))
                    // 이미지 + 설명 영역
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        RoutineImageSelectBox(
                            selectedImageUri = viewModel.imageUri.value,
                            onClick = {
                                isImageOptionVisible = true
                                focusManager.clearFocus()
                            }
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            BasicTextField(
                                value = viewModel.routineTitle.value,
                                onValueChange = {
                                    if (it.length <= 30) viewModel.updateTitle(it) // 최대 30자
                                },
                                textStyle = typography.title_B_24,
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    if (viewModel.routineTitle.value.isEmpty()) {
                                        Text("루틴 제목", style = typography.title_B_24)
                                    }
                                    innerTextField()
                                }
                            )
                            Spacer(modifier = Modifier.height(9.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ShowUserCheckbox(
                                    showUser = showUser,
                                    onClick = { viewModel.toggleShowUser() }
                                )
                                RoutineSimpleFocusSwitch(
                                    checked = isFocusingRoutine,
                                    onClick = { viewModel.toggleFocusingRoutine() }
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            RoutineDescriptionField(
                                value = routineDescription,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                onValueChange = { viewModel.updateDescription(it) }
                            )
                        }
                    }
                }

                item {
                    // 태그 추가 버튼
                    MyRoutineTagInCreateRoutine(
                        tagList = tagList,
                        //tagList = listOf("운동", "건강", "루틴"),
                        onAddTag = {
                            viewModel.addTag("예시태그")
                        },
                        onDeleteTag = {
                            viewModel.removeTag("예시태그")
                        }
                    )
                }

                item { Text("STEP", style = typography.title_B_20) }
                itemsIndexed(
                    items = stepList,
                    key = { _, step -> step.id }
                ) { index, step ->
                    StepItem(
                        step = step,
                        stepCount = stepList.size,
                        isFocusingRoutine = isFocusingRoutine,
                        onTitleChange = { newTitle ->
                            viewModel.updateStepTitle(step.id, newTitle)
                        },
                        onShowTimePicker = {
                            viewModel.setEditingStep(step.id)
                            isTimePickerVisible = true
                        },
                        onDelete = {
                            viewModel.removeStep(step.id)
                        }
                    )
                }

                item { AddStepButton { viewModel.addStep() } }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }

            if (isFocusingRoutine) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White)
                        .padding(bottom = 30.dp)
                ) {
                    Text(
                        text = "사용앱",
                        style = typography.title_B_20,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(9.dp),
                        contentPadding = PaddingValues(start = 11.dp),
                    ) {
                        items(selectedAppList) { app ->
                            SelectedAppNoText(
                                appIcon = app.appIcon,
                                isRemovable = true
                            ) { viewModel.removeAppFromSelected(app) }
                        }

                        if (selectedAppList.size < 4) {
                            item {
                                AddAppBox {
                                    coroutineScope.launch {
                                        isBottomSheetOpen = true
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 완료하기 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(if (isSubmitEnabled) colors.limeGreen else colors.lightGray)
                    .clickable(
                        enabled = isSubmitEnabled,
                        indication = null,
                        interactionSource = null
                    ) {
                        // TODO: 이미지 업로드하여 imageKey 획득 필요
                        val imageKey: String? = null // [임시] 서버 업로드 연동 후 실제 키로 치환
                        viewModel.submitRoutine(imageKey) // [변경] DTO 생성/로그
                        navController.popBackStack()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "완료하기",
                    style = typography.body_SB_16,
                    color = if (isSubmitEnabled) colors.black else colors.darkGray
                )
            }
        }

        // 이미지 선택 옵션 팝업
        if (isImageOptionVisible) {
            ImageChoiceOptionButtonScreen(
                onImageSelected = {
                    imagePickerLauncher.launch("image/*")
                    isImageOptionVisible = false
                },
                onCameraSelected = {
                    // [변경] 실제 카메라 촬영 동작
                    val permission = Manifest.permission.CAMERA
                    val isGranted = ContextCompat.checkSelfPermission(
                        context, permission
                    ) == PackageManager.PERMISSION_GRANTED

                    if (isGranted) {
                        val uri = createImageUri(context)
                        cameraImageUriState.value = uri
                        takePictureLauncher.launch(uri)
                    } else {
                        cameraPermissionLauncher.launch(permission)
                    }
                    isImageOptionVisible = false
                },
                onCancel = { isImageOptionVisible = false }
            )
        }

        // 시간 선택 팝업
        if (isTimePickerVisible) {
            TimePickerDialog(
                initialTime = viewModel.getEditingStepTime(),
                onConfirm = { h, m, s ->
                    viewModel.confirmTime(h, m, s)
                    isTimePickerVisible = false
                },
                onDismiss = { isTimePickerVisible = false }
            )
        }

    }
    DraggableAppSearchBottomSheet(
        isVisible = isBottomSheetOpen,
        onDismiss = { isBottomSheetOpen = false },
        appList = allApps,
        selectedAppList = selectedAppList,
        onAddApp = { app ->
            viewModel.addAppToSelected(app)
        },
        onRemoveApp = { app ->
            viewModel.removeAppFromSelected(app)
        },
    )
}
private fun createImageUri(context: Context): Uri {
    val imageFile = File.createTempFile(
        "moru_camera_", ".jpg",
        context.cacheDir // 외부 저장 권한 없이 캐시에 저장
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

@Preview
@Composable
private fun RoutineCreateScreenPreview() {
    val navController = NavHostController(LocalContext.current)
    RoutineCreateScreen(navController = navController)
}