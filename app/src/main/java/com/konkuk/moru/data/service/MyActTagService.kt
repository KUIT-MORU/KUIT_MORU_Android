package com.konkuk.moru.data.service

import com.konkuk.moru.data.dto.response.MyActTagResponse
import retrofit2.http.GET

interface MyActTagService {
    @GET("api/tags")
    suspend fun getAllTags(): List<MyActTagResponse>
}