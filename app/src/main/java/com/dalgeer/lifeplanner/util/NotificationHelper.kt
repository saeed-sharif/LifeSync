package com.dalgeer.lifeplanner.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dalgeer.lifeplanner.MainActivity
import com.dalgeer.lifeplanner.R
import com.dalgeer.lifeplanner.receiver.NotificationActionReceiver

object NotificationHelper {

    // Channel settings (sound, importance) can't be changed after creation, so the id is bumped (v5)
    // and the old v4 channels are deleted.
    const val CHANNEL_TASK_REMINDERS = "task_reminders_channel_alarm_v5"
    const val CHANNEL_ROUTINE_REMINDERS = "routine_reminders_channel_alarm_v5"

    private val OLD_CHANNEL_IDS = listOf(
        "task_reminders_channel_alarm_v4",
        "routine_reminders_channel_alarm_v4"
    )

    private val VIBRATION_PATTERN = longArrayOf(0, 800, 400, 800, 400, 800)

    private const val AUTO_DISMISS_MS = 60_000L

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        OLD_CHANNEL_IDS.forEach { id ->
            if (manager.getNotificationChannel(id) != null) manager.deleteNotificationChannel(id)
        }

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        fun buildChannel(id: String, name: String, desc: String): NotificationChannel =
            NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = desc
                setSound(alarmSound, audioAttributes)
                enableVibration(true)
                vibrationPattern = VIBRATION_PATTERN
                enableLights(true)
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

        if (manager.getNotificationChannel(CHANNEL_TASK_REMINDERS) == null) {
            manager.createNotificationChannel(
                buildChannel(
                    CHANNEL_TASK_REMINDERS,
                    "Task Alarms & Reminders",
                    "Alarm-style alerts for scheduled tasks"
                )
            )
        }
        if (manager.getNotificationChannel(CHANNEL_ROUTINE_REMINDERS) == null) {
            manager.createNotificationChannel(
                buildChannel(
                    CHANNEL_ROUTINE_REMINDERS,
                    "Routine Alarms & Reminders",
                    "Alarm-style alerts for daily routine habits"
                )
            )
        }
    }

    fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int,
        channelId: String = CHANNEL_TASK_REMINDERS,
        taskId: String? = null,
        routineId: String? = null
    ) {
        if (!PermissionUtils.hasNotificationPermission(context)) return

        createNotificationChannels(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setTimeoutAfter(AUTO_DISMISS_MS)

        // On Android 8+ sound/vibration come from the channel. setSound/setDefaults here would
        // override the alarm sound, so they are only used on older versions.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            builder.setSound(alarmSound)
            builder.setVibrate(VIBRATION_PATTERN)
        }

        if (taskId != null || routineId != null) {
            val markDoneIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.ACTION_MARK_DONE
                putExtra(NotificationActionReceiver.EXTRA_TASK_ID, taskId)
                putExtra(NotificationActionReceiver.EXTRA_ROUTINE_ID, routineId)
                putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
            }
            val markDonePendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId * 10 + 1,
                markDoneIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(R.mipmap.ic_launcher, "Mark Done", markDonePendingIntent)

            val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.ACTION_SNOOZE
                putExtra(NotificationActionReceiver.EXTRA_TASK_ID, taskId)
                putExtra(NotificationActionReceiver.EXTRA_ROUTINE_ID, routineId)
                putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
                putExtra(NotificationActionReceiver.EXTRA_TITLE, title)
                putExtra(NotificationActionReceiver.EXTRA_MESSAGE, message)
            }
            val snoozePendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId * 10 + 2,
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(R.mipmap.ic_launcher, "Snooze 15m", snoozePendingIntent)
        }

        val notification = builder.build()
        // Keep ringing/vibrating until dismissed (or the 60s timeout), like a real alarm.
        notification.flags = notification.flags or Notification.FLAG_INSISTENT

        try {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}