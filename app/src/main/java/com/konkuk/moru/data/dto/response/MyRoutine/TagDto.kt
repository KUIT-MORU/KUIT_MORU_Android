package com.konkuk.moru.data.dto.response.MyRoutine

data class TagDto(
    val id: Int,
    val name: String,
    val createdAt: String? = null // 스웨거 스펙 포함(옵션)
)