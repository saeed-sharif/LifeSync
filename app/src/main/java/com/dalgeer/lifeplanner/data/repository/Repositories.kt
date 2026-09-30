package com.dalgeer.lifeplanner.data.repository

import com.dalgeer.lifeplanner.data.model.*
import kotlinx.coroutines.flow.StateFlow

interface TaskRepository {
    val tasks: StateFlow<List<TaskModel>>
    fun addTask(task: TaskModel)
    fun toggleTaskStatus(taskId: String)
    fun deleteTask(taskId: String)
}

interface RoutineRepository {
    val routines: StateFlow<List<RoutineModel>>
    fun addRoutine(routine: RoutineModel)
    fun toggleRoutineCompletion(routineId: String)
    fun deleteRoutine(routineId: String)
}

interface GoalRepository {
    val goals: StateFlow<List<GoalModel>>
    fun addGoal(goal: GoalModel)
    fun toggleGoalCompletion(goalId: String)
    fun updateGoalProgress(goalId: String, newCurrentValue: String)
    fun deleteGoal(goalId: String)
}
