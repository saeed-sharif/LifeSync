package com.dalgeer.lifeplanner.data.repository

import android.content.Context
import com.dalgeer.lifeplanner.data.local.dao.*
import com.dalgeer.lifeplanner.data.local.entities.*
import com.dalgeer.lifeplanner.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RoomTaskRepository(private val taskDao: TaskDao) : TaskRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    override val tasks: StateFlow<List<TaskModel>> = taskDao.getAllTasks()
        .map { entities -> entities.map { it.toModel() } }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    override fun addTask(task: TaskModel) {
        scope.launch {
            taskDao.insertTask(TaskEntity.fromModel(task))
        }
    }

    override fun toggleTaskStatus(taskId: String) {
        scope.launch {
            val current = tasks.value.find { it.id == taskId }
            if (current != null) {
                val updatedStatus = if (current.status == TaskStatus.Pending) TaskStatus.Completed else TaskStatus.Pending
                val updatedTask = current.copy(status = updatedStatus)
                taskDao.updateTask(TaskEntity.fromModel(updatedTask))
            }
        }
    }

    override fun deleteTask(taskId: String) {
        scope.launch {
            taskDao.deleteTask(taskId)
        }
    }
}

class RoomRoutineRepository(
    private val routineDao: RoutineDao,
    private val context: Context
) : RoutineRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            checkAndResetDailyRoutines(context, routineDao)
        }
    }

    override val routines: StateFlow<List<RoutineModel>> = routineDao.getAllRoutines()
        .map { entities -> entities.map { it.toModel() } }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    override fun addRoutine(routine: RoutineModel) {
        scope.launch {
            routineDao.insertRoutine(RoutineEntity.fromModel(routine))
        }
    }

    override fun toggleRoutineCompletion(routineId: String) {
        scope.launch {
            val current = routines.value.find { it.id == routineId }
            if (current != null) {
                val newCompleted = !current.isCompletedToday
                val updatedRoutine = current.copy(
                    isCompletedToday = newCompleted,
                    streak = if (newCompleted) current.streak + 1 else maxOf(0, current.streak - 1)
                )
                routineDao.updateRoutine(RoutineEntity.fromModel(updatedRoutine))
            }
        }
    }

    override fun deleteRoutine(routineId: String) {
        scope.launch {
            routineDao.deleteRoutine(routineId)
        }
    }

    companion object {
        suspend fun checkAndResetDailyRoutines(context: Context, routineDao: RoutineDao) {
            val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
            val lastReset = com.dalgeer.lifeplanner.data.preferences.AppPreferences.getLastResetDate(context)

            if (lastReset != todayStr) {
                val routines = routineDao.getAllRoutines().first()
                routines.forEach { entity ->
                    val model = entity.toModel()
                    val updatedStreak = if (!model.isCompletedToday) maxOf(0, model.streak - 1) else model.streak
                    val updatedModel = model.copy(
                        isCompletedToday = false,
                        streak = updatedStreak
                    )
                    routineDao.updateRoutine(RoutineEntity.fromModel(updatedModel))
                }
                com.dalgeer.lifeplanner.data.preferences.AppPreferences.setLastResetDate(context, todayStr)
            }
        }
    }
}

class RoomGoalRepository(private val goalDao: GoalDao) : GoalRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    override val goals: StateFlow<List<GoalModel>> = goalDao.getAllGoals()
        .map { entities -> entities.map { it.toModel() } }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    override fun addGoal(goal: GoalModel) {
        scope.launch {
            goalDao.insertGoal(GoalEntity.fromModel(goal))
        }
    }

    override fun toggleGoalCompletion(goalId: String) {
        scope.launch {
            val currentGoal = goals.value.find { it.id == goalId }
            if (currentGoal != null) {
                val updatedGoal = currentGoal.copy(isCompleted = !currentGoal.isCompleted)
                goalDao.updateGoal(GoalEntity.fromModel(updatedGoal))
            }
        }
    }

    override fun updateGoalProgress(goalId: String, newCurrentValue: String) {
        scope.launch {
            val currentGoal = goals.value.find { it.id == goalId }
            if (currentGoal != null) {
                val target = currentGoal.getTargetFloat()
                val current = com.dalgeer.lifeplanner.data.model.extractFloatValue(newCurrentValue) ?: 0f
                val isNowCompleted = target > 0f && current >= target
                val updatedGoal = currentGoal.copy(
                    currentMetricValue = newCurrentValue,
                    isCompleted = isNowCompleted
                )
                goalDao.updateGoal(GoalEntity.fromModel(updatedGoal))
            }
        }
    }

    override fun deleteGoal(goalId: String) {
        scope.launch {
            goalDao.deleteGoal(goalId)
        }
    }
}
