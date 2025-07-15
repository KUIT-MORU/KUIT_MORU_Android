
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.model.RoutineStep
import com.konkuk.moru.data.model.SimilarRoutine
import java.time.DayOfWeek

object DummyData {

    val dummyRoutines = listOf(
        // "지금 가장 핫한 루틴은?" (likes > 70)
        Routine(
            id = 1, title = "아침 10분 요가", description = "간단한 요가로 하루를 시작해요. 몸과 마음을 깨우는 시간을 가져보세요.",
            imageUrl = "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?q=80&w=2120&auto=format&fit=crop", category = "건강", tags = listOf("건강", "요가"),
            authorName = "요가마스터", authorProfileUrl = "https://images.unsplash.com/photo-1552058544-f2b08422138a", likes = 112, isLiked = true, isBookmarked = false, isRunning = false,
            scheduledDays = setOf(DayOfWeek.MONDAY),
            steps = listOf(RoutineStep("매트 준비", "00:30"), RoutineStep("고양이 자세", "02:00"), RoutineStep("다운독 자세", "02:00"), RoutineStep("전사 자세", "03:00"), RoutineStep("사바사나 (휴식)", "02:30")),
            similarRoutines = listOf(SimilarRoutine("https://images.unsplash.com/photo-1506126613408-4e0e0f7c50e1", "저녁 명상", "#명상"), SimilarRoutine("https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b", "코어 운동", "#운동"))
        ),
        Routine(
            id = 2, title = "매일 TIL 작성하기", description = "개발 지식을 매일 기록합니다. 꾸준함이 실력!",
            imageUrl = "https://images.unsplash.com/photo-1498050108023-c5249f4df085?q=80&w=2072&auto=format&fit=crop", category = "개발", tags = listOf("개발", "TIL"),
            authorName = "개발왕", authorProfileUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d", likes = 98, isLiked = false, isBookmarked = true, isRunning = false,
            scheduledDays = setOf(DayOfWeek.TUESDAY),
            steps = listOf(RoutineStep("어제 배운 내용 복습", "10:00"), RoutineStep("오늘의 학습 목표 설정", "05:00"), RoutineStep("블로그에 TIL 작성", "45:00")),
            similarRoutines = listOf(SimilarRoutine(null, "사이드 프로젝트", "#개발"), SimilarRoutine(null, "알고리즘 문제 풀기", "#코딩테스트"))
        ),
        Routine(
            id = 3, title = "점심시간 산책", description = "식사 후 가벼운 산책으로 오후를 상쾌하게!",
            imageUrl = "https://images.unsplash.com/photo-1508672019048-805c876b67e2?q=80&w=2070&auto=format&fit=crop", category = "운동", tags = listOf("운동", "산책"),
            authorName = "산책러", authorProfileUrl = "https://images.unsplash.com/photo-1548142813-c348350df52b", likes = 76, isLiked = true, isBookmarked = false, isRunning = true,
            scheduledDays = setOf(DayOfWeek.WEDNESDAY),
            steps = listOf(RoutineStep("스트레칭", "02:00"), RoutineStep("공원 걷기", "15:00"), RoutineStep("가볍게 뛰기", "05:00")),
            similarRoutines = emptyList()
        ),
        Routine(
            id = 4, title = "하루 30분 책읽기", description = "마음의 양식을 쌓는 시간",
            imageUrl = "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?q=80&w=2070&auto=format&fit=crop", category = "독서", tags = listOf("독서", "습관"),
            authorName = "북웜", authorProfileUrl = "https://images.unsplash.com/photo-1580489944761-15a19d654956", likes = 85, isLiked = false, isBookmarked = true, isRunning = false,
            scheduledDays = setOf(DayOfWeek.THURSDAY)
        ),
        Routine(
            id = 35, title = "자기 전 스트레칭과 수면 명상", description = "숙면을 위한 최고의 조합",
            imageUrl = "https://images.unsplash.com/photo-1512433445585-72243d372138?q=80&w=1887&auto=format&fit=crop", category = "운동", tags = listOf("운동", "명상", "수면", "스트레칭"),
            authorName = "꿀잠요정", authorProfileUrl = null, likes = 95, isLiked = false, isBookmarked = true, isRunning = false,
            scheduledDays = setOf(DayOfWeek.FRIDAY)
        ),

        // "MORU님과 딱 맞는 루틴" (authorName == "MORU")
        Routine(10, "MORU 맞춤 루틴 1: 독서", "당신을 위한 맞춤 루틴입니다.", "https://images.unsplash.com/photo-1532012197267-da84d127e765?q=80&w=1887&auto=format&fit=crop", "독서", listOf("독서"), "MORU", null, 25, false, true, false, scheduledDays = setOf(DayOfWeek.SATURDAY)),
        Routine(11, "MORU 맞춤 루틴 2: 운동", "당신을 위한 맞춤 루틴입니다.", "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?q=80&w=2070&auto=format&fit=crop", "운동", listOf("운동"), "MORU", null, 33, true, false, false, scheduledDays = setOf(DayOfWeek.MONDAY)),
        Routine(12, "MORU 맞춤 루틴 3: 명상", "당신을 위한 맞춤 루틴입니다.", "https://images.unsplash.com/photo-1593811167563-9d312357283d?q=80&w=1887&auto=format&fit=crop", "자기계발", listOf("명상", "아침"), "MORU", null, 41, false, false, false, scheduledDays = setOf(DayOfWeek.TUESDAY)),
        Routine(13, "MORU 맞춤 루틴 4: 영어 공부", "당신을 위한 맞춤 루틴입니다.", "https://images.unsplash.com/photo-1455860922589-96865239906b?q=80&w=2070&auto=format&fit=crop", "학습", listOf("학습", "외국어"), "MORU", null, 29, false, true, false, scheduledDays = setOf(DayOfWeek.WEDNESDAY)),
        Routine(14, "MORU 맞춤 루틴 5: 물 마시기", "당신을 위한 맞춤 루틴입니다.", "https://images.unsplash.com/photo-1541533267753-bab141444692?q=80&w=1887&auto=format&fit=crop", "건강", listOf("건강", "습관"), "MORU", null, 50, true, false, false, scheduledDays = setOf(DayOfWeek.THURSDAY)),
        Routine(15, "MORU 맞춤 루틴 6: 블로그 글쓰기", "당신을 위한 맞춤 루틴입니다.", "https://images.unsplash.com/photo-1499750310107-5fef28a66643?q=80&w=2070&auto=format&fit=crop", "개발", listOf("개발", "글쓰기"), "MORU", null, 38, false, false, false, scheduledDays = setOf(DayOfWeek.FRIDAY)),

        // "#지하철#독서" (tags.containsAll(listOf("지하철", "독서")))
        Routine(20, "출근길 지하철 독서", "이동 시간을 활용한 독서", null, "독서", listOf("독서", "지하철", "자기계발"), "지하철독서왕", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d", 68, true, false, false, scheduledDays = setOf(DayOfWeek.SATURDAY)),
        Routine(21, "퇴근길 웹소설 읽기", "하루의 마무리는 웹소설", null, "독서", listOf("독서", "지하철", "취미"), "웹소설매니아", null, 55, false, true, false, scheduledDays = setOf(DayOfWeek.MONDAY)),
        Routine(22, "지하철에서 기술 서적 읽기", "틈새 시간을 이용한 공부", null, "독서", listOf("독서", "지하철", "개발"), "공부하는개발자", null, 71, false, false, false, scheduledDays = setOf(DayOfWeek.TUESDAY)),
        Routine(23, "지하철 오디오북 듣기", "눈이 피곤할 땐 오디오북", null, "독서", listOf("독서", "지하철", "오디오북"), "지하철독서왕", null, 49, true, true, false, scheduledDays = setOf(DayOfWeek.WEDNESDAY)),
        Routine(24, "지하철 시집 읽기", "짧은 시간, 깊은 감동", null, "독서", listOf("독서", "지하철", "시"), "감성시인", null, 34, false, false, false, scheduledDays = setOf(DayOfWeek.THURSDAY)),
        Routine(25, "지하철 신문 기사 읽기", "세상 돌아가는 소식", null, "독서", listOf("독서", "지하철", "뉴스"), "뉴스캐스터", null, 45, true, false, false, scheduledDays = setOf(DayOfWeek.FRIDAY)),
        Routine(26, "지하철에서 전공 서적 예습", "수업 전 미리보는 전공", null, "독서", listOf("독서", "지하철", "전공"), "과탑", null, 62, false, true, false, scheduledDays = setOf(DayOfWeek.SATURDAY)),

        // "#운동#명상" (tags.containsAll(listOf("운동", "명상")))
        Routine(30, "운동 후 5분 명상", "몸과 마음을 함께 단련", null, "운동", listOf("운동", "명상", "건강"), "헬창", "https://images.unsplash.com/photo-1548142813-c348350df52b", 91, true, false, false, scheduledDays = setOf(DayOfWeek.MONDAY)),
        Routine(31, "요가와 명상의 조화", "정적인 움직임 속의 평화", null, "운동", listOf("운동", "명상", "요가"), "요가마스터", null, 88, false, true, false, scheduledDays = setOf(DayOfWeek.TUESDAY)),
        Routine(32, "아침 조깅 후 마무리 명상", "상쾌한 하루의 시작", null, "운동", listOf("운동", "명상", "아침"), "아침형인간", null, 79, true, true, false, scheduledDays = setOf(DayOfWeek.WEDNESDAY)),
        Routine(33, "격렬한 운동 전 마인드셋 명상", "부상 방지와 집중력 향상", null, "운동", listOf("운동", "명상", "헬스"), "헬창", null, 67, false, false, false, scheduledDays = setOf(DayOfWeek.THURSDAY)),
        Routine(34, "필라테스 후 호흡 명상", "몸의 정렬과 마음의 안정", null, "운동", listOf("운동", "명상", "필라테스"), "필라테스강사", null, 82, true, false, false, scheduledDays = setOf(DayOfWeek.FRIDAY))
    )
}