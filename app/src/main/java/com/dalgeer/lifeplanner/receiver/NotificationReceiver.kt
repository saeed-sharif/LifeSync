package com.dalgeer.lifeplanner.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.dalgeer.lifeplanner.data.local.AppDatabase
import com.dalgeer.lifeplanner.data.model.TaskStatus
import com.dalgeer.lifeplanner.data.repository.RoomRoutineRepository
import com.dalgeer.lifeplanner.util.AlarmScheduler
import com.dalgeer.lifeplanner.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Fired by AlarmManager (task, routine or snooze alarm). Decides whether the reminder is still
 * valid (not deleted / not completed), re-arms repeating alarms, then shows the notification.
 */
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val appContext = context.applicationContext
        val wakeLock = acquireWakeLock(appContext)
        val pendingResult = goAsync()

        val reminderAction = intent.action
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "LifeSync Reminder 📌"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "You have a task scheduled for now."
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 1001)
        val channelId = intent.getStringExtra(EXTRA_CHANNEL_ID) ?: NotificationHelper.CHANNEL_TASK_REMINDERS
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        val routineId = intent.getStringExtra(EXTRA_ROUTINE_ID)

        CoroutineScope(Dispatchers.IO).launch {
            var shouldShow = true
            try {
                val db = AppDatabase.getInstance(appContext)

                if (routineId != null) {
                    // Make sure "completed today" is not a leftover from yesterday.
                    RoomRoutineRepository.checkAndResetDailyRoutines(appContext, db.routineDao())

                    val routine = db.routineDao().getAllRoutines().first()
                        .find { it.id == routineId }?.toModel()

                    if (routine == null) {
                        shouldShow = false // routine was deleted
                    } else {
                        // Arm tomorrow's / next allowed day's alarm first.
                        if (reminderAction == AlarmScheduler.ACTION_ROUTINE_REMINDER) {
                            AlarmScheduler.scheduleRoutineReminder(appContext, routine)
                        }
                        if (!routine.reminder || routine.isCompletedToday) {
                            shouldShow = false
                        }
                    }
                } else if (taskId != null) {
                    val task = db.taskDao().getAllTasks().first()
                        .find { it.id == taskId }?.toModel()

                    if (task == null || task.status == TaskStatus.Completed) {
                        shouldShow = false // deleted or already done
                    } else if (reminderAction == AlarmScheduler.ACTION_TASK_REMINDER &&
                        AlarmScheduler.isRepeatingTask(task)
                    ) {
                        AlarmScheduler.scheduleTaskReminder(appContext, task, allowGrace = false)
                    }
                }
            } catch (e: Exception) {
                // On any DB problem, still show the reminder (fail-safe).
                e.printStackTrace()
            }

            try {
                if (shouldShow) {
                    NotificationHelper.showNotification(
                        context = appContext,
                        title = title,
                        message = message,
                        notificationId = notificationId,
                        channelId = channelId,
                        taskId = taskId,
                        routineId = routineId
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    if (wakeLock?.isHeld == true) wakeLock.release()
                } catch (_: Exception) {
                }
                pendingResult.finish()
            }
        }
    }

    private fun acquireWakeLock(context: Context): PowerManager.WakeLock? {
        return try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "LifeSync:NotificationReceiverWakeLock"
            ).also { it.acquire(10_000) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_CHANNEL_ID = "extra_channel_id"
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_ROUTINE_ID = "extra_routine_id"
    }
}