package com.konkuk.moru.data.mapper

import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.presentation.routinefeed.data.RoutineInfo

 fun RoutineInfo.toRoutineModel(): Routine {
    // 서버 응답에는 없지만 UI 모델에는 필요한 정보들을 기본값으로 채워줍니다.
    return Routine(
        // === 서버 응답과 매핑되는 정보 ===
        routineId = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        tags = this.tags,
        likes = this.likeCount,
        isRunning = this.isRunning,
        // === 서버 응답에 없어서 기본값으로 채워야 하는 정보 ===
        description = "Description not available", // 상세 정보는 상세 API에서 별도 요청 필요
        category = "일상", // 카테고리 정보가 없으므로 기본값 설정
        authorId = "", // 작성자 ID 정보 없음
        authorName = "Unknown", // 작성자 이름 정보 없음
        authorProfileUrl = null, // 작성자 프로필 정보 없음
        isLiked = false, // '좋아요' 상태는 보통 사용자 로컬 DB나 별도 상태로 관리
        isBookmarked = false, // '북마크' 상태도 위와 동일
        isChecked = false, // 삭제 모드에서만 사용되므로 false
        scheduledTime = null, // 내 루틴이 아니므로 null
        scheduledDays = emptySet(), // 내 루틴이 아니므로 empty
        isAlarmEnabled = false, // 내 루틴이 아니므로 false
        steps = emptyList(), // 상세 정보이므로 empty
        similarRoutines = emptyList(), // 상세 정보이므로 empty
        usedApps = emptyList() // 상세 정보이므로 empty
    )
}