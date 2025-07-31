package com.konkuk.moru.data.model

data class TagDto(
    val id: Int,
    val name: String,
    val isSelected: Boolean = false
)