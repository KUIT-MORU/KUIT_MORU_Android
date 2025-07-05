package com.konkuk.moru.presentation.routinefeed.data

import androidx.annotation.DrawableRes

/**
 * MORU LIVE 섹션의 사용자 정보 모델
 * @param id 사용자 고유 ID
 * @param name 사용자 이름
 * @param tag 대표 태그
 * @param profileImageRes 사용자 프로필 이미지의 Drawable 리소스 ID (실제 앱에서는 URL이 될 수 있음)
 */
data class LiveUserInfo(
    val id: Int,
    val name: String,
    val tag: String,
    @DrawableRes val profileImageRes: Int
)