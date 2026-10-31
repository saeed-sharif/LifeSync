package com.dalgeer.lifeplanner.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.dalgeer.lifeplanner.data.local.AppDatabase
import com.dalgeer.lifeplanner.data.model.RoutineFrequency
import com.dalgeer.lifeplanner.data.model.RoutineModel
import com.dalgeer.lifeplanner.data.model.TaskModel
import com.dalgeer.lifeplanner.data.model.TaskStatus
import com.dalgeer.lifeplanner.receiver.NotificationReceiver
import kotlinx.coroutines.flow.first
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Single place that schedules / cancels every reminder alarm.
 *
 * Public API used by ViewModels:
 *  - scheduleTaskReminder(context, task)      -> call after add / edit task
 *  - cancelTaskReminder(context, taskId)      -> call after delete / complete task
 *  - scheduleRoutineReminder(context, routine)-> call after add / edit routine
 *  - cancelRoutineReminder(context, routineId)-> call after delete routine / reminder turned off
 *  - rescheduleAll(context)                   -> boot, app start, permission granted, time change
 */
object AlarmScheduler {

    const val ACTION_TASK_REMINDER = "com.dalgeer.lifeplanner.SHOW_TASK_REMINDER"
    const val ACTION_ROUTINE_REMINDER = "com.dalgeer.lifeplanner.SHOW_ROUTINE_REMINDER"
    const val ACTION_SNOOZE_REMINDER = "com.dalgeer.lifeplanner.SHOW_SNOOZED_REMINDER"

    private const val GRACE_MS = 120_000L
    private const val SNOOZE_MS = 15 * 60 * 1000L

    private val DATE_FORMATS = listOf(
        "MMM dd, yyyy",
        "MMM d, yyyy",
        "MMMM dd, yyyy",
        "MMMM d, yyyy",
        "dd MMM yyyy",
        "d MMM yyyy",
        "yyyy-MM-dd",
        "dd/MM/yyyy",
        "d/M/yyyy",
        "dd-MM-yyyy",
        "EEE, MMM dd, yyyy",
        "EEE, MMM d, yyyy",
        "EEEE, MMM d, yyyy",
        "EEE, dd MMM yyyy"
    )

    private val TIME_FORMATS = listOf("hh:mm a", "h:mm a", "hh:mma", "h:mma", "HH:mm")

    // ------------------------------------------------------------------
    // Low-level helpers
    // ------------------------------------------------------------------

    private fun alarmManager(context: Context): AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun reminderIntent(context: Context, reminderAction: String): Intent =
        Intent(context, NotificationReceiver::class.java).apply {
            this.action = reminderAction
        }

    private fun setInexact(am: AlarmManager, triggerAt: Long, pendingIntent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } else {
            am.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    /**
     * Exact alarm when the user allowed it. If the permission is missing we fall back to an
     * inexact (but still Doze-friendly) alarm so the reminder is delayed instead of lost.
     */
    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(context: Context, triggerAt: Long, pendingIntent: PendingIntent) {
        val am = alarmManager(context)
        try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms() ->
                    setInexact(am, triggerAt, pendingIntent)

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)

                else -> am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
        } catch (e: SecurityException) {
            try {
                setInexact(am, triggerAt, pendingIntent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cancelAlarm(context: Context, requestCode: Int, reminderAction: String) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            reminderIntent(context, reminderAction),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager(context).cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    // ------------------------------------------------------------------
    // Tasks
    // ------------------------------------------------------------------

    /**
     * @param allowGrace true  -> a task whose time passed < 2 minutes ago still fires (use when user just saved it)
     *                   false -> never fire past tasks (use for bulk reschedule on boot / app start)
     */
    fun scheduleTaskReminder(context: Context, task: TaskModel, allowGrace: Boolean = true) {
        val appContext = context.applicationContext

        if (task.status != TaskStatus.Pending) {
            cancelAlarm(appContext, task.id.hashCode(), ACTION_TASK_REMINDER)
            return
        }

        val triggerAt = nextTaskTrigger(task, System.currentTimeMillis(), allowGrace)
        if (triggerAt == null) {
            // Past, non-repeating task (or unparsable date): make sure no stale alarm remains.
            cancelAlarm(appContext, task.id.hashCode(), ACTION_TASK_REMINDER)
            return
        }

        val intent = reminderIntent(appContext, ACTION_TASK_REMINDER).apply {
            putExtra(NotificationReceiver.EXTRA_TITLE, "Task Reminder 📌")
            putExtra(NotificationReceiver.EXTRA_MESSAGE, task.title)
            putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, task.id.hashCode())
            putExtra(NotificationReceiver.EXTRA_CHANNEL_ID, NotificationHelper.CHANNEL_TASK_REMINDERS)
            putExtra(NotificationReceiver.EXTRA_TASK_ID, task.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setAlarm(appContext, triggerAt, pendingIntent)
    }

    /** Cancels the main alarm, any pending snooze and the notification already on screen. */
    fun cancelTaskReminder(context: Context, taskId: String) {
        val appContext = context.applicationContext
        cancelAlarm(appContext, taskId.hashCode(), ACTION_TASK_REMINDER)
        cancelAlarm(appContext, taskId.hashCode(), ACTION_SNOOZE_REMINDER)
        (appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .cancel(taskId.hashCode())
    }

    fun isRepeatingTask(task: TaskModel): Boolean =
        advanceByRepeatOption(Calendar.getInstance(), task.repeatOption)

    private fun nextTaskTrigger(task: TaskModel, now: Long, allowGrace: Boolean): Long? {
        val base = parseDateTimeToMillis(task.date, task.time) ?: return null

        if (base > now) return base

        if (allowGrace && now - base < GRACE_MS) return now + 5_000

        if (!isRepeatingTask(task)) return null

        val cal = Calendar.getInstance().apply { timeInMillis = base }
        var guard = 0
        while (cal.timeInMillis <= now && guard < 2000) {
            if (!advanceByRepeatOption(cal, task.repeatOption)) return null
            guard++
        }
        return if (cal.timeInMillis > now) cal.timeInMillis else null
    }

    /** Moves [cal] one repeat step forward. Returns false when the option means "does not repeat". */
    private fun advanceByRepeatOption(cal: Calendar, repeatOption: String): Boolean {
        val option = repeatOption.trim().lowercase(Locale.US)
        if (option.isBlank() || option.contains("not repeat") || option.contains("n't repeat") ||
            option.contains("never") || option == "none"
        ) return false

        when {
            option.contains("weekday") -> {
                do {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                } while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                    cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                )
            }
            option.contains("day") -> cal.add(Calendar.DAY_OF_YEAR, 1)
            option.contains("week") -> cal.add(Calendar.DAY_OF_YEAR, 7)
            option.contains("month") -> cal.add(Calendar.MONTH, 1)
            option.contains("year") -> cal.add(Calendar.YEAR, 1)
            else -> return false
        }
        return true
    }

    // ------------------------------------------------------------------
    // Routines
    // ------------------------------------------------------------------

    fun scheduleRoutineReminder(context: Context, routine: RoutineModel) {
        val appContext = context.applicationContext

        if (!routine.reminder) {
            cancelAlarm(appContext, routine.id.hashCode(), ACTION_ROUTINE_REMINDER)
            return
        }

        val triggerAt = nextRoutineTrigger(routine, System.currentTimeMillis()) ?: return

        val intent = reminderIntent(appContext, ACTION_ROUTINE_REMINDER).apply {
            putExtra(NotificationReceiver.EXTRA_TITLE, "Routine Habit Reminder 🔥")
            putExtra(NotificationReceiver.EXTRA_MESSAGE, "Time for your habit: ${routine.name}")
            putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, routine.id.hashCode())
            putExtra(NotificationReceiver.EXTRA_CHANNEL_ID, NotificationHelper.CHANNEL_ROUTINE_REMINDERS)
            putExtra(NotificationReceiver.EXTRA_ROUTINE_ID, routine.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            routine.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setAlarm(appContext, triggerAt, pendingIntent)
    }

    fun cancelRoutineReminder(context: Context, routineId: String) {
        val appContext = context.applicationContext
        cancelAlarm(appContext, routineId.hashCode(), ACTION_ROUTINE_REMINDER)
        cancelAlarm(appContext, routineId.hashCode(), ACTION_SNOOZE_REMINDER)
        (appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .cancel(routineId.hashCode())
    }

    private fun nextRoutineTrigger(routine: RoutineModel, now: Long): Long? {
        val (hour, minute) = parseTimeOfDay(routine.time) ?: return null

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        for (i in 0..7) {
            // +1s margin: if the system fires the alarm a few ms early, we must not re-arm "today" again
            if (cal.timeInMillis > now + 1_000 && isRoutineDay(routine, cal)) {
                return cal.timeInMillis
            }
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return null
    }

    fun isRoutineDay(routine: RoutineModel, cal: Calendar): Boolean {
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        return when (routine.frequency) {
            RoutineFrequency.Daily -> true
            RoutineFrequency.Weekdays -> dow != Calendar.SATURDAY && dow != Calendar.SUNDAY
            RoutineFrequency.Weekends -> dow == Calendar.SATURDAY || dow == Calendar.SUNDAY
            RoutineFrequency.Weekly, RoutineFrequency.Custom -> {
                val days = parseCustomDays(routine.customDays)
                days.isEmpty() || dow in days
            }
        }
    }

    private fun parseCustomDays(raw: String): Set<Int> {
        val text = raw.lowercase(Locale.US)
        val result = mutableSetOf<Int>()
        if (text.contains("mon")) result.add(Calendar.MONDAY)
        if (text.contains("tue")) result.add(Calendar.TUESDAY)
        if (text.contains("wed")) result.add(Calendar.WEDNESDAY)
        if (text.contains("thu")) result.add(Calendar.THURSDAY)
        if (text.contains("fri")) result.add(Calendar.FRIDAY)
        if (text.contains("sat")) result.add(Calendar.SATURDAY)
        if (text.contains("sun")) result.add(Calendar.SUNDAY)
        return result
    }

    // ------------------------------------------------------------------
    // Snooze
    // ------------------------------------------------------------------

    fun scheduleSnooze(
        context: Context,
        title: String,
        message: String,
        notificationId: Int,
        channelId: String,
        taskId: String?,
        routineId: String?,
        delayMs: Long = SNOOZE_MS
    ) {
        val appContext = context.applicationContext

        val intent = reminderIntent(appContext, ACTION_SNOOZE_REMINDER).apply {
            putExtra(NotificationReceiver.EXTRA_TITLE, title)
            putExtra(NotificationReceiver.EXTRA_MESSAGE, message)
            putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(NotificationReceiver.EXTRA_CHANNEL_ID, channelId)
            if (taskId != null) putExtra(NotificationReceiver.EXTRA_TASK_ID, taskId)
            if (routineId != null) putExtra(NotificationReceiver.EXTRA_ROUTINE_ID, routineId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setAlarm(appContext, System.currentTimeMillis() + delayMs, pendingIntent)
    }

    fun cancelSnooze(context: Context, notificationId: Int) {
        cancelAlarm(context.applicationContext, notificationId, ACTION_SNOOZE_REMINDER)
    }

    // ------------------------------------------------------------------
    // Bulk reschedule (boot, app start, time change, permission granted)
    // ------------------------------------------------------------------

    suspend fun rescheduleAll(context: Context) {
        val appContext = context.applicationContext
        val db = AppDatabase.getInstance(appContext)

        for (entity in db.taskDao().getAllTasks().first()) {
            val task = entity.toModel()
            if (task.status == TaskStatus.Pending) {
                scheduleTaskReminder(appContext, task, allowGrace = false)
            }
        }

        for (entity in db.routineDao().getAllRoutines().first()) {
            val routine = entity.toModel()
            scheduleRoutineReminder(appContext, routine)
        }
    }

    // ------------------------------------------------------------------
    // Date / time parsing (public: also used by TaskModel.isOverdue)
    // ------------------------------------------------------------------

    fun parseDateTimeToMillis(dateStr: String, timeStr: String): Long? {
        return try {
            val calendar = Calendar.getInstance()
            val cleanDate = dateStr.trim()

            when {
                cleanDate.startsWith("Today", ignoreCase = true) -> Unit
                cleanDate.startsWith("Tomorrow", ignoreCase = true) -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                cleanDate.startsWith("Next Week", ignoreCase = true) -> calendar.add(Calendar.DAY_OF_YEAR, 7)
                cleanDate.startsWith("Next Month", ignoreCase = true) -> calendar.add(Calendar.MONTH, 1)
                else -> {
                    val parsed = parseDateOnly(cleanDate) ?: return null
                    val dateCal = Calendar.getInstance().apply { time = parsed }
                    calendar.set(
                        dateCal.get(Calendar.YEAR),
                        dateCal.get(Calendar.MONTH),
                        dateCal.get(Calendar.DAY_OF_MONTH)
                    )
                }
            }

            val (hour, minute) = parseTimeOfDay(timeStr) ?: return null
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseDateOnly(raw: String): Date? {
        val candidates = LinkedHashSet<String>()
        candidates.add(raw.trim())
        if (raw.contains(",")) candidates.add(raw.substringAfter(",").trim())

        for (candidate in candidates) {
            for (format in DATE_FORMATS) {
                for (locale in listOf(Locale.US, Locale.getDefault())) {
                    try {
                        val sdf = SimpleDateFormat(format, locale).apply { isLenient = false }
                        val pos = ParsePosition(0)
                        val parsed = sdf.parse(candidate, pos)
                        if (parsed != null && pos.index == candidate.length) return parsed
                    } catch (_: Exception) {
                    }
                }
            }
        }
        return null
    }

    private fun parseTimeOfDay(raw: String): Pair<Int, Int>? {
        val clean = raw.replace('\u202F', ' ').replace('\u00A0', ' ').trim()

        for (format in TIME_FORMATS) {
            for (locale in listOf(Locale.US, Locale.getDefault())) {
                try {
                    val sdf = SimpleDateFormat(format, locale).apply { isLenient = false }
                    val pos = ParsePosition(0)
                    val parsed = sdf.parse(clean, pos)
                    if (parsed != null && pos.index == clean.length) {
                        val cal = Calendar.getInstance().apply { time = parsed }
                        return Pair(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
                    }
                } catch (_: Exception) {
                }
            }
        }
        return null
    }
}