package com.konkuk.moru.presentation.routinefocus.screen

import androidx.compose.runtime.Composable
import com.konkuk.moru.presentation.routinefocus.viewmodel.RoutineFocusViewModel

@Composable
fun RoutineFocusScreenContainer(
    viewModel: RoutineFocusViewModel,
    onDismiss: () -> Unit,
    routineItems: List<Pair<String, String>>
) {
    if (viewModel.isLandscapeMode) {
        LandscapeRoutineFocusScreen(
            viewModel = viewModel,
            onDismiss = onDismiss,
            routineItems = routineItems,
            currentStep = viewModel.currentStep
        )
    } else {
        PortraitRoutineFocusScreen(
            viewModel = viewModel,
            onDismiss = onDismiss,
            routineItems = routineItems,
            currentStep = viewModel.currentStep
        )
    }
}
