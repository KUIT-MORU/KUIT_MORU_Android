package com.konkuk.moru.data.repositoryimpl

import android.util.Log
import com.konkuk.moru.data.dto.request.FavoriteTagRequest
import com.konkuk.moru.data.dto.response.ServerTag
import com.konkuk.moru.data.dto.response.UpdateUserProfileResponse
import com.konkuk.moru.data.dto.response.OBImageUploadResponse
import com.konkuk.moru.data.dto.response.NicknameCheckResponse
import com.konkuk.moru.data.service.ImageUploadService
import com.konkuk.moru.data.service.OBUserService
import com.konkuk.moru.domain.repository.OBUserRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named
import java.io.File

class OBUserRepositoryImpl @Inject constructor(
    @Named("obUserAuthed") private val authedService: OBUserService,
    @Named("obUserAuthless") private val authlessService: OBUserService,
    private val imageService: ImageUploadService // (미사용 가능하지만 보존)
) : OBUserRepository {

    // ==============================
    // 닉네임 중복 체크
    // ==============================
    override suspend fun checkNicknameAvailable(nickname: String): Result<Boolean> = runCatching {
        Log.d("onboarding", "checkNicknameAvailable() start nickname=$nickname")

        // 1) AUTHLESS 먼저 시도
        val guest = safeCheckNickname(nickname, channel = "authless")
        if (guest?.isSuccessful == true) {
            return@runCatching (guest.body()?.available == true)
        }

        // 2) 실패 시 AUTHeD 폴백 (게스트 null 또는 비성공 모두 포함)
        val authed = safeCheckNickname(nickname, channel = "authed")
        if (authed?.isSuccessful != true) throw HttpException(authed!!)
        (authed.body()?.available == true)
    }.onFailure { e ->
        Log.d("onboarding", "checkNicknameAvailable() failed: ${e.message}")
    }

    private suspend fun safeCheckNickname(
        nickname: String,
        channel: String
    ): Response<NicknameCheckResponse>? {
        return runCatching {
            val res = if (channel == "authless") {
                authlessService.checkNickname(nickname)
            } else {
                authedService.checkNickname(nickname)
            }
            Log.d(
                "onboarding",
                "checkNicknameAvailable() $channel resp code=${res.code()} body=${res.body()}"
            )
            res
        }.onFailure { e ->
            Log.d("onboarding", "checkNicknameAvailable() $channel exception: ${e.message}")
        }.getOrNull()
    }

    // ==============================
    // 사용자 정보 업데이트 (Dynamic)
    // ==============================
    override suspend fun updateProfileDynamic(
        body: Map<String, String>
    ): Result<UpdateUserProfileResponse> = runCatching {
        Log.d("onboarding", "updateProfileDynamic() body=$body")
        val res = authedService.updateMeDynamic(body)
        val err = res.errorBody()?.string()?.take(1200)
        Log.d(
            "onboarding",
            "updateProfileDynamic() resp code=${res.code()} body=${res.body()} err=${err ?: "null"}"
        )
        if (!res.isSuccessful) throw HttpException(res)
        res.body()!!
    }.onFailure { e ->
        Log.d("onboarding", "updateProfileDynamic() failed: ${e.message}")
    }

    // ==============================
    // 태그 전체 조회
    // ==============================
    override suspend fun getAllTags(): Result<List<ServerTag>> =
        runCatching {
            Log.d("onboarding", "getAllTags() request")
            val res = authedService.getAllTags()
            val code = res.code()
            val err = res.errorBody()?.string()
            Log.d(
                "onboarding",
                "getAllTags() response code=$code${if (err != null) " error=$err" else ""} count=${res.body()?.size ?: -1}"
            )
            if (!res.isSuccessful) throw HttpException(res)
            res.body() ?: emptyList()
        }.onFailure { e ->
            Log.d("onboarding", "getAllTags() failed: ${e.message}")
        }

    // ==============================
    // 관심 태그 등록
    // ==============================
    override suspend fun addFavoriteTags(body: FavoriteTagRequest): Result<Unit> =
        runCatching {
            Log.d("onboarding", "addFavoriteTags() request body=$body")
            val res = authedService.addFavoriteTags(body)
            val code = res.code()
            val err = res.errorBody()?.string()
            Log.d(
                "onboarding",
                "addFavoriteTags() response code=$code${if (err != null) " error=$err" else ""}"
            )
            if (!res.isSuccessful) throw HttpException(res)
            Unit
        }.onFailure { e ->
            Log.d("onboarding", "addFavoriteTags() failed: ${e.message}")
        }

    // ==============================
    // 이미지 업로드
    // ==============================
    override suspend fun uploadImage(file: File): Result<String> = runCatching {
        val mime = guessMimeType(file)
        val body = file.asRequestBody(mime.toMediaType())
        val part = MultipartBody.Part.createFormData("file", file.name, body)

        Log.d(
            "onboarding",
            "uploadImage() start: name=${file.name}, size=${file.length()}, mime=$mime"
        )

        val res = authedService.uploadImage(part) // 인증 클라이언트 사용

        Log.d("onboarding", "uploadImage() resp: code=${res.code()}")
        if (!res.isSuccessful) {
            val err = res.errorBody()?.string()?.take(500)
            Log.d("onboarding", "uploadImage() failed: code=${res.code()} err=${err ?: "null"}")
            throw HttpException(res)
        }

        val obj: OBImageUploadResponse? = res.body()
        Log.d("onboarding", "uploadImage() success body=$obj")

        val key = obj?.bestKeyOrNull()
        require(!key.isNullOrBlank()) { "uploadImage response has no url/key" }

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