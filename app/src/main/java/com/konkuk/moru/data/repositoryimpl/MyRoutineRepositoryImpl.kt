package com.konkuk.moru.data.repositoryimpl

import com.konkuk.moru.data.dto.response.MyRoutine.MyRoutineSummaryDto
import com.konkuk.moru.data.dto.response.MyRoutine.MyRoutineUi
import com.konkuk.moru.data.dto.response.MyRoutine.UpdateScheduleRequest
import com.konkuk.moru.data.mapper.toMyApiString
import com.konkuk.moru.data.mapper.toMyUi
import com.konkuk.moru.data.service.MyRoutineService
import com.konkuk.moru.domain.repository.MyRoutineRepository
import com.konkuk.moru.domain.repository.MyRoutineSchedule
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
    private val service: MyRoutineService
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
            "LATEST"  -> dtoList.sortedByDescending { toEpochFlexible(it.createdAt) }// [추가] 최신순
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
                    .optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true).optionalEnd()
                    .toFormatter()
                LocalDateTime.parse(s, parser).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            } catch (_: Exception) { 0L }
        }
    }

    override suspend fun getRoutineDetail(routineId: String): MyRoutineUi {
        return service.getRoutineDetail(routineId).toMyUi()
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


}