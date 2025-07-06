package com.konkuk.moru.presentation.routinefeed.data

import java.time.LocalDateTime

// 실제 앱에서는 API 명세에 맞춰 수정해야 합니다. (예: @Serializable 어노테이션 추가 등)
data class Notification(
    val id: Int,
    val userImageUrl: String?,
    val actingUser: String, // 행동을 한 사용자 (예: "xx님")
    val messageAction: String, // 행동 (예: "을 팔로우 했습니다.", "[루틴명]을 생성했습니다.")
    val targetName: String?, // 행동의 대상 (예: "사용자명") -> 대상이 없는 경우도 있으므로 Nullable
    val timestamp: LocalDateTime
)