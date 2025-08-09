package com.konkuk.moru.presentation.routinefocus.screen

import androidx.compose.runtime.Composable
import com.konkuk.moru.presentation.routinefocus.viewmodel.SharedRoutineViewModel
import com.konkuk.moru.presentation.routinefocus.viewmodel.RoutineFocusViewModel

@Composable
fun RoutineFocusScreenContainer(
    focusViewModel: RoutineFocusViewModel,
    sharedViewModel: SharedRoutineViewModel,
    onDismiss: () -> Unit
){
    if (focusViewModel.isLandscapeMode) {
        LandscapeRoutineFocusScreen(
            viewModel = focusViewModel,
            sharedViewModel = sharedViewModel,
            onDismiss = onDismiss,
            currentStep = focusViewModel.currentStep
        )
    } else {
        PortraitRoutineFocusScreen(
            focusViewModel = focusViewModel,
            sharedViewModel = sharedViewModel,
            onDismiss = onDismiss,
            currentStep = focusViewModel.currentStep
        )
    }

}