package com.dalgeer.lifeplanner.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dalgeer.lifeplanner.data.model.*

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val priority: String,
    val date: String,
    val time: String,
    val notes: String,
    val repeatOption: String,
    val status: String
) {
    fun toModel() = TaskModel(
        id = id,
        title = title,
        category = try { TaskCategory.valueOf(category) } catch (e: Exception) { TaskCategory.General },
        priority = try { TaskPriority.valueOf(priority) } catch (e: Exception) { TaskPriority.Medium },
        date = date,
        time = time,
        notes = notes,
        repeatOption = repeatOption,
        status = try { TaskStatus.valueOf(status) } catch (e: Exception) { TaskStatus.Pending }
    )

    companion object {
        fun fromModel(model: TaskModel) = TaskEntity(
            id = model.id,
            title = model.title,
            category = model.category.name,
            priority = model.priority.name,
            date = model.date,
            time = model.time,
            notes = model.notes,
            repeatOption = model.repeatOption,
            status = model.status.name
        )
    }
}

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey val id: String,
    val name: String,
    val timeOfDay: String,
    val frequency: String,
    val customDays: String = "",
    val time: String,
    val reminder: Boolean,
    val notes: String,
    val streak: Int,
    val isCompletedToday: Boolean
) {
    fun toModel() = RoutineModel(
        id = id,
        name = name,
        timeOfDay = try { RoutineTimeOfDay.valueOf(timeOfDay) } catch (e: Exception) { RoutineTimeOfDay.Morning },
        frequency = try { RoutineFrequency.valueOf(frequency) } catch (e: Exception) { RoutineFrequency.Daily },
        customDays = customDays,
        time = time,
        reminder = reminder,
        notes = notes,
        streak = streak,
        isCompletedToday = isCompletedToday
    )

    companion object {
        fun fromModel(model: RoutineModel) = RoutineEntity(
            id = model.id,
            name = model.name,
            timeOfDay = model.timeOfDay.name,
            frequency = model.frequency.name,
            customDays = model.customDays,
            time = model.time,
            reminder = model.reminder,
            notes = model.notes,
            streak = model.streak,
            isCompletedToday = model.isCompletedToday
        )
    }
}

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,
    val targetDate: String,
    val targetMetricValue: String,
    val currentMetricValue: String,
    val notes: String,
    val isCompleted: Boolean
) {
    fun toModel() = GoalModel(
        id = id,
        title = title,
        type = try { GoalType.valueOf(type) } catch (e: Exception) { GoalType.Personal },
        targetDate = targetDate,
        targetMetricValue = targetMetricValue,
        currentMetricValue = currentMetricValue,
        notes = notes,
        isCompleted = isCompleted
    )

    companion object {
        fun fromModel(model: GoalModel) = GoalEntity(
            id = model.id,
            title = model.title,
            type = model.type.name,
            targetDate = model.targetDate,
            targetMetricValue = model.targetMetricValue,
            currentMetricValue = model.currentMetricValue,
            notes = model.notes,
            isCompleted = model.isCompleted
        )
    }
}
