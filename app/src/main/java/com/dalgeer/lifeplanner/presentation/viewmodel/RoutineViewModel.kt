package com.dalgeer.lifeplanner.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dalgeer.lifeplanner.data.local.AppDatabase
import com.dalgeer.lifeplanner.data.model.RoutineModel
import com.dalgeer.lifeplanner.data.repository.RoomRoutineRepository
import com.dalgeer.lifeplanner.data.repository.RoutineRepository
import com.dalgeer.lifeplanner.presentation.uiState.UiState
import com.dalgeer.lifeplanner.util.AlarmScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RoutineViewModel(
    application: Application,
    private val repository: RoutineRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<UiState<List<RoutineModel>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<RoutineModel>>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.routines.collect { routines ->
                    _uiState.value = UiState.Success(routines)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }

    fun addRoutine(routine: RoutineModel) {
        viewModelScope.launch {
            repository.addRoutine(routine)
            AlarmScheduler.scheduleRoutineReminder(getApplication(), routine)
        }
    }

    fun toggleRoutineCompletion(routineId: String) {
        viewModelScope.launch {
            repository.toggleRoutineCompletion(routineId)
        }
    }

    fun deleteRoutine(routineId: String) {
        viewModelScope.launch {
            repository.deleteRoutine(routineId)
        }
    }
}
