package com.konkuk.moru.data.mapper

import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime

// [추가] 요일 → 서버 문자열
fun DayOfWeek.toMyApiString(): String = when (this) {
    DayOfWeek.MONDAY -> "MON"
    DayOfWeek.TUESDAY -> "TUE"
    DayOfWeek.WEDNESDAY -> "WED"
    DayOfWeek.THURSDAY -> "THU"
    DayOfWeek.FRIDAY -> "FRI"
    DayOfWeek.SATURDAY -> "SAT"
    DayOfWeek.SUNDAY -> "SUN"
}

fun String?.toMyMinutesOrNull(): Long? = try { this?.let { Duration.parse(it).toMinutes() } } catch (_: Exception) { null }
fun String?.toMyLocalTimeOrNull(): LocalTime? = try { this?.let { LocalTime.parse(it) } } catch (_: Exception) { null }