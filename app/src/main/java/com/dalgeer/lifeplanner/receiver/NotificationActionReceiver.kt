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
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        val routineId = intent.getStringExtra(EXTRA_ROUTINE_ID)
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 1001)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        when (action) {
            ACTION_MARK_DONE -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = AppDatabase.getInstance(context)
                        if (taskId != null) {
                            val taskDao = db.taskDao()
                            val taskEntityList = taskDao.getAllTasks().first()
                            val taskEntity = taskEntityList.find { it.id == taskId }
                            if (taskEntity != null) {
                                val updatedTask = taskEntity.toModel().copy(status = TaskStatus.Completed)
                                taskDao.updateTask(TaskEntity.fromModel(updatedTask))
                            }
                            AlarmScheduler.cancelTaskReminder(context, taskId)
                        } else if (routineId != null) {
                            val routineDao = db.routineDao()
                            val routineEntityList = routineDao.getAllRoutines().first()
                            val routineEntity = routineEntityList.find { it.id == routineId }
                            if (routineEntity != null) {
                                val routine = routineEntity.toModel()
                                val updatedRoutine = routine.copy(
                                    isCompletedToday = true,
                                    streak = if (!routine.isCompletedToday) routine.streak + 1 else routine.streak
                                )
                                routineDao.updateRoutine(RoutineEntity.fromModel(updatedRoutine))
                            }
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

                // Schedule snooze alarm 15 minutes (900,000 ms) from now
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
                val snoozeTimeMillis = System.currentTimeMillis() + (15 * 60 * 1000)

                val snoozeIntent = Intent(context, NotificationReceiver::class.java).apply {
                    putExtra(NotificationReceiver.EXTRA_TITLE, title)
                    putExtra(NotificationReceiver.EXTRA_MESSAGE, message)
                    putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
                    putExtra(NotificationReceiver.EXTRA_CHANNEL_ID, NotificationHelper.CHANNEL_TASK_REMINDERS)
                }

                val pendingIntent = android.app.PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    snoozeIntent,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, snoozeTimeMillis, pendingIntent)
                    } else {
                        alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, snoozeTimeMillis, pendingIntent)
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
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
