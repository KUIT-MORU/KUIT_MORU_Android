package com.konkuk.moru.data.repositoryimpl

import com.konkuk.moru.data.dto.response.MyRoutine.AddTagsRequest
import com.konkuk.moru.data.dto.response.MyRoutine.MyRoutineDetailDto
import com.konkuk.moru.data.dto.response.MyRoutine.MyRoutineSummaryDto
import com.konkuk.moru.data.dto.response.MyRoutine.MyRoutineUi
import com.konkuk.moru.data.dto.response.MyRoutine.PatchOrCreateStepRequest
import com.konkuk.moru.data.dto.response.MyRoutine.PatchRoutineRequest
import com.konkuk.moru.data.dto.response.MyRoutine.UpdateScheduleRequest
import com.konkuk.moru.data.mapper.toMyApiString
import com.konkuk.moru.data.mapper.toMyUi
import com.konkuk.moru.data.service.ImageService
import com.konkuk.moru.data.service.MyRoutineService
import com.konkuk.moru.domain.repository.MyRoutineRepository
import com.konkuk.moru.domain.repository.MyRoutineSchedule
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField
import javax.inject.Inject

class MyRoutineRepositoryImpl @Inject constructor(
    private val service: MyRoutineService,
    private val imageService: ImageService // [추가]
) : MyRoutineRepository {

    override suspend fun getMyRoutines(
        sortType: String,
        dayOfWeek: DayOfWeek?,
        page: Int,
        size: Int
    ): List<MyRoutineUi> {
        // [변경] TIME인데 dayOfWeek가 없으면 서버가 기대대로 정렬 안 줄 수 있어 null 그대로 전달
        //       (TIME의 기본 요일 처리는 ViewModel에서 '오늘 요일'로 채워 줍니다.)
        val dayParam = dayOfWeek?.toMyApiString()

        val res = service.getMyRoutines(
            sortType = sortType,
            dayOfWeek = dayParam,
            page = page,
            size = size
        )

        // [추가] 서버가 정렬을 무시하거나 일관적이지 않을 때를 대비해
        //        POPULAR / LATEST는 클라이언트에서 한 번 더 보정 정렬
        val dtoList = res.content

        val sortedDto: List<MyRoutineSummaryDto> = when (sortType) {
            "POPULAR" -> dtoList.sortedByDescending { it.likeCount } // [추가] 좋아요 내림차순
            "LATEST" -> dtoList.sortedByDescending { toEpochFlexible(it.createdAt) }// [추가] 최신순
            else -> dtoList // TIME은 서버 기준(요일 필요) 유지
        }

        // [유지] 정렬된 DTO → UI 매핑 (likeCount → likes 매핑 포함)
        return sortedDto.map { it.toMyUi() }
    }

    private fun toEpoch(iso: String): Long = try {
        // 예) "2025-08-15T02:46:24.026Z"
        Instant.parse(iso).toEpochMilli()
    } catch (_: DateTimeParseException) {
        0L
    }

    private fun toEpochFlexible(s: String?): Long {
        if (s.isNullOrBlank()) return 0L
        return try {
            // 1) 2025-08-15T05:45:03.725Z 형태
            Instant.parse(s).toEpochMilli()
        } catch (_: Exception) {
            try {
                // 2) 2025-08-15T16:26:06.764185 (존 없음, 마이크로초도 허용)
                val parser = DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                    .optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
                    .optionalEnd()
                    .toFormatter()
                LocalDateTime.parse(s, parser).atZone(ZoneId.systemDefault()).toInstant()
                    .toEpochMilli()
            } catch (_: Exception) {
                0L
            }
        }
    }

    override suspend fun getRoutineDetail(routineId: String): MyRoutineUi {
        return service.getRoutineDetail(routineId).toMyUi()
    }

    // ===== [추가] 상세 RAW =====
    override suspend fun getRoutineDetailRaw(routineId: String): MyRoutineDetailDto {
        return service.getRoutineDetail(routineId)
    }


    private fun Response<Unit>.isOkOr404() = isSuccessful || code() == 404

    private suspend fun purgeSchedules(routineId: String): Boolean {
        return try {
            val bulk = service.deleteAllSchedules(routineId)
            if (bulk.isOkOr404()) return true

            // 벌크 실패 시 개별 삭제로 폴백
            val list = runCatching { service.getSchedules(routineId) }.getOrDefault(emptyList())
            var allOk = true
            list.forEach { sch ->
                val r = service.deleteSchedule(routineId, sch.id)
                if (!r.isOkOr404()) allOk = false
            }
            allOk
        } catch (_: Exception) {
            false
        }
    }

    // 404는 “이미 삭제됨”으로 간주하여 성공 처리
    override suspend fun deleteRoutineSafe(routineId: String): Boolean {
        val first = service.deleteRoutine(routineId)
        if (first.isSuccessful || first.code() == 404) return true

        // 연관 스케줄 정리 시도
        service.deleteAllSchedules(routineId) // 2xx/404 모두 무시 가능

        val second = service.deleteRoutine(routineId)
        if (second.isSuccessful || second.code() == 404) return true

        // 최종 실패: false 반환(절대 throw 해서 앱 죽이지 않기)
        return false
    }


    override suspend fun deleteRoutine(routineId: String) {
        val r = service.deleteRoutine(routineId)
        if (!r.isSuccessful) throw IllegalStateException("Failed to delete routine: ${r.code()}")
    }

    // 스케줄 관련 로직

    override suspend fun getSchedules(routineId: String): List<MyRoutineSchedule> {
        return service.getSchedules(routineId).map {
            MyRoutineSchedule(
                id = it.id,
                dayOfWeek = it.dayOfWeek,
                time = it.time,
                alarmEnabled = it.alarmEnabled
            )
        }
    }

    override suspend fun deleteAllSchedules(routineId: String) {
        val r = service.deleteAllSchedules(routineId)
        if (!r.isSuccessful) throw IllegalStateException("Failed to delete all schedules")
    }

    override suspend fun deleteSchedule(routineId: String, scheduleId: String) {
        val r = service.deleteSchedule(routineId, scheduleId)
        if (!r.isSuccessful) throw IllegalStateException("Failed to delete schedule $scheduleId")
    }

    override suspend fun updateSchedule(
        routineId: String,
        schId: String,
        time: String,
        days: Set<DayOfWeek>,
        alarm: Boolean
    ): List<MyRoutineSchedule> {
        val req = UpdateScheduleRequest(
            repeatType = "CUSTOM",
            daysToCreate = days.map { it.toMyApiString() },
            time = time,
            alarmEnabled = alarm
        )
        return service.patchSchedule(routineId, schId, req).map {
            MyRoutineSchedule(
                id = it.id,
                dayOfWeek = it.dayOfWeek,
                time = it.time,
                alarmEnabled = it.alarmEnabled
            )
        }
    }


    // ===== [추가] 루틴 PATCH =====
    override suspend fun patchRoutine(
        routineId: String,
        title: String?,
        imageUrl: String?,
        tagNames: List<String>?,
        description: String?,
        steps: List<Triple<String, Int, String?>>?,
        selectedApps: List<String>?,
        isSimple: Boolean?,
        isUserVisible: Boolean?
    ) {
        val steps2d = steps?.let { list ->
            listOf(list.map { (name, order, iso) ->
                PatchOrCreateStepRequest(name, order, iso)
            })
        }
        val body = PatchRoutineRequest(
            title = title,
            imageUrl = imageUrl,
            tags = tagNames,
            description = description,
            steps = steps2d,
            selectedApps = selectedApps,
            isSimple = isSimple,
            isUserVisible = isUserVisible
        )
        val res = service.patchRoutine(routineId, body)
        if (!res.isSuccessful) error("patchRoutine failed: ${res.code()}")
    }

    // ===== [추가] STEP =====
    override suspend fun getSteps(routineId: String): List<MyRoutineDetailDto.StepDto> =
        service.getSteps(routineId)

    override suspend fun addSteps(routineId: String, steps: List<Triple<String, Int, String?>>) {
        val body = steps.map { (n, o, iso) -> PatchOrCreateStepRequest(n, o, iso) }
        val res = service.addSteps(routineId, body)
        if (!res.isSuccessful) error("addSteps failed: ${res.code()}")
    }

    override suspend fun patchSteps(
        routineId: String,
        stepId: String,
        steps: List<Triple<String, Int, String?>>
    ) {
        val body = steps.map { (n, o, iso) -> PatchOrCreateStepRequest(n, o, iso) }
        val res = service.patchSteps(routineId, stepId, body)
        if (!res.isSuccessful) error("patchSteps failed: ${res.code()}")
    }

    override suspend fun deleteStep(routineId: String, stepId: String) {
        val res = service.deleteStep(routineId, stepId)
        if (!res.isSuccessful) error("deleteStep failed: ${res.code()}")
    }

    // ===== [추가] TAG =====
    override suspend fun getRoutineTags(routineId: String) = service.getRoutineTags(routineId)

    override suspend fun addRoutineTags(routineId: String, tagIds: List<String>) =
        service.addRoutineTags(routineId, AddTagsRequest(tagIds))

    override suspend fun removeRoutineTag(routineId: String, tagId: String) {
        val res = service.removeRoutineTag(routineId, tagId)
        if (!res.isSuccessful) error("removeRoutineTag failed: ${res.code()}")
    }

    // ===== [추가] 이미지 업로드 =====
    override suspend fun uploadImageAndGetUrl(
        fileName: String,
        bytes: ByteArray,
        mime: String
    ): String {
        val media = mime.toMediaType()
        val body: RequestBody = bytes.toRequestBody(media, 0, bytes.size)
        val part = MultipartBody.Part.createFormData("file", fileName, body)
        val map = imageService.upload(part)
        // 서버가 어떤 키로 주든 안전하게 파싱
        return map["url"] ?: map["location"] ?: map["key"]
        ?: error("upload response missing url/location/key")
    }


}