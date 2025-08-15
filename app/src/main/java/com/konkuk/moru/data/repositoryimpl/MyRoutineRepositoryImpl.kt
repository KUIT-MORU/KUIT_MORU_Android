package com.konkuk.moru.data.repositoryimpl

import com.konkuk.moru.data.dto.response.MyRoutine.MyRoutineSummaryDto
import com.konkuk.moru.data.dto.response.MyRoutine.MyRoutineUi
import com.konkuk.moru.data.mapper.toMyApiString
import com.konkuk.moru.data.mapper.toMyUi
import com.konkuk.moru.data.service.MyRoutineService
import com.konkuk.moru.domain.repository.MyRoutineRepository
import com.konkuk.moru.domain.repository.MyRoutineSchedule
import java.time.DayOfWeek
import java.time.Instant
import java.time.format.DateTimeParseException
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
            "LATEST" -> dtoList.sortedByDescending { it.createdAt?.let(::toEpoch) ?: 0L } // [추가] 최신순
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

    override suspend fun getRoutineDetail(routineId: String): MyRoutineUi {
        return service.getRoutineDetail(routineId).toMyUi()
    }

    override suspend fun deleteRoutine(routineId: String) {
        val r = service.deleteRoutine(routineId)
        if (!r.isSuccessful) throw IllegalStateException("Failed to delete routine: ${r.code()}")
    }

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
}