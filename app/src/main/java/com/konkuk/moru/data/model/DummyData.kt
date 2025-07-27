package com.konkuk.moru.data.model

import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo
import com.konkuk.moru.presentation.routinefeed.data.User
import com.konkuk.moru.presentation.routinefeed.screen.follow.FollowRelation
import java.time.DayOfWeek
import java.time.LocalTime
object DummyData {
    const val MY_USER_ID = 252 // 내 유저 아이디를 252으로 가정

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

    val dummyUsers = listOf(
        User(
            1,
            "운동하는 제니",
            " 꾸준함이 답이다! 매일 아침 운동 기록",
            "https://images.unsplash.com/photo-1580489944761-15a19d654956"
        ),
        User(
            2,
            "책읽는 라이언",
            "마음의 양식을 쌓는 중. 한 달에 2권 읽기 목표",
            "https://images.unsplash.com/photo-1548142813-c348350df52b"
        ),
        User(3, "개발자 모루", "코드로 세상을 이롭게 하고 싶은 개발자입니다.", null),
        User(
            4,
            "요리왕 준",
            "오늘은 내가 요리사! #집밥 #레시피",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d"
        ),
        User(
            101,
            "요가마스터",
            "몸과 마음의 연결, 요가로 찾으세요.",
            "https://images.unsplash.com/photo-1552058544-f2b08422138a"
        ),
        User(
            102,
            "개발왕",
            "1일 1커밋. #TIL #오픈소스",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d"
        ),
        User(
            201,
            "지하철독서왕",
            "이동 시간을 황금으로 만드는 법",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d"
        ),
        User(MY_USER_ID, "MORU (나)", "이 앱의 주인공입니다. 루틴을 만들어봐요!", null)
    )

    val dummyLiveUsers = listOf(
        LiveUserInfo(
            1,
            "운동하는 제니",
            "#오운완",
            "https://images.unsplash.com/photo-1580489944761-15a19d654956"
        ),
        LiveUserInfo(
            2,
            "책읽는 라이언",
            "#북스타그램",
            "https://images.unsplash.com/photo-1548142813-c348350df52b"
        ),
        LiveUserInfo(3, "개발자 모루", "#TIL", null),
        LiveUserInfo(
            4,
            "요리왕 준",
            "#집밥",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d"
        ),
        LiveUserInfo(
            101,
            "요가마스터",
            "#요가",
            "https://images.unsplash.com/photo-1552058544-f2b08422138a"
        ),
        LiveUserInfo(
            102,
            "개발왕",
            "#코딩",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d"
        )
    )


    // 앱 전체의 루틴을 관리하는 단일 소스. 수정 가능하도록 var와 MutableList 사용
    var feedRoutines: MutableList<Routine> = mutableListOf(
        // --- '나'의 루틴 (MY_USER_ID) ---
        Routine(
            routineId = 501,
            title = "MORU의 아침 명상",
            description = "상쾌한 아침을 여는 5분 명상 루틴입니다.",
            imageUrl = "https://images.unsplash.com/photo-1506126613408-4e0e0f7c50e1",
            category = "간편", // ✨ 수정
            tags = listOf("명상", "아침루틴", "집중", "운동"),
            authorId = MY_USER_ID,
            authorName = "MORU (나)",
            authorProfileUrl = null,
            likes = 42,
            isLiked = true, isBookmarked = true, isRunning = false,
            scheduledDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            steps = listOf(RoutineStep("편안하게 앉기", "00:30"), RoutineStep("호흡에 집중하기", "03:00")),
            usedApps = listOf(
                // ✨ 수정: R.drawable -> 실제 이미지 URL
                AppInfo("kakao", "https://uxwing.com/wp-content/themes/uxwing/download/brands-and-social-media/kakaotalk-icon.png"),
                AppInfo("Youtube", "https://uxwing.com/wp-content/themes/uxwing/download/brands-and-social-media/youtube-logotype-icon.png")
            )
        ),
        Routine(
            routineId = 502,
            title = "MORU의 주말 대청소",
            description = "개운하게 주말을 맞이하는 청소 루틴!",
            imageUrl = "https://images.unsplash.com/photo-1585421943279-25f1712ba7a8",
            category = "집중", // ✨ 수정
            tags = listOf("청소", "주말", "정리"),
            authorId = MY_USER_ID,
            authorName = "MORU (나)",
            authorProfileUrl = null,
            likes = 15,
            isLiked = false, isBookmarked = true, isRunning = true,
            scheduledDays = setOf(DayOfWeek.SATURDAY),
            steps = listOf(RoutineStep("환기하기", "05:00"), RoutineStep("먼지 털기", "15:00")),
            usedApps = listOf(
                // ✨ 수정: R.drawable -> 실제 이미지 URL
                AppInfo("정리 앱", "https://uxwing.com/wp-content/themes/uxwing/download/house-and-home/dustpan-icon.png")
            )
        ),
        // ✨ [추가] '나'의 루틴 2개 추가
        Routine(
            routineId = 503,
            title = "MORU의 저녁 스트레칭",
            description = "하루의 피로를 푸는 간단한 스트레칭.",
            imageUrl = "https://images.unsplash.com/photo-1599901860904-17e6ed7083a0",
            category = "간편",
            tags = listOf("스트레칭", "저녁루틴", "건강"),
            authorId = MY_USER_ID,
            authorName = "MORU (나)",
            authorProfileUrl = null,
            likes = 28,
            isLiked = true, isBookmarked = false, isRunning = false,
            scheduledDays = setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY),
            steps = listOf(RoutineStep("목 스트레칭", "02:00"), RoutineStep("어깨 돌리기", "02:00"))
        ),
        Routine(
            routineId = 504,
            title = "MORU의 집중 코딩 타임",
            description = "방해 없이 2시간 동안 코딩에 집중하는 시간.",
            imageUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97",
            category = "집중",
            tags = listOf("코딩", "개발", "집중"),
            authorId = MY_USER_ID,
            authorName = "MORU (나)",
            authorProfileUrl = null,
            likes = 55,
            isLiked = false, isBookmarked = true, isRunning = false,
            scheduledTime = LocalTime.of(20, 0),
            steps = listOf(RoutineStep("작업 환경 설정", "05:00"), RoutineStep("집중 코딩", "120:00")),
            usedApps = listOf(
                // ✨ 수정: R.drawable -> 실제 이미지 URL
                AppInfo("Github", "https://uxwing.com/wp-content/themes/uxwing/download/brands-and-social-media/github-icon.png")
            )
        ),

