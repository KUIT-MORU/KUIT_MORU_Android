package com.konkuk.moru.data.service

import com.konkuk.moru.data.dto.request.FavoriteTagRequest
import com.konkuk.moru.data.dto.response.NicknameCheckResponse
import com.konkuk.moru.data.dto.response.UpdateUserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface OBUserService {
    @GET("/api/user/nickname/{nickname}")
    suspend fun checkNickname(
        @Path(value = "nickname", encoded = true) nickname: String
    ): Response<NicknameCheckResponse>

    // (참고) DTO PATCH는 사용하지 않음. 서버 스키마가 확정되어 Dynamic만 사용.
    // @PATCH("/api/user/me")
    // suspend fun updateMe(@Body body: UpdateUserProfileRequest): Response<UpdateUserProfileResponse>

    // ★ 변경: 서버가 허용한 방식(로그로 검증) — PUT + Map
    @PATCH("/api/user/me")
    suspend fun updateMeDynamic(
        @Body body: Map<String, String>
    ): Response<UpdateUserProfileResponse>

    @POST("/api/user/favorite-tag")
    suspend fun addFavoriteTags(
        @Body body: FavoriteTagRequest
    ): Response<Unit>
}