package com.konkuk.moru.data.model

import java.util.UUID

data class Step(
    val id: String = UUID.randomUUID().toString(), // 고유 ID 생성
    val title: String,
    val time: String
)