package com.konkuk.moru.presentation.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.mapper.toDomain
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.repositoryimpl.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeRoutinesViewModel @Inject constructor(
    private val repo: RoutineRepository
) : ViewModel() {

    private companion object {
        private const val TAG = "HomeRoutinesVM"
    }

    private val _serverRoutines = MutableStateFlow<List<Routine>>(emptyList())
    val serverRoutines: StateFlow<List<Routine>> = _serverRoutines

    fun loadTodayRoutines(page: Int = 0, size: Int = 20) {
        viewModelScope.launch {
            runCatching { repo.getMyRoutinesToday(page, size) }
                .onSuccess { pageRes ->
                    Log.d(TAG, "loadTodayRoutines success: " +
                            "total=${pageRes.totalElements}, page=${pageRes.number}, size=${pageRes.size}, " +
                            "contentSize=${pageRes.content.size}"
                    )
                    Log.d(TAG, "server IDs=" + pageRes.content.joinToString { it.routineId })

                    _serverRoutines.value = pageRes.content.map { it.toDomain() }
                }
                .onFailure { t ->
                    // [추가] 실패 로그
                    Log.e(TAG, "loadTodayRoutines failed", t)
                    _serverRoutines.value = emptyList()
                }
        }
    }
}
