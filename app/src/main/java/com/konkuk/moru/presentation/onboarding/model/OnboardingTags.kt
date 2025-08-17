package com.konkuk.moru.presentation.onboarding.model

import com.konkuk.moru.data.model.TagDef

object OnboardingTags {
    // 상황 태그(ob_rou_tag1)
    val SITUATION = listOf(
        TagDef("f7af32f3-5125-4382-8cc6-06bb9f9fcb6b", "#출근길"),
        TagDef("842b851c-1f61-48ec-ad79-cea47c1774fc", "#지하철"),
        TagDef("076af816-24d4-4c4b-936c-44e017497c59", "#퇴근길"),
        TagDef("b9915595-8291-4450-a582-e39bbd7b3c25", "#모닝루틴"),
        TagDef("ab83e0e8-3d4b-4012-ac43-596197536bc2", "#일어나서"),
        TagDef("d6b5668e-a339-4657-95b9-76e34bf2656d", "#저녁"),
        TagDef("583991ad-4675-44de-90c1-53d01294307d", "#자기전"),
        TagDef("236d5c10-244c-4a53-bb0d-ab109da2a7a9", "#휴일"),
        TagDef("78e51b74-80f5-4286-addb-425fc8b792df", "#공강"),
    )

    // 활동 태그(ob_rou_tag2)
    val ACTIVITY = listOf(
        TagDef("d840097d-5e0a-4d94-9a92-58d4c01bd7c6", "#독서"),
        TagDef("2a276076-fd0c-4870-9695-c2ba0faa1b7a", "#과제"),
        TagDef("15298fde-f2cd-4c37-818a-9ea0166a534c", "#공부"),
        TagDef("e1729a0d-99d9-4eb3-b7e6-550488dfca2e", "#작업"),
        TagDef("1ee13c59-49ba-4ef8-a280-de08551abae0", "#다이어트"),
        TagDef("ee154842-4abb-4560-a49e-f4414f2d6730", "#수능"),
        TagDef("a5f08f69-0e5a-4e47-adc6-1a9462227fb4", "#취준"),
        TagDef("48c9f196-53cf-4faa-a369-6a8ae95730d2", "#프로그래밍"),
        TagDef("c6d12e10-2d73-410e-81a2-3aeb48b8a54f", "#휴식"),
    )
}