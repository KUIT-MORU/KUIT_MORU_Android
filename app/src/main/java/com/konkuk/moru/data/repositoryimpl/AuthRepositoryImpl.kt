package com.konkuk.moru.data.repositoryimpl

import android.util.Log
import com.konkuk.moru.data.dto.request.LoginRequestDto
import com.konkuk.moru.data.service.AuthService
import com.konkuk.moru.data.token.TokenManager
import com.konkuk.moru.domain.repository.AuthRepository
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Pair<String, String>> {
        return try {
            Log.d("Login", "로그인 시도: $email")
            val response = service.login(LoginRequestDto(email, password))
            
            Log.d("Login", "서버 응답 코드: ${response.code()}")
            Log.d("Login", "서버 응답 헤더: ${response.headers()}")
            
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.e("Login", "HTTP ${response.code()}: $errorBody")
                Log.e("Login", "전체 에러 응답: $response")
                
                return when (response.code()) {
                    400 -> Result.failure(Exception("잘못된 요청입니다. 이메일과 비밀번호를 확인해주세요."))
                    401 -> Result.failure(Exception("이메일 또는 비밀번호가 올바르지 않습니다."))
                    500 -> Result.failure(Exception("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요. (상세: $errorBody)"))
                    else -> Result.failure(Exception("로그인에 실패했습니다. (${response.code()}) - $errorBody"))
                }
            }
            
            val res = response.body() ?: throw Exception("응답 데이터가 없습니다.")
            val access = res.token.accessToken
            val refresh = res.token.refreshToken

            Log.d("Login", "받은 accessToken = ${access.take(6)}...${access.takeLast(4)}")

            tokenManager.saveTokens(access, refresh)

            Result.success(access to refresh)
        } catch (e: HttpException) {
            Log.e("Login", "HTTP Exception: ${e.code()}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("Login", "Login failed", e)
            Result.failure(e)
        }
    }
}