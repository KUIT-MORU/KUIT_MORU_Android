package com.konkuk.moru.data.repositoryimpl

import android.util.Log
import com.konkuk.moru.data.dto.response.OBImageUploadResponse
import com.konkuk.moru.data.service.CRImageService
import com.konkuk.moru.domain.repository.CRImageRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

class CRImageRepositoryImpl @Inject constructor(
    private val service: CRImageService
) : CRImageRepository {

    override suspend fun uploadImage(file: File): Result<String> = runCatching {
        val mime = when (file.extension.lowercase()) {
            "jpg","jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            else -> "image/*"
        }
        val body = file.asRequestBody(mime.toMediaType())
        val part = MultipartBody.Part.createFormData("file", file.name, body)

        Log.d("createroutine", "[upload] start name=${file.name} size=${file.length()} mime=$mime")
        val res = service.uploadImage(part)
        // ----- [추가] 성공/실패와 무관하게 Authorization 헤더 먼저 로깅 -----
        val rawReq = res.raw().request
        Log.d("createroutine", "[upload] req Authorization=${rawReq.header("Authorization")?.let { it.take(16) + "..." } ?: "null"}")
        // --------------------------------------------------------------------
        Log.d("createroutine", "[upload] resp code=${res.code()} body=${res.body()}")

        if (!res.isSuccessful) {
            val err = res.errorBody()?.string()?.take(800)
            Log.d("createroutine", "[upload] error=$err")
            throw HttpException(res)
        }

        val obj: OBImageUploadResponse? = res.body()
        val key = obj?.bestKeyOrNull()
        require(!key.isNullOrBlank()) { "uploadImage response has no url/key" }

        Log.d("createroutine", "[upload] success key=$key")
        key
    }.onFailure { e ->
        Log.d("createroutine", "[upload] failed ${e.message}")
    }
}