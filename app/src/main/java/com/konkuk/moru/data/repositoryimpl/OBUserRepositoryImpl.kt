package com.konkuk.moru.data.repositoryimpl

import android.util.Log
import com.konkuk.moru.data.dto.request.FavoriteTagRequest
import com.konkuk.moru.data.dto.request.UpdateUserProfileRequest
import com.konkuk.moru.data.dto.response.UpdateUserProfileResponse
import com.konkuk.moru.data.service.OBUserService
import com.konkuk.moru.domain.repository.OBUserRepository
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Named


class OBUserRepositoryImpl @Inject constructor(
    @Named("obUserAuthed") private val authedService: OBUserService,
    @Named("obUserAuthless") private val authlessService: OBUserService
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

    override suspend fun updateProfile(body: UpdateUserProfileRequest): Result<UpdateUserProfileResponse> =
        runCatching {
            Log.d("onboarding", "updateProfile() request body=$body")
            val res = authedService.updateMe(body)
            Log.d("onboarding", "updateProfile() response code=${res.code()} body=${res.body()}")
            if (!res.isSuccessful) throw HttpException(res)
            res.body()!!
        }.onFailure { e ->
            Log.d("onboarding", "updateProfile() failed: ${e.message}")
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
}