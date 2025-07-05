package com.konkuk.moru.presentation.routinefeed.data

data class RoutineSectionModel(
    val title: String,
    val routines: List<RoutineInfo>
)


/**
 * 제목이 있는 루틴 목록 섹션 모델
 * @param title 섹션 제목
 * @param routines 해당 섹션에 표시될 루틴 목록
 */
