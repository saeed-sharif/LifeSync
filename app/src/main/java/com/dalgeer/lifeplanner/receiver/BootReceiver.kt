package com.dalgeer.lifeplanner.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dalgeer.lifeplanner.data.local.AppDatabase
import com.dalgeer.lifeplanner.data.model.TaskStatus
import com.dalgeer.lifeplanner.data.preferences.AppPreferences
import com.dalgeer.lifeplanner.util.AlarmScheduler
import com.dalgeer.lifeplanner.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        // Handling standard and manufacturer-specific boot/restart events
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            // 1. Generate the formatted date-time string
            val timeString = SimpleDateFormat(
                "hh:mm:ss a, dd MMM yyyy",
                Locale.getDefault()
            ).format(Date())

            // 2. Save last restart time in preferences for Settings display
            AppPreferences.setLastRestartTime(context, timeString)

            // 3. Fetch all tasks and routines from database
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = AppDatabase.getInstance(context)
                    val now = System.currentTimeMillis()

                    // Process pending tasks: future ones get scheduled, past/missed ones trigger a missed notification
                    val taskEntities = database.taskDao().getAllTasks().first()
                    for (entity in taskEntities) {
                        val task = entity.toModel()
                        if (task.status == TaskStatus.Pending) {
                            val timeInMillis = AlarmScheduler.parseDateTimeToMillis(task.date, task.time)
                            if (timeInMillis != null) {
                                if (timeInMillis > now) {
                                    // Future task -> schedule reminder alarm
                                    AlarmScheduler.scheduleTaskReminder(context, task)
                                } else {
                                    // Missed task (time passed while phone was off / restarting) -> show missed notification
                                    NotificationHelper.showNotification(
                                        context = context,
                                        title = "Missed Task Reminder ⚠️",
                                        message = "You missed: ${task.title} (Scheduled at ${task.time})",
                                        notificationId = task.id.hashCode(),
                                        channelId = NotificationHelper.CHANNEL_TASK_REMINDERS,
                                        taskId = task.id
                                    )
                                }
                            }
                        }
                    }

                    // Reschedule routines with reminders enabled
                    val routineEntities = database.routineDao().getAllRoutines().first()
                    for (entity in routineEntities) {
                        val routine = entity.toModel()
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
    }
}
