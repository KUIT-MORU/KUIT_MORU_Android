package com.konkuk.moru.presentation.routinefeed.data


    // 화면 전체 데이터를 담는 클래스
    data class RoutineDetail(
        val imageUrl: String?, // 상단 이미지 URL (없을 수 있음)
        val authorName: String,
        val authorProfileUrl: String?,
        val routineTitle: String,
        val routineCategory: String, // "집중"과 같은 카테고리
        val routineDescription: String,
        val tags: List<String>,
        var likeCount: Int,
        var isLiked: Boolean,
        var isBookmarked: Boolean,
        val steps: List<RoutineStep>, // 활동 단계 목록
        val similarRoutines: List<SimilarRoutine> // 비슷한 루틴 목록
    )

    // 각 활동 단계를 나타내는 클래스
    data class RoutineStep(
       // val stepNumber: Int,
        val name: String,
        val duration: String // "00:00" 형식의 시간
    )

    // 비슷한 루틴 정보를 나타내는 클래스
    data class SimilarRoutine(
        val imageUrl: String?,
        val name: String,
        val tag: String
    )
