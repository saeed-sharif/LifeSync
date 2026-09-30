package com.dalgeer.lifeplanner.ui.common

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dalgeer.lifeplanner.ui.theme.PrimaryPurple
import com.dalgeer.lifeplanner.ui.theme.TextDark
import com.dalgeer.lifeplanner.ui.theme.TextGray
import com.dalgeer.lifeplanner.ui.theme.White
import com.dalgeer.lifeplanner.util.PermissionUtils

@Composable
fun NotificationAndAlarmPermissionHandler(
    showDialogIfMissing: Boolean = true,
    onPermissionResult: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var hasNotifPermission by remember { mutableStateOf(PermissionUtils.hasNotificationPermission(context)) }
    var hasExactAlarmPermission by remember { mutableStateOf(PermissionUtils.canScheduleExactAlarms(context)) }
    var isBatteryOptimized by remember { mutableStateOf(!PermissionUtils.isIgnoringBatteryOptimizations(context)) }

    var showNotifDisclosureDialog by remember { mutableStateOf(false) }
    var showAlarmDisclosureDialog by remember { mutableStateOf(false) }
    var showBatteryDisclosureDialog by remember { mutableStateOf(false) }

    // Launcher for POST_NOTIFICATIONS permission (Android 13+)
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotifPermission = isGranted
        onPermissionResult(isGranted)
        if (!hasExactAlarmPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            showAlarmDisclosureDialog = true
        } else if (isBatteryOptimized) {
            showBatteryDisclosureDialog = true
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotifPermission && showDialogIfMissing) {
            showNotifDisclosureDialog = true
        } else if (!hasExactAlarmPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && showDialogIfMissing) {
            showAlarmDisclosureDialog = true
        } else if (isBatteryOptimized && showDialogIfMissing) {
            showBatteryDisclosureDialog = true
        }
    }

    // 1. Prominent Disclosure: Notification Permission (Google Play Compliant)
    if (showNotifDisclosureDialog) {
        ProminentDisclosureDialog(
            icon = Icons.Outlined.NotificationsActive,
            iconTint = PrimaryPurple,
            title = "Permission Notice: Notifications",
            subtitle = "Why LifeSync needs Notification Access",
            description = "LifeSync uses Notification permissions exclusively to display your scheduled task alerts, habit reminders, and interactive actions ('Mark Done' / 'Snooze').\n\n• No Ads or Marketing Notifications.\n• Used 100% locally on your device.",
            confirmText = "Allow Notifications",
            dismissText = "Not Now",
            onConfirm = {
                showNotifDisclosureDialog = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            onDismiss = {
                showNotifDisclosureDialog = false
                if (!hasExactAlarmPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    showAlarmDisclosureDialog = true
                } else if (isBatteryOptimized) {
                    showBatteryDisclosureDialog = true
                }
            }
        )
    }

    // 2. Prominent Disclosure: Exact Alarm Permission (Google Play Compliant)
    if (showAlarmDisclosureDialog) {
        ProminentDisclosureDialog(
            icon = Icons.Outlined.Alarm,
            iconTint = Color(0xFFF57C00),
            title = "Permission Notice: Exact Alarms",
            subtitle = "Ensure Timely Task & Habit Reminders",
            description = "LifeSync requires Exact Alarm permission so your task alarms and routine habit reminders ring at the exact minute you set, even when your screen is locked or device is idle.\n\n• Used solely for local alarm ringing.\n• No background tracking or data collection.",
            confirmText = "Enable in Settings",
            dismissText = "Skip for Now",
            onConfirm = {
                showAlarmDisclosureDialog = false
                PermissionUtils.openExactAlarmSettings(context)
            },
            onDismiss = {
                showAlarmDisclosureDialog = false
                if (isBatteryOptimized) showBatteryDisclosureDialog = true
            }
        )
    }

    // 3. Prominent Disclosure: Battery Unrestricted Access (Google Play Compliant)
    if (showBatteryDisclosureDialog) {
        ProminentDisclosureDialog(
            icon = Icons.Outlined.BatterySaver,
            iconTint = Color(0xFF00A878),
            title = "Permission Notice: Battery Saver",
            subtitle = "Prevent Missed Routine Reminders",
            description = "To prevent your phone's battery saver from stopping habit alarms when LifeSync is closed or swiped away, please allow unrestricted battery usage.\n\n• Guarantees 100% reliable alarms.\n• Ultra-lightweight and battery-friendly.",
            confirmText = "Allow Unrestricted",
            dismissText = "Later",
            onConfirm = {
                showBatteryDisclosureDialog = false
                PermissionUtils.requestIgnoreBatteryOptimizations(context)
            },
            onDismiss = {
                showBatteryDisclosureDialog = false
            }
        )
    }
}

@Composable
private fun ProminentDisclosureDialog(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    description: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = White,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }
        },
        text = {
            Text(
                text = description,
                fontSize = 13.sp,
                color = TextGray,
                lineHeight = 18.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = confirmText,
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = dismissText,
                    color = TextGray,
                    fontSize = 13.sp
                )
            }
        }
    )
}
