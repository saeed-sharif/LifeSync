package com.dalgeer.lifeplanner.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dalgeer.lifeplanner.data.local.AppDatabase
import com.dalgeer.lifeplanner.data.model.TaskModel
import com.dalgeer.lifeplanner.data.model.TaskStatus
import com.dalgeer.lifeplanner.data.repository.RoomTaskRepository
import com.dalgeer.lifeplanner.data.repository.TaskRepository
import com.dalgeer.lifeplanner.presentation.uiState.UiState
import com.dalgeer.lifeplanner.util.AlarmScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(
    application: Application,
    private val repository: TaskRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<UiState<List<TaskModel>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<TaskModel>>> = _uiState.asStateFlow()

    private val togglingIds = HashSet<String>()

    init {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.tasks.collect { tasks ->
                    _uiState.value = UiState.Success(tasks)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }

    fun addTask(task: TaskModel) {
        viewModelScope.launch {
            repository.addTask(task)
            AlarmScheduler.scheduleTaskReminder(getApplication(), task)
        }
    }

    fun toggleTaskStatus(taskId: String) {
        synchronized(togglingIds) {
            if (togglingIds.contains(taskId)) return
            togglingIds.add(taskId)
        }

        viewModelScope.launch {
            try {
                val tasks = (uiState.value as? UiState.Success)?.data ?: emptyList()
                val task = tasks.find { it.id == taskId }
                if (task != null) {
                    val isCompleting = task.status == TaskStatus.Pending
                    repository.toggleTaskStatus(taskId)

                    if (isCompleting) {
                        AlarmScheduler.cancelTaskReminder(getApplication(), taskId)

                        // Auto-Repeat Recurring Task Logic
                        if (task.repeatOption != "Does not repeat") {
                            val nextDateStr = when (task.repeatOption) {
                                "Every Day" -> "Tomorrow"
                                "Every Week" -> "Next Week"
                                "Every Month" -> "Next Month"
                                else -> "Tomorrow"
                            }
                            val nextTask = TaskModel(
                                title = task.title,
                                category = task.category,
                                priority = task.priority,
                                date = nextDateStr,
                                time = task.time,
                                notes = task.notes,
                                repeatOption = task.repeatOption,
                                status = TaskStatus.Pending
                            )
                            repository.addTask(nextTask)
                            AlarmScheduler.scheduleTaskReminder(getApplication(), nextTask)
                        }
                    } else {
                        AlarmScheduler.scheduleTaskReminder(getApplication(), task)
                    }
                }
            } finally {
                synchronized(togglingIds) {
                    togglingIds.remove(taskId)
                }
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            AlarmScheduler.cancelTaskReminder(getApplication(), taskId)
        }
    }
}
