package com.konkuk.moru.presentation.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.konkuk.moru.presentation.onboarding.component.OnboardingPage

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(), //TODO: 추후 주석 해제 예정
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val currentPage by viewModel.currentPage.collectAsState()
    //val currentPage = 1 // 프리뷰 보기 위해 임시로 1로 설정. TODO: 추후 삭제 예정

    // 온보딩 완료 시 콜백 호출
    //val isOnboardingComplete by viewModel.isOnboardingComplete.collectAsState()
    val isOnboardingComplete = false // 프리뷰 보기 위해 임시로 false로 설정. TODO: 추후 삭제 예정
    LaunchedEffect(isOnboardingComplete) {
        if (isOnboardingComplete) {
            onFinish()
        }
    }

    LaunchedEffect(currentPage) {
        pagerState.animateScrollToPage(currentPage)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            count = OnboardingViewModel.LAST_PAGE_INDEX + 1,
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            userScrollEnabled = false // 스와이프 금지, 버튼으로만 이동
        ) { page ->
            OnboardingPage(page = page, onNext = {viewModel.nextPage()})
        }
    }
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    //OnboardingScreen {  }
}