        // --- 다른 사용자의 루틴 ---
        Routine(
            routineId = 1,
            title = "아침 10분 요가",
            description = "간단한 요가로 하루를 시작해요. 몸과 마음을 깨우는 시간을 가져보세요.",
            imageUrl = "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?q=80&w=2120&auto=format&fit=crop",
            category = "간편", // ✨ 수정
            tags = listOf("건강", "요가", "아침루틴", "운동", "명상"),
            authorId = 101,
            authorName = "요가마스터",
            authorProfileUrl = "https://images.unsplash.com/photo-1552058544-f2b08422138a",
            likes = 112,
            isLiked = true, isBookmarked = false, isRunning = false,
            steps = listOf(RoutineStep("매트 준비", "00:30"), RoutineStep("고양이 자세", "02:00"))
        ),
        Routine(
            routineId = 2,
            title = "매일 TIL 작성하기",
            description = "개발 지식을 매일 기록합니다. 꾸준함이 실력!",
            imageUrl = "https://images.unsplash.com/photo-1498050108023-c5249f4df085?q=80&w=2072&auto=format&fit=crop",
            category = "집중", // ✨ 수정
            tags = listOf("개발", "TIL"),
            authorId = 102,
            authorName = "개발왕",
            authorProfileUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            likes = 98,
            isLiked = false, isBookmarked = false, isRunning = false,
            steps = listOf(RoutineStep("어제 배운 내용 복습", "10:00"), RoutineStep("블로그에 TIL 작성", "45:00"))
        ),
        Routine(
            routineId = 20,
            title = "출근길 지하철 독서",
            description = "이동 시간을 활용한 독서",
            imageUrl = "https://images.unsplash.com/photo-1481627834876-b7833e8f5570",
            category = "간편", // ✨ 수정
            tags = listOf("독서", "지하철", "자기계발"),
            authorId = 201,
            authorName = "지하철독서왕",
            authorProfileUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            likes = 68,
            isLiked = true, isBookmarked = false, isRunning = false
        ),
        Routine(
            routineId = 31,
            title = "요가와 명상의 조화",
            description = "정적인 움직임 속의 평화",
            imageUrl = "https://images.unsplash.com/photo-1506126613408-4e0e0f7c50e1",
            category = "집중", // ✨ 수정
            tags = listOf("운동", "명상", "요가"),
            authorId = 101,
            authorName = "요가마스터",
            authorProfileUrl = null,
            likes = 88,
            isLiked = false, isBookmarked = true, isRunning = false
        ),
        Routine(
            routineId = 10,
            title = "MORU 맞춤 루틴 1: 독서",
            description = "당신을 위한 맞춤 루틴입니다.",
            imageUrl = "https://images.unsplash.com/photo-1532012197267-da84d127e765?q=80&w=1887&auto=format&fit=crop",
            category = "간편", // ✨ 수정
            tags = listOf("독서", "MORU"),
            authorId = 3,
            authorName = "MORU",
            authorProfileUrl = null,
            likes = 25,
            isLiked = false, isBookmarked = true, isRunning = false
        )
    )
}

/**
 * 전체 루틴 목록에서 주어진 루틴과 비슷한 태그를 가진 다른 루틴들을 찾아 반환합니다.
 */
fun findSimilarRoutinesByTags(
    targetRoutine: Routine,
    allRoutines: List<Routine>,
    limit: Int = 5
): List<SimilarRoutine> {
    val targetTags = targetRoutine.tags.toSet()

    return allRoutines
        .filter { it.routineId != targetRoutine.routineId }
        .filter { routine -> routine.tags.any { tag -> tag in targetTags } }
        .take(limit)
        .map {
            SimilarRoutine(
                imageUrl = it.imageUrl,
                name = it.title,
                tag = "#${it.tags.firstOrNull() ?: "루틴"}"
            )
        }
}