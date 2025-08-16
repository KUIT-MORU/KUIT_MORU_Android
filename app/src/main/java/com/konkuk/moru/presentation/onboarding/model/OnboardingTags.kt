package com.konkuk.moru.presentation.onboarding.model

object OnboardingTags {
    data class TagDef(val id: String, val label: String)

    // 상황 태그(ob_rou_tag1)
    val SITUATION = listOf(
        TagDef("6e78b13a-4282-41f4-bb8e-df8ca3baaa85", "#출근길"),
        TagDef("ded087ad-fc9e-4a70-b016-7f47d7a05e8f", "#지하철"),
        TagDef("10d0e7c9-8179-4a48-b101-5376eb0f357a", "#퇴근길"),
        TagDef("672c6fe1-2ba5-4209-9ca0-95274c0d8e82", "#모닝루틴"),
        TagDef("e0473b71-fe6c-48bb-a1f2-79ccd6efeab8", "#일어나서"),
        TagDef("13f7788b-4241-43b5-9f52-67ba6a2024c2", "#저녁"),
        TagDef("7c5cbe2a-ca4c-4134-ae98-836b19f3adc4", "#자기전"),
        TagDef("4e9282c3-e6a8-445e-89f8-bb0f3fd1a6a2", "#휴일"),
        TagDef("1516f576-325b-4cba-8871-36a9970fb743", "#공강"),
    )

    // 활동 태그(ob_rou_tag2)
    val ACTIVITY = listOf(
        TagDef("8dcf50a6-ceaa-4e4e-acf8-ef148d50d8fe", "#독서"),
        TagDef("2ce0449c-ab6e-492a-8d2f-888eed127aa6", "#과제"),
        TagDef("30da5b0c-adb4-44c3-9be7-8f498fdf5b4b", "#공부"),
        TagDef("0b2026e7-6b63-4086-b808-763b7238eca7", "#작업"),
        TagDef("304d8485-60e5-40de-a607-1986888cd0bc", "#다이어트"),
        TagDef("a2331c30-2aa3-451a-97eb-57f578654447", "#수능"),
        TagDef("4c1281bf-014f-4aba-829b-be8fb7783a97", "#취준"),
        TagDef("43605d53-3a05-4c08-93cb-686f6ac55944", "#프로그래밍"),
        TagDef("8e5874ae-62ec-42a0-a5d8-83e5c252ae48", "#휴식"),
    )
}