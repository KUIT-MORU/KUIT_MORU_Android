package com.konkuk.moru.presentation.onboarding.component

import androidx.compose.runtime.Composable
import com.konkuk.moru.presentation.onboarding.page.FinalPage
import com.konkuk.moru.presentation.onboarding.page.InfoPage1
import com.konkuk.moru.presentation.onboarding.page.InfoPage2
import com.konkuk.moru.presentation.onboarding.page.InfoPage3
import com.konkuk.moru.presentation.onboarding.page.PermissionPage
import com.konkuk.moru.presentation.onboarding.page.TagSelectionPage
import com.konkuk.moru.presentation.onboarding.page.UserInfoPage

@Composable
fun OnboardingPage(page: Int, onNext: () -> Unit) {
    when (page) {
        0 -> UserInfoPage(onNext = onNext)
        1 -> InfoPage1()
        2 -> TagSelectionPage()
        3 -> InfoPage2()
        4 -> PermissionPage()
        5 -> InfoPage3()
        6 -> FinalPage()
    }
}