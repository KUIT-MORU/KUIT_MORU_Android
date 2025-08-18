package com.konkuk.moru.presentation.myactivity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.domain.model.MyActRecord
import com.konkuk.moru.domain.model.MyActRecordCursor
import com.konkuk.moru.domain.repository.MyActRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class MyActRecordUi(
    val id: String,
    val title: String,
    val tags: List<String>,
    val isComplete: Boolean,
    val startedAt: LocalDate,
    val durationSec: Long = 0L
)

@HiltViewModel
class MyActRecordViewModel @Inject constructor(
    private val repo: MyActRecordRepository
) : ViewModel() {

    private val _today = MutableStateFlow<List<MyActRecordUi>>(emptyList())
    val today: StateFlow<List<MyActRecordUi>> = _today

    private val _recent = MutableStateFlow<List<MyActRecordUi>>(emptyList())
    val recent: StateFlow<List<MyActRecordUi>> = _recent

    private val _all = MutableStateFlow<List<MyActRecordUi>>(emptyList())
    val all: StateFlow<List<MyActRecordUi>> = _all

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadToday() {
        if (_loading.value) return
        _loading.value = true
        viewModelScope.launch {
            runCatching { repo.getTodayLogs() }
                .onSuccess { list ->
                    val ui = list.map { it.toUi() }
                    _today.value = ui
                    _all.value = ui
                }
                .onFailure { e -> _error.value = e.message }
            _loading.value = false
        }
    }

    fun loadRecent() {
        viewModelScope.launch {
            runCatching { repo.getRecentLogs() }
                .onSuccess { list -> _recent.value = list.map { it.toUi() } }
                .onFailure { _error.value = it.message }
        }
    }

    private var allCursor: MyActRecordCursor? = null
    private var allHasNext: Boolean = true
    private val _allLoading = MutableStateFlow(false)
    val allLoading: StateFlow<Boolean> = _allLoading  // 필요 시 화면에서 사용

    fun loadAllFirst(size: Int = 24) {
        if (_allLoading.value) return
        _allLoading.value = true
        viewModelScope.launch {
            runCatching { repo.getLogs(size = size) }
                .onSuccess { page ->
                    _all.value = page.items.map { it.toUi() }
                    allCursor = page.nextCursor
                    allHasNext = page.hasNext
                }
                .onFailure { _error.value = it.message }
            _allLoading.value = false
        }
    }

    fun loadAllNext(size: Int = 24) {
        if (_allLoading.value || !allHasNext) return
        val c = allCursor ?: return
        _allLoading.value = true
        viewModelScope.launch {
            runCatching { repo.getLogs(createdAt = c.createdAt, logId = c.logId, size = size) }
                .onSuccess { page ->
                    _all.value = _all.value + page.items.map { it.toUi() }
                    allCursor = page.nextCursor
                    allHasNext = page.hasNext
                }
                .onFailure { _error.value = it.message }
            _allLoading.value = false
        }
    }
}

private fun MyActRecord.toUi(): MyActRecordUi =
    MyActRecordUi(
        id = id,
        title = title,
        tags = tags,
        isComplete = isCompleted,
        startedAt = startedAt,
        durationSec = durationSec
    )
