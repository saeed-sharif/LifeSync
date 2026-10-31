package com.dalgeer.lifeplanner.util

import android.Manifest
import android.app.AlarmManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat

object PermissionUtils {

    /** Notification permission (Android 13+). */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /** Exact alarm permission (Android 12+). Denied by default on Android 14+. */
    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }
    }

    /**
     * Kept for existing callers (e.g. Settings screen). Now opens the system battery-optimization
     * LIST screen (no special permission / Play restriction) instead of the direct prompt.
     */
    fun requestIgnoreBatteryOptimizations(context: Context) {
        openBatterySettings(context)
    }

    private fun isXiaomiFamily(): Boolean {
        val m = Build.MANUFACTURER.lowercase()
        return m.contains("xiaomi") || m.contains("redmi") || m.contains("poco")
    }

    /** True on OEMs that kill background apps / block autostart (alarms stop working there). */
    fun needsOemBackgroundSetup(): Boolean {
        val m = Build.MANUFACTURER.lowercase()
        return isXiaomiFamily() || m.contains("vivo") || m.contains("iqoo") ||
                m.contains("oppo") || m.contains("realme") || m.contains("oneplus") ||
                m.contains("huawei") || m.contains("honor") || m.contains("samsung")
    }

    /** Opens the manufacturer's Autostart / background-start screen (Xiaomi, Vivo, Oppo...). */
    fun openAutoStartSettings(context: Context) {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val intentList: List<Intent> = when {
            isXiaomiFamily() -> listOf(
                Intent().setComponent(
                    ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                ),
                Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT),
                Intent().setComponent(
                    ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.powercenter.PowerSettings"
                    )
                )
            )
            manufacturer.contains("vivo") || manufacturer.contains("iqoo") -> listOf(
                Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
                Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
                Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.PurviewTabActivity"))
            )
            manufacturer.contains("oppo") || manufacturer.contains("realme") || manufacturer.contains("oneplus") -> listOf(
                Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
                Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
                Intent().setComponent(ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity"))
            )
            manufacturer.contains("samsung") -> listOf(
                Intent().setComponent(ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
                Intent().setComponent(ComponentName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity"))
            )
            manufacturer.contains("huawei") || manufacturer.contains("honor") -> listOf(
                Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
                Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"))
            )
            else -> emptyList()
        }

        for (intent in intentList) {
            try {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                return
            } catch (e: Exception) {
                // try next fallback
            }
        }

        openAppDetailsSettings(context)
    }

    /**
     * Opens the battery screen. On Xiaomi/MIUI/HyperOS this is the "Battery saver" page for the app
     * where "No restrictions" must be selected. Other phones get the system battery optimisation list.
     */
    fun openBatterySettings(context: Context) {
        if (isXiaomiFamily()) {
            try {
                val intent = Intent().apply {
                    component = ComponentName(
                        "com.miui.powerkeeper",
                        "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
                    )
                    putExtra("package_name", context.packageName)
                    putExtra("package_label", context.applicationInfo.loadLabel(context.packageManager).toString())
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                return
            } catch (e: Exception) {
                // fall through
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                return
            } catch (e: Exception) {
                // fall through
            }
        }

        openAppDetailsSettings(context)
    }

    /** Opens the exact alarm permission screen (Android 12+). */
    fun openExactAlarmSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                openAppDetailsSettings(context)
            }
        }
    }

    /** App notification settings (channels, lock screen, pop-up). */
    fun openNotificationSettings(context: Context) {
        try {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            } else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openAppDetailsSettings(context)
        }
    }

    /** App info page (on MIUI this has "Other permissions": lock-screen, pop-up windows). */
    fun openAppDetailsSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}