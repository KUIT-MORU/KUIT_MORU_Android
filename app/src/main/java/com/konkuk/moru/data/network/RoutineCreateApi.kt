package com.konkuk.moru.data.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// [참고] 실제 엔드포인트 경로는 Swagger 기준으로 맞춰주세요.
// 아래 경로는 예시입니다. (백엔드와 논의 후 수정)
// ex) 이미지 업로드: POST /files/upload  → imageKey 반환
//     루틴 생성: POST /routines

data class ImageUploadResponse(
    val imageKey: String
)

data class CreateRoutineRequest(
    val title: String,
    val imageKey: String?,
    val tags: List<String>,
    val description: String,
    val steps: List<StepDto>,
    val selectedApps: List<String>,
    val isSimple: Boolean,
    val isUserVisible: Boolean
)

data class StepDto(
    val name: String,
    val stepOrder: Int,
    val estimatedTime: String
)

data class CreateRoutineResponse(
    val id: Long?,      // 서버 스펙에 맞게 조정
    val message: String?
)

interface RoutineApi {

    @Multipart
    @POST("files/upload") // TODO: Swagger 경로로 맞추세요
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<ImageUploadResponse>

    @POST("routines") // TODO: Swagger 경로로 맞추세요
    suspend fun createRoutine(
        @Body body: CreateRoutineRequest
    ): Response<CreateRoutineResponse>
}