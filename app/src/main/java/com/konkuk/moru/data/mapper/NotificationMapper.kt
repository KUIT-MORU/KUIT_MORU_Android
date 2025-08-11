package com.konkuk.moru.data.mapper

import com.konkuk.moru.data.model.NotificationCursor
import com.konkuk.moru.data.model.NotificationItem
import com.konkuk.moru.data.model.NotificationPage
import com.konkuk.moru.presentation.routinefeed.data.NotificationCursorDto
import com.konkuk.moru.presentation.routinefeed.data.NotificationItemDto
import com.konkuk.moru.presentation.routinefeed.data.NotificationListResponseDto
import java.time.Instant

fun NotificationItemDto.toDomain(): NotificationItem =
    NotificationItem(
        id = id,
        senderId=senderId,
        senderNickname = senderNickname,
        senderProfileImage = senderProfileImage,
        message = message,
        createdAt = null,              // <- 서버가 per-item createdAt 주면 Instant.parse(...)로 채우기
        relativeTime = relativeTime
    )

fun NotificationCursorDto.toDomain(): NotificationCursor =
    NotificationCursor(
        createdAt = Instant.parse(createdAt),
        id = id
    )

fun NotificationListResponseDto.toDomain(): NotificationPage =
    NotificationPage(
        items = content.map { it.toDomain() },
        hasNext = hasNext,
        nextCursor = nextCursor?.toDomain()
    )