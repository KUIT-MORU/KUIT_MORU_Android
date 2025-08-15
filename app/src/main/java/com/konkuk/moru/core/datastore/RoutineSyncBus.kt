package com.konkuk.moru.core.datastore

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object RoutineSyncBus {

    sealed class Event {
        data class Like(val routineId: String, val isLiked: Boolean, val likeCount: Int) : Event()
        data class Scrap(val routineId: String, val isScrapped: Boolean) : Event()
        data class Follow(val userId: String, val isFollowing: Boolean) : Event()
    }

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 64)
    val events = _events.asSharedFlow()

    fun publish(event: Event) {
        _events.tryEmit(event) // [추가]
    }
}