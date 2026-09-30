package com.dalgeer.lifeplanner.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.dalgeer.lifeplanner.MainActivity
import com.dalgeer.lifeplanner.data.model.RoutineModel
import com.dalgeer.lifeplanner.data.model.TaskModel
import com.dalgeer.lifeplanner.receiver.NotificationReceiver
import java.text.SimpleDateFormat
import java.util.*

object AlarmScheduler {

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleTaskReminder(context: Context, task: TaskModel) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timeInMillis = parseDateTimeToMillis(task.date, task.time) ?: return

        val now = System.currentTimeMillis()
        // If the task time is in the past by more than 1 minute, don't schedule.
        // If it was created for the current minute, schedule for 5 seconds from now so it triggers!
        val targetTime = if (timeInMillis <= now) {
            if (now - timeInMillis < 120_000) {
                now + 5_000
            } else {
                return
            }
        } else {
            timeInMillis
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "com.dalgeer.lifeplanner.SHOW_TASK_REMINDER"
            putExtra(NotificationReceiver.EXTRA_TITLE, "Task Reminder 📌")
            putExtra(NotificationReceiver.EXTRA_MESSAGE, task.title)
            putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, task.id.hashCode())
            putExtra(NotificationReceiver.EXTRA_CHANNEL_ID, NotificationHelper.CHANNEL_TASK_REMINDERS)
            putExtra(NotificationReceiver.EXTRA_TASK_ID, task.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, MainActivity::class.java)
        val showPendingIntent = PendingIntent.getActivity(
            context,
            task.id.hashCode(),
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (PermissionUtils.canScheduleExactAlarms(context)) {
                val alarmClockInfo = AlarmManager.AlarmClockInfo(targetTime, showPendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent)
                }
            }
        } catch (e: Exception) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun cancelTaskReminder(context: Context, taskId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleRoutineReminder(context: Context, routine: RoutineModel) {
        if (!routine.reminder) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timeInMillis = parseTimeToMillisToday(routine.time) ?: return

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "com.dalgeer.lifeplanner.SHOW_ROUTINE_REMINDER"
            putExtra(NotificationReceiver.EXTRA_TITLE, "Routine Habit Reminder 🔥")
            putExtra(NotificationReceiver.EXTRA_MESSAGE, "Time for your habit: ${routine.name}")
            putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, routine.id.hashCode())
            putExtra(NotificationReceiver.EXTRA_CHANNEL_ID, NotificationHelper.CHANNEL_ROUTINE_REMINDERS)
            putExtra(NotificationReceiver.EXTRA_ROUTINE_ID, routine.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            routine.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, MainActivity::class.java)
        val showPendingIntent = PendingIntent.getActivity(
            context,
            routine.id.hashCode(),
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (PermissionUtils.canScheduleExactAlarms(context)) {
                val alarmClockInfo = AlarmManager.AlarmClockInfo(timeInMillis, showPendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
                }
            }
        } catch (e: Exception) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun parseDateTimeToMillis(dateStr: String, timeStr: String): Long? {
        return try {
            val calendar = Calendar.getInstance()

            // 1. Date Parsing
            if (dateStr.equals("Today", ignoreCase = true)) {
                // Keep today's date
            } else if (dateStr.equals("Tomorrow", ignoreCase = true)) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            } else if (dateStr.equals("Next Week", ignoreCase = true)) {
                calendar.add(Calendar.DAY_OF_YEAR, 7)
            } else if (dateStr.equals("Next Month", ignoreCase = true)) {
                calendar.add(Calendar.MONTH, 1)
            } else {
                val parsedDate = try {
                    SimpleDateFormat("MMM dd, yyyy", Locale.US).parse(dateStr)
                } catch (e: Exception) {
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(dateStr)
                }
                if (parsedDate != null) {
                    val dateCal = Calendar.getInstance()
                    dateCal.time = parsedDate
                    calendar.set(Calendar.YEAR, dateCal.get(Calendar.YEAR))
                    calendar.set(Calendar.MONTH, dateCal.get(Calendar.MONTH))
                    calendar.set(Calendar.DAY_OF_MONTH, dateCal.get(Calendar.DAY_OF_MONTH))
                }
            }

            // 2. Time Parsing
            val parsedTime = try {
                SimpleDateFormat("hh:mm a", Locale.US).parse(timeStr)
            } catch (e: Exception) {
                SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(timeStr)
            }

            if (parsedTime != null) {
                val timeCal = Calendar.getInstance()
                timeCal.time = parsedTime
                calendar.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }

            calendar.timeInMillis
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseTimeToMillisToday(timeStr: String): Long? {
        return try {
            val calendar = Calendar.getInstance()
            val parsedTime = try {
                SimpleDateFormat("hh:mm a", Locale.US).parse(timeStr)
            } catch (e: Exception) {
                SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(timeStr)
            }

            if (parsedTime != null) {
                val timeCal = Calendar.getInstance()
                timeCal.time = parsedTime
                calendar.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                // If time has passed today, schedule for tomorrow
                if (calendar.timeInMillis <= System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            calendar.timeInMillis
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
