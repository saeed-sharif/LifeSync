package com.dalgeer.lifeplanner.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dalgeer.lifeplanner.data.local.AppDatabase
import com.dalgeer.lifeplanner.util.AlarmScheduler
import com.dalgeer.lifeplanner.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "LifeSync Reminder 📌"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "You have a task scheduled for now."
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 1001)
        val channelId = intent.getStringExtra(EXTRA_CHANNEL_ID) ?: NotificationHelper.CHANNEL_TASK_REMINDERS
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        val routineId = intent.getStringExtra(EXTRA_ROUTINE_ID)

        // If it's a routine alarm, automatically schedule the next day's reminder
        if (routineId != null) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getInstance(context)
                    val routineEntity = db.routineDao().getAllRoutines().first().find { it.id == routineId }
                    if (routineEntity != null) {
                        val routine = routineEntity.toModel()
                        if (routine.reminder) {
                            AlarmScheduler.scheduleRoutineReminder(context, routine)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }

        NotificationHelper.showNotification(
            context = context,
            title = title,
            message = message,
            notificationId = notificationId,
            channelId = channelId,
            taskId = taskId,
            routineId = routineId
        )
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
