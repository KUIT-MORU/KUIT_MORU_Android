package com.konkuk.moru.data.service

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// [추가] 서버가 Map<String,String>을 돌려주는 스웨거 예시 대응
interface ImageService {
    @Multipart
    @POST("api/images/upload")
    suspend fun upload(@Part file: MultipartBody.Part): Map<String, String>
}