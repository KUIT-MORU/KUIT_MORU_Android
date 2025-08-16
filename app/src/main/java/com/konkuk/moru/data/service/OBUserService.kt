package com.konkuk.moru.data.service

import com.konkuk.moru.data.dto.request.FavoriteTagRequest
import com.konkuk.moru.data.dto.request.UpdateUserProfileRequest
import com.konkuk.moru.data.dto.response.NicknameCheckResponse
import com.konkuk.moru.data.dto.response.UpdateUserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface OBUserService {
    @GET("/api/user/nickname/{nickname}")
    suspend fun checkNickname(
//        @Path("nickname") nickname: String
        @Path(value = "nickname", encoded = true) nickname: String
    ): Response<NicknameCheckResponse>

    @PATCH("/api/user/me")
    suspend fun updateMe(
        @Body body: UpdateUserProfileRequest
    ): Response<UpdateUserProfileResponse>

    @POST("/api/user/favorite-tag")
    suspend fun addFavoriteTags(
        @Body body: FavoriteTagRequest
    ): Response<Unit>
}