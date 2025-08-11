package com.konkuk.moru.data.repositoryimpl

import com.konkuk.moru.data.mapper.toDomain
import com.konkuk.moru.data.model.NotificationCursor
import com.konkuk.moru.data.model.NotificationPage
import com.konkuk.moru.data.service.NotificationService
import com.konkuk.moru.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: NotificationService
) : NotificationRepository {


    override suspend fun fetchNotifications(
        cursor: NotificationCursor?,
        size: Int
    ): NotificationPage {
        val res = api.getNotifications(
            cursorCreatedAt = cursor?.createdAt?.toString(),
            lastNotificationId = cursor?.id,
            size = size
        )
        return res.toDomain()
    }

    // [추가]
    override suspend fun getUnreadCount(): Int {
        return api.getUnreadCount()
    }

    // [추가]
    override suspend fun deleteNotification(notificationId: String) {
        api.deleteNotification(notificationId)
    }

    // [추가]
    override suspend fun markAllAsRead() {
        // 나중에 서버 API가 추가되면 아래 주석을 해제합니다.
        // api.markAllAsRead()
    }


}