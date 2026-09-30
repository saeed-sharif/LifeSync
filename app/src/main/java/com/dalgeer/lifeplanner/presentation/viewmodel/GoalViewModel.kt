package com.dalgeer.lifeplanner.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dalgeer.lifeplanner.data.local.AppDatabase
import com.dalgeer.lifeplanner.data.model.GoalModel
import com.dalgeer.lifeplanner.data.repository.RoomGoalRepository
import com.dalgeer.lifeplanner.data.repository.GoalRepository
import com.dalgeer.lifeplanner.presentation.uiState.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GoalViewModel(
    application: Application,
    private val repository: GoalRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<UiState<List<GoalModel>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<GoalModel>>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.goals.collect { goals ->
                    _uiState.value = UiState.Success(goals)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }

    fun addGoal(goal: GoalModel) {
        viewModelScope.launch {
            repository.addGoal(goal)
        }
    }

    fun toggleGoalCompletion(goalId: String) {
        viewModelScope.launch {
            repository.toggleGoalCompletion(goalId)
        }
    }

    fun updateGoalProgress(goalId: String, newCurrentValue: String) {
        viewModelScope.launch {
            repository.updateGoalProgress(goalId, newCurrentValue)
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
        }
    }
}
