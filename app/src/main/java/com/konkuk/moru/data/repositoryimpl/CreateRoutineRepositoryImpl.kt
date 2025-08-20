package com.konkuk.moru.data.repositoryimpl

import android.os.SystemClock
import android.util.Log
import com.konkuk.moru.data.dto.request.CreateRoutineRequest
import com.konkuk.moru.data.dto.response.CreateRoutineResponse
import com.konkuk.moru.data.service.CreateRoutineService
import com.konkuk.moru.domain.repository.CreateRoutineRepository
import retrofit2.HttpException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

class CreateRoutineRepositoryImpl @Inject constructor(
    private val service: CreateRoutineService
) : CreateRoutineRepository {
    private val TAG = "createroutine"
    private val gson by lazy { com.google.gson.Gson() } // [추가]

    override suspend fun createRoutine(body: CreateRoutineRequest): Result<CreateRoutineResponse> =
        runCatching {
            val trace = UUID.randomUUID().toString().take(8)
            val t0 = SystemClock.elapsedRealtime()
            Log.d(TAG, "[$trace] request start: /api/routines")

            // 👇 실제 전송될 JSON을 그대로 확인 (널/빈값 포함)
            val json = gson.toJson(body) // [추가]
            Log.d(TAG, "[$trace] request json=$json") // [추가]

            val res = service.createRoutine(body)

            val t1 = SystemClock.elapsedRealtime()
            val rawReq = res.raw().request
            Log.d(TAG, "[$trace] response time=${t1 - t0}ms")
            Log.d(TAG, "[$trace] http ${rawReq.method} ${rawReq.url}")

            // 👇 Authorization 헤더 포함 여부(앞자리만) 체크
            val auth = rawReq.header("Authorization")?.let { it.take(16) + "..." } ?: "null" // [추가]
            Log.d(TAG, "[$trace] req headers: Authorization=$auth, Content-Type=${rawReq.header("Content-Type")}") // [추가]

            Log.d(TAG, "[$trace] code=${res.code()} success=${res.isSuccessful}")
            val err = res.errorBody()?.string()?.take(2000)
            if (err != null) Log.d(TAG, "[$trace] errorBody=$err")

            if (!res.isSuccessful) throw HttpException(res)
            val bodyResp = res.body() ?: throw IllegalStateException("Empty body")
            Log.d(TAG, "[$trace] response body=$bodyResp")
            bodyResp
        }.onFailure { e ->
            Log.d(TAG, "[fail] ${e::class.java.simpleName}: ${e.message}")
            Log.d(TAG, Log.getStackTraceString(e))
        }
}