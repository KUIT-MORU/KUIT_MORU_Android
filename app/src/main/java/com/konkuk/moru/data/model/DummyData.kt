package com.konkuk.moru.data.model // 실제 프로젝트 경로에 맞게 확인해주세요.

import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo
import com.konkuk.moru.presentation.routinefeed.screen.follow.FollowRelation
import java.time.DayOfWeek

object DummyData {

    val dummyFollowRelations = listOf(
        // 1번 유저(제니)가 다른 사람들을 팔로우
        FollowRelation(followerId = 1, followingId = 3),   // 제니 -> 모루
        FollowRelation(followerId = 1, followingId = 101), // 제니 -> 요가마스터
        FollowRelation(followerId = 1, followingId = 102), // 제니 -> 개발왕

        // 3번 유저(모루)가 다른 사람들을 팔로우
        FollowRelation(followerId = 3, followingId = 1),   // 모루 -> 제니
        FollowRelation(followerId = 3, followingId = 102), // 모루 -> 개발왕

        // 다른 유저들이 101번 유저(요가마스터)를 팔로우
        FollowRelation(followerId = 2, followingId = 101), // 라이언 -> 요가마스터
        FollowRelation(followerId = 3, followingId = 101), // 모루 -> 요가마스터
        FollowRelation(followerId = 4, followingId = 101), // 준 -> 요가마스터
    )
    val dummyLiveUsers = listOf(
        LiveUserInfo(1, "운동하는 제니", "#오운완", "https://images.unsplash.com/photo-1580489944761-15a19d654956"),
        LiveUserInfo(2, "책읽는 라이언", "#북스타그램", "https://images.unsplash.com/photo-1548142813-c348350df52b"),
        LiveUserInfo(3, "개발자 모루", "#TIL", null), // MORU는 프로필 이미지가 없는 것으로 설정
        LiveUserInfo(4, "요리왕 준", "#집밥", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d"),
        LiveUserInfo(5, "여행가 에밀리", "#여행에미치다", "https://images.unsplash.com/photo-1438761681033-6461ffad8d80"),
        LiveUserInfo(6, "명상하는 소피아", "#마음챙김", "https://images.unsplash.com/photo-1552058544-f2b08422138a"),
        LiveUserInfo(7, "기상인증 챌린저", "#미라클모닝", "https://images.unsplash.com/photo-1542909168-82c3e7fdca5c"),
        LiveUserInfo(8, "기타치는 브라운", "#음악", "https://images.unsplash.com/photo-1541533267753-bab141444692"),

        // --- 루틴 작성자들도 동일하게 수정 ---
        LiveUserInfo(101, "요가마스터", "#요가", "https://images.unsplash.com/photo-1552058544-f2b08422138a"),
        LiveUserInfo(102, "개발왕", "#코딩", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d"),
        LiveUserInfo(103, "산책러", "#산책", "https://images.unsplash.com/photo-1548142813-c348350df52b"),
        LiveUserInfo(104, "북웜", "#독서", "https://images.unsplash.com/photo-1580489944761-15a19d654956"),
        LiveUserInfo(105, "꿀잠요정", "#숙면", null),
        LiveUserInfo(201, "지하철독서왕", "#지하철", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d"),
        LiveUserInfo(202, "웹소설매니아", "#웹소설", null),
        LiveUserInfo(301, "헬창", "#오운완", "https://images.unsplash.com/photo-1548142813-c348350df52b")
    )
    val dummyRoutines = listOf(
        Routine(
            routineId = 1,
            title = "아침 10분 요가",
            description = "간단한 요가로 하루를 시작해요. 몸과 마음을 깨우는 시간을 가져보세요.",
            imageUrl = "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?q=80&w=2120&auto=format&fit=crop",
            category = "건강",
            tags = listOf("건강", "요가"),
            authorId = 101,
            authorName = "요가마스터",
            authorProfileUrl = "https://images.unsplash.com/photo-1552058544-f2b08422138a",
            likes = 112,
            isLiked = true,
            isBookmarked = false,
            isRunning = false,
            scheduledDays = setOf(DayOfWeek.MONDAY),
            steps = listOf(
                RoutineStep("매트 준비", "00:30"),
                RoutineStep("고양이 자세", "02:00"),
                RoutineStep("다운독 자세", "02:00"),
                RoutineStep("전사 자세", "03:00"),
                RoutineStep("사바사나 (휴식)", "02:30")
            ),
            similarRoutines = listOf(
                SimilarRoutine(
                    "https://images.unsplash.com/photo-1506126613408-4e0e0f7c50e1",
                    "저녁 명상",
                    "#명상"
                ),
                SimilarRoutine(
                    "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b",
                    "코어 운동",
                    "#운동"
                )
            )
        ),
        Routine(
            routineId = 2,
            title = "매일 TIL 작성하기",
            description = "개발 지식을 매일 기록합니다. 꾸준함이 실력!",
            imageUrl = "https://images.unsplash.com/photo-1498050108023-c5249f4df085?q=80&w=2072&auto=format&fit=crop",
            category = "개발",
            tags = listOf("개발", "TIL"),
            authorId = 102,
            authorName = "개발왕",
            authorProfileUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            likes = 98,
            isLiked = false,
            isBookmarked = true,
            isRunning = false,
            scheduledDays = setOf(DayOfWeek.TUESDAY),
            steps = listOf(
                RoutineStep("어제 배운 내용 복습", "10:00"),
                RoutineStep("오늘의 학습 목표 설정", "05:00"),
                RoutineStep("블로그에 TIL 작성", "45:00")
            ),
            similarRoutines = listOf(
                SimilarRoutine(null, "사이드 프로젝트", "#개발"),
                SimilarRoutine(null, "알고리즘 문제 풀기", "#코딩테스트")
            )
        ),
        Routine(
            routineId = 3,
            title = "점심시간 산책",
            description = "식사 후 가벼운 산책으로 오후를 상쾌하게!",
            imageUrl = "https://images.unsplash.com/photo-1508672019048-805c876b67e2?q=80&w=2070&auto=format&fit=crop",
            category = "운동",
            tags = listOf("운동", "산책"),
            authorId = 103,
            authorName = "산책러",
            authorProfileUrl = "https://images.unsplash.com/photo-1548142813-c348350df52b",
            likes = 76,
            isLiked = true,
            isBookmarked = false,
            isRunning = true,
            scheduledDays = setOf(DayOfWeek.WEDNESDAY),
            steps = listOf(
                RoutineStep("스트레칭", "02:00"),
                RoutineStep("공원 걷기", "15:00"),
                RoutineStep("가볍게 뛰기", "05:00")
            ),
            similarRoutines = emptyList()
        ),
        Routine(
            routineId = 4,
            title = "하루 30분 책읽기",
            description = "마음의 양식을 쌓는 시간",
            imageUrl = "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?q=80&w=2070&auto=format&fit=crop",
            category = "독서",
            tags = listOf("독서", "습관"),
            authorId = 104,
            authorName = "북웜",
            authorProfileUrl = "https://images.unsplash.com/photo-1580489944761-15a19d654956",
            likes = 85,
            isLiked = false,
            isBookmarked = true,
            isRunning = false,
            scheduledDays = setOf(DayOfWeek.THURSDAY)
        ),
        Routine(
            routineId = 35,
            title = "자기 전 스트레칭과 수면 명상",
            description = "숙면을 위한 최고의 조합",
            imageUrl = "https://images.unsplash.com/photo-1512433445585-72243d372138?q=80&w=1887&auto=format&fit=crop",
            category = "운동",
            tags = listOf("운동", "명상", "수면", "스트레칭"),
            authorId = 105,
            authorName = "꿀잠요정",
            authorProfileUrl = null,
            likes = 95,
            isLiked = false,
            isBookmarked = true,
            isRunning = false,
            scheduledDays = setOf(DayOfWeek.FRIDAY)
        ),
        // [수정] 아래 모든 Routine 생성자를 Named Arguments 방식으로 변경
        Routine(
            routineId = 10,
            title = "MORU 맞춤 루틴 1: 독서",
            description = "당신을 위한 맞춤 루틴입니다.",
            imageUrl = "https://images.unsplash.com/photo-1532012197267-da84d127e765?q=80&w=1887&auto=format&fit=crop",
            category = "독서",
            tags = listOf("독서"),
            authorId = 3,
            authorName = "MORU",
            authorProfileUrl = null,
            likes = 25,
            isLiked = false,
            isBookmarked = true,
            isRunning = false,
            scheduledDays = setOf(DayOfWeek.SATURDAY)
        ),
        Routine(
            routineId = 11,
            title = "MORU 맞춤 루틴 2: 운동",
            description = "당신을 위한 맞춤 루틴입니다.",
            imageUrl = "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?q=80&w=2070&auto=format&fit=crop",
            category = "운동",
            tags = listOf("운동"),
            authorId = 3,
            authorName = "MORU",
            authorProfileUrl = null,
            likes = 33,
            isLiked = true,
            isBookmarked = false,
            isRunning = false,
            scheduledDays = setOf(DayOfWeek.MONDAY)
        ),
        Routine(
            routineId = 20,
            title = "출근길 지하철 독서",
            description = "이동 시간을 활용한 독서",
            imageUrl = null,
            category = "독서",
            tags = listOf("독서", "지하철", "자기계발"),
            authorId = 201,
            authorName = "지하철독서왕",
            authorProfileUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            likes = 68,
            isLiked = true,
            isBookmarked = false,
            isRunning = false,
            scheduledDays = setOf(DayOfWeek.SATURDAY)
        ),
        Routine(
            routineId = 21,
            title = "퇴근길 웹소설 읽기",
            description = "하루의 마무리는 웹소설",
            imageUrl = null,
            category = "독서",
            tags = listOf("독서", "지하철", "취미"),
            authorId = 202,
            authorName = "웹소설매니아",
            authorProfileUrl = null,
            likes = 55,
            isLiked = false,
            isBookmarked = true,
            isRunning = false,
            scheduledDays = setOf(DayOfWeek.MONDAY)
        ),
        Routine(
            routineId = 30,
            title = "운동 후 5분 명상",
            description = "몸과 마음을 함께 단련",
            imageUrl = null,
            category = "운동",
            tags = listOf("운동", "명상", "건강"),
            authorId = 301,
            authorName = "헬창",
            authorProfileUrl = "https://images.unsplash.com/photo-1548142813-c348350df52b",
            likes = 91,
            isLiked = true,
            isBookmarked = false,
            isRunning = false,
            scheduledDays = setOf(DayOfWeek.MONDAY)
        ),
        Routine(
            routineId = 31,
            title = "요가와 명상의 조화",
            description = "정적인 움직임 속의 평화",
            imageUrl = null,
            category = "운동",
            tags = listOf("운동", "명상", "요가"),
            authorId = 101,
            authorName = "요가마스터",
            authorProfileUrl = null,
            likes = 88,
            isLiked = false,
            isBookmarked = true,
            isRunning = false,
            scheduledDays = setOf(DayOfWeek.TUESDAY)
        )
    )
}