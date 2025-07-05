package com.konkuk.moru.presentation.routinefeed.data


data class RoutineInfo(
    val id: Int,
    val name: String,
    val tag: String,
    val likes: Int,
    val isRunning: Boolean,
    var isLiked: Boolean
)

/**
 * 루틴 카드 정보 모델
 * @param id 루틴 고유 ID
 * @param name 루틴 이름
 * @param tag 대표 태그
 * @param likes 좋아요 수
 * @param isRunning 현재 진행중 여부
 * @param isLiked 내가 좋아요를 눌렀는지 여부
 */

