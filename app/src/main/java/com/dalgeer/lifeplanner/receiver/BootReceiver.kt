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

/**
 * Re-arms every alarm after: reboot, app update, time/timezone change, or when the user grants
 * the exact-alarm permission (all alarms are wiped by the system in those cases).
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action !in HANDLED_ACTIONS) return

        val appContext = context.applicationContext
        val isBoot = action == Intent.ACTION_BOOT_COMPLETED

        if (isBoot) {
            val timeString = SimpleDateFormat("hh:mm:ss a, dd MMM yyyy", Locale.getDefault()).format(Date())
            AppPreferences.setLastRestartTime(appContext, timeString)
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (isBoot) notifyMissedTasks(appContext)
                AlarmScheduler.rescheduleAll(appContext)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    /**
     * Shows a "missed" notification only for non-repeating tasks that became due during the last
     * 24 hours while the phone was off, and only once per task time.
     */
    private suspend fun notifyMissedTasks(context: Context) {
        val prefs = context.getSharedPreferences(MISSED_PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()

        val tasks = AppDatabase.getInstance(context).taskDao().getAllTasks().first()
        for (entity in tasks) {
            val task = entity.toModel()
            if (task.status != TaskStatus.Pending) continue
            if (AlarmScheduler.isRepeatingTask(task)) continue

            val due = AlarmScheduler.parseDateTimeToMillis(task.date, task.time) ?: continue
            if (due >= now || now - due > MISSED_WINDOW_MS) continue
            if (prefs.getLong(task.id, 0L) == due) continue // already notified

            NotificationHelper.showNotification(
                context = context,
                title = "Missed Task Reminder ⚠️",
                message = "You missed: ${task.title} (Scheduled at ${task.time})",
                notificationId = task.id.hashCode(),
                channelId = NotificationHelper.CHANNEL_TASK_REMINDERS,
                taskId = task.id
            )
            prefs.edit().putLong(task.id, due).apply()
        }
    }

    private companion object {
        const val MISSED_PREFS = "missed_task_notifications"
        const val MISSED_WINDOW_MS = 24 * 60 * 60 * 1000L

        val HANDLED_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED"
        )
    }
}