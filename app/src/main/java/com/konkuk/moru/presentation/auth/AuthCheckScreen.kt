package com.konkuk.moru.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.konkuk.moru.core.datastore.LoginPreference
import com.konkuk.moru.core.datastore.OnboardingPreference
import com.konkuk.moru.presentation.navigation.Route
import kotlinx.coroutines.flow.first

@Composable
fun AuthCheckScreen(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val isLoggedIn = LoginPreference.isLoggedIn(context).first()
        val isOnboarded = OnboardingPreference.isOnboardingComplete(context).first()

        val targetRoute = when {
            !isLoggedIn -> Route.Login.route
            !isOnboarded -> Route.Onboarding.route
            else -> Route.Main.route
        }

        navController.navigate(targetRoute) {
            popUpTo(0) { inclusive = true } // 백스택 전부 제거
        }
    }
}