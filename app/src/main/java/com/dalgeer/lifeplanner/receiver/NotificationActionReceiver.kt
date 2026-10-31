package com.dalgeer.lifeplanner.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dalgeer.lifeplanner.data.local.AppDatabase
import com.dalgeer.lifeplanner.data.local.entities.RoutineEntity
import com.dalgeer.lifeplanner.data.local.entities.TaskEntity
import com.dalgeer.lifeplanner.data.model.TaskStatus
import com.dalgeer.lifeplanner.util.AlarmScheduler
import com.dalgeer.lifeplanner.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val appContext = context.applicationContext
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        val routineId = intent.getStringExtra(EXTRA_ROUTINE_ID)
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 1001)

        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        when (action) {
            ACTION_MARK_DONE -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = AppDatabase.getInstance(appContext)
                        if (taskId != null) {
                            val taskDao = db.taskDao()
                            val taskEntity = taskDao.getAllTasks().first().find { it.id == taskId }
                            if (taskEntity != null) {
                                val updatedTask = taskEntity.toModel().copy(status = TaskStatus.Completed)
                                taskDao.updateTask(TaskEntity.fromModel(updatedTask))
                            }
                            // Cancels main alarm + pending snooze
                            AlarmScheduler.cancelTaskReminder(appContext, taskId)
                        } else if (routineId != null) {
                            val routineDao = db.routineDao()
                            val routineEntity = routineDao.getAllRoutines().first().find { it.id == routineId }
                            if (routineEntity != null) {
                                val routine = routineEntity.toModel()
                                val updatedRoutine = routine.copy(
                                    isCompletedToday = true,
                                    streak = if (!routine.isCompletedToday) routine.streak + 1 else routine.streak
                                )
                                routineDao.updateRoutine(RoutineEntity.fromModel(updatedRoutine))
                            }
                            // Only the snooze is cancelled; the daily alarm keeps running.
                            AlarmScheduler.cancelSnooze(appContext, routineId.hashCode())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

            ACTION_SNOOZE -> {
                val title = intent.getStringExtra(EXTRA_TITLE) ?: "Snoozed Reminder 📌"
                val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Reminder snoozed for 15 minutes."
                val channelId = if (routineId != null) {
                    NotificationHelper.CHANNEL_ROUTINE_REMINDERS
                } else {
                    NotificationHelper.CHANNEL_TASK_REMINDERS
                }

                // taskId / routineId are passed on so the snoozed notification keeps its buttons.
                AlarmScheduler.scheduleSnooze(
                    context = appContext,
                    title = title,
                    message = message,
                    notificationId = notificationId,
                    channelId = channelId,
                    taskId = taskId,
                    routineId = routineId
                )
            }
        }
    }

    companion object {
        const val ACTION_MARK_DONE = "com.dalgeer.lifeplanner.ACTION_MARK_DONE"
        const val ACTION_SNOOZE = "com.dalgeer.lifeplanner.ACTION_SNOOZE"

        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_ROUTINE_ID = "extra_routine_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
    }
}