package com.konkuk.moru.data.repositoryimpl

import android.os.SystemClock
import android.util.Log
import com.konkuk.moru.data.dto.response.CreateRoutineResponse
import com.konkuk.moru.data.network.ApiErrorParser
import com.konkuk.moru.data.service.CreateRoutineService
import com.konkuk.moru.domain.repository.CreateRoutineRepository
import retrofit2.HttpException
import java.util.UUID
import javax.inject.Inject
import com.konkuk.moru.data.net.ErrorBodyParser

class CreateRoutineRepositoryImpl @Inject constructor(
    private val service: CreateRoutineService
) : CreateRoutineRepository {

    private val TAG = "createroutine"
    private val gson by lazy { com.google.gson.Gson() }

    // [변경] body: Any
    override suspend fun createRoutine(body: Any): Result<CreateRoutineResponse> =
        runCatching {
            val trace = UUID.randomUUID().toString().take(8)
            val t0 = SystemClock.elapsedRealtime()
            Log.d(TAG, "[$trace] request start: /api/routines")
            Log.d(TAG, "[$trace] request json=${gson.toJson(body)}") // 타입 구분 없이 로깅

            val res = service.createRoutine(body)

            val parsed = ErrorBodyParser.parse(res.errorBody())
            if (parsed != null) {
                Log.d(
                    TAG,
                    "[$trace] parsedError status=${parsed.status} code=${parsed.code} message=${parsed.message} basicError=${parsed.error} path=${parsed.path}"
                )
            }

            Log.d(TAG, "[$trace] code=${res.code()} success=${res.isSuccessful}")

            if (!res.isSuccessful) throw HttpException(res)

            val t1 = SystemClock.elapsedRealtime()
            val rawReq = res.raw().request
            Log.d(TAG, "[$trace] http ${rawReq.method} ${rawReq.url}")
            Log.d(
                TAG,
                "[$trace] req headers: Authorization=${
                    rawReq.header("Authorization")?.let { it.take(16) + "..." } ?: "null"
                }"
            )

            if (!res.isSuccessful) {
                val (errRaw, errParsed) = ApiErrorParser.parse(res)
                if (errRaw != null) Log.d(TAG, "[$trace] errorBody=$errRaw")
                if (errParsed != null) {
                    Log.d(
                        TAG,
                        "[$trace] parsedError status=${errParsed.status} code=${errParsed.code} message=${errParsed.message}"
                    )
                }
                val msg = buildString {
                    append("HTTP ${res.code()}")
                    errParsed?.code?.let { append(" / $it") }
                    errParsed?.message?.let { append(" - $it") }
                }
                throw HttpException(res).also {
                    Log.d(TAG, "[$trace] throw HttpException: $msg")
                }
            }

            res.body() ?: throw IllegalStateException("Empty body")
        }.onFailure { e ->
            Log.d(TAG, "[fail] ${e::class.java.simpleName}: ${e.message}")
            Log.d(TAG, Log.getStackTraceString(e))
        }
}