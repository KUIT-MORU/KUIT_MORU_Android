package com.konkuk.moru.data.repositoryimpl

import android.os.SystemClock
import android.util.Log
import com.konkuk.moru.data.dto.request.LoginRequestDto
import com.konkuk.moru.data.dto.request.SignUpRequest
import com.konkuk.moru.data.service.AuthService
import com.konkuk.moru.data.token.TokenManager
import com.konkuk.moru.domain.repository.AuthRepository
import retrofit2.HttpException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import java.util.UUID

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

            tokenManager.saveTokens(access, refresh) // [유지]

            Result.success(access to refresh)
        } catch (e: HttpException) {
            Log.e("Login", "HTTP Exception: ${e.code()}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("Login", "Login failed", e)
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        val traceId = UUID.randomUUID().toString().take(8)
        val t0 = SystemClock.elapsedRealtime()
        Log.d("signup","[$traceId] call: /api/auth/signup, email=$email, pwdLen=${password.length}")

        return try {
            val resp = service.signUp(SignUpRequest(email = email, password = password))
            val ms = SystemClock.elapsedRealtime() - t0
            Log.d("signup","[$traceId] ok in ${ms}ms, tokens(access=${resp.accessToken.take(6)}...${resp.accessToken.takeLast(4)}, refresh=${resp.refreshToken.take(6)}...${resp.refreshToken.takeLast(4)})")

            tokenManager.saveTokens(resp.accessToken, resp.refreshToken) // [유지]
            Log.d("signup", "[$traceId] tokens saved")
            Result.success(Unit)
        } catch (e: HttpException) {
            val ms = SystemClock.elapsedRealtime() - t0
            Log.e("signup","[$traceId] http ${e.code()} in ${ms}ms: ${e.message()}", e)
            Result.failure(e)
        } catch (ce: CancellationException) {
            Log.w("signup", "[$traceId] cancelled", ce)
            throw ce
        } catch (e: Exception) {
            val ms = SystemClock.elapsedRealtime() - t0
            Log.e("signup", "[$traceId] fail in ${ms}ms: ${e.message}", e)
            Result.failure(e)
        }
    }
}