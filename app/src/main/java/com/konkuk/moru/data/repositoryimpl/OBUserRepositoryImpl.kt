package com.konkuk.moru.data.repositoryimpl

import android.util.Log
import com.konkuk.moru.data.dto.request.FavoriteTagRequest
import com.konkuk.moru.data.dto.response.UpdateUserProfileResponse
import com.konkuk.moru.data.service.ImageUploadService
import com.konkuk.moru.data.service.OBUserService
import com.konkuk.moru.domain.repository.OBUserRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Named
import java.io.File

class OBUserRepositoryImpl @Inject constructor(
    @Named("obUserAuthed") private val authedService: OBUserService,
    @Named("obUserAuthless") private val authlessService: OBUserService,
    private val imageService: ImageUploadService
) : OBUserRepository {

    override suspend fun checkNicknameAvailable(nickname: String): Result<Boolean> = runCatching {
        Log.d("onboarding", "checkNicknameAvailable() request nickname=$nickname (AUTHeD ONLY)")
        val res = authedService.checkNickname(nickname)
        Log.d("onboarding", "checkNicknameAvailable() authed resp code=${res.code()} body=${res.body()}")
        if (!res.isSuccessful) throw HttpException(res)
        res.body()?.available == true
    }.onFailure { e ->
        Log.d("onboarding", "checkNicknameAvailable() failed: ${e.message}")
    }

    // ★ 변경: Dynamic 업데이트 전용. 단일 호출/단일 스키마
    override suspend fun updateProfileDynamic(
        body: Map<String, String>
    ): Result<UpdateUserProfileResponse> = runCatching {
        Log.d("onboarding", "updateProfileDynamic() body=$body")
        val res = authedService.updateMeDynamic(body)
        val err = res.errorBody()?.string()?.take(1200)
        Log.d("onboarding", "updateProfileDynamic() resp code=${res.code()} body=${res.body()} err=${err ?: "null"}")
        if (!res.isSuccessful) throw HttpException(res)
        res.body()!!
    }.onFailure { e ->
        Log.d("onboarding", "updateProfileDynamic() failed: ${e.message}")
    }

    override suspend fun addFavoriteTags(body: FavoriteTagRequest): Result<Unit> =
        runCatching {
            Log.d("onboarding", "addFavoriteTags() request body=$body")
            val res = authedService.addFavoriteTags(body)
            val code = res.code()
            val err = res.errorBody()?.string()
            Log.d("onboarding", "addFavoriteTags() response code=$code${if (err != null) " error=$err" else ""}")
            if (!res.isSuccessful) throw HttpException(res)
            Unit
        }.onFailure { e ->
            Log.d("onboarding", "addFavoriteTags() failed: ${e.message}")
        }

    override suspend fun uploadImage(file: File): Result<String> = runCatching {
        val mime = guessMimeType(file)
        val body = file.asRequestBody(mime.toMediaType())
        val part = MultipartBody.Part.createFormData("file", file.name, body) // ★ 고정: part=file

        Log.d("onboarding", "uploadImage() start: name=${file.name}, size=${file.length()}, mime=$mime")
        val res = imageService.uploadDynamic("/api/images/upload", part) // ★ 고정: 경로=/api/images/upload
        Log.d("onboarding", "uploadImage() resp: path=/api/images/upload, code=${res.code()}")

        if (!res.isSuccessful) {
            val err = res.errorBody()?.string()?.take(500)
            Log.d("onboarding", "uploadImage() failed: code=${res.code()} err=${err ?: "null"}")
            throw HttpException(res)
        }

        val obj = res.body()
        Log.d("onboarding", "uploadImage() success body=$obj")

        val key = obj?.imageUrl ?: obj?.url ?: obj?.path ?: obj?.imageKey ?: obj?.key
        require(!key.isNullOrBlank()) { "uploadImage response has no url/key" } // ★ 실패 시 명확히

        key
    }.onFailure { e ->
        Log.d("onboarding", "uploadImage() failed final: ${e.message}")
    }

    private fun guessMimeType(file: File): String = when (file.extension.lowercase()) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "webp" -> "image/webp"
        "gif" -> "image/gif"
        else -> "image/*"
    }
}