package com.konkuk.moru.presentation.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.konkuk.moru.presentation.onboarding.component.OnboardingPage

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val currentPage by viewModel.currentPage.collectAsState()

    // 온보딩 완료 시 콜백 호출
    val isOnboardingComplete by viewModel.isOnboardingComplete.collectAsState()
    LaunchedEffect(isOnboardingComplete) {
        if (isOnboardingComplete) {
            onFinish()
        }
    }

    LaunchedEffect(currentPage) {
        pagerState.animateScrollToPage(currentPage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HorizontalPager(
            count = OnboardingViewModel.LAST_PAGE_INDEX + 1,
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            userScrollEnabled = false // 스와이프 금지, 버튼으로만 이동
        ) { page ->
            OnboardingPage(page = page)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 인디케이터
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (currentPage < OnboardingViewModel.LAST_PAGE_INDEX) {
                    viewModel.nextPage()
                } else {
                    viewModel.nextPage() // 마지막 페이지 → completeOnboarding()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (currentPage < OnboardingViewModel.LAST_PAGE_INDEX) "다음" else "앱 시작하기"
            )
        }
    }
}