package com.dalgeer.lifeplanner.ui.common

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.NotificationsActive
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
import com.dalgeer.lifeplanner.ui.theme.CardSurface
import com.dalgeer.lifeplanner.ui.theme.PrimaryPurple
import com.dalgeer.lifeplanner.ui.theme.TextDark
import com.dalgeer.lifeplanner.ui.theme.TextGray
import com.dalgeer.lifeplanner.util.AlarmScheduler
import com.dalgeer.lifeplanner.util.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun NotificationAndAlarmPermissionHandler(
    resumeTick: Int = 0,
    showDialogIfMissing: Boolean = true,
    onPermissionResult: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var refreshKey by remember { mutableIntStateOf(0) }

    var hasNotifPermission by remember(resumeTick, refreshKey) {
        mutableStateOf(PermissionUtils.hasNotificationPermission(context))
    }
    var hasExactAlarmPermission by remember(resumeTick, refreshKey) {
        mutableStateOf(PermissionUtils.canScheduleExactAlarms(context))
    }

    var showNotifDisclosureDialog by remember { mutableStateOf(false) }
    var showAlarmDisclosureDialog by remember { mutableStateOf(false) }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotifPermission = isGranted
        onPermissionResult(isGranted)
        refreshKey++
        if (!hasExactAlarmPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            showAlarmDisclosureDialog = true
        }
    }

    // Re-schedule pending alarms whenever permissions are valid or granted
    LaunchedEffect(hasNotifPermission, hasExactAlarmPermission) {
        if (hasNotifPermission || hasExactAlarmPermission) {
            try {
                withContext(Dispatchers.IO) {
                    AlarmScheduler.rescheduleAll(context)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(resumeTick, showDialogIfMissing) {
        hasNotifPermission = PermissionUtils.hasNotificationPermission(context)
        hasExactAlarmPermission = PermissionUtils.canScheduleExactAlarms(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotifPermission && showDialogIfMissing) {
            showNotifDisclosureDialog = true
            showAlarmDisclosureDialog = false
        } else if (!hasExactAlarmPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && showDialogIfMissing) {
            showAlarmDisclosureDialog = true
            showNotifDisclosureDialog = false
        } else {
            showNotifDisclosureDialog = false
            showAlarmDisclosureDialog = false
        }
    }

    // 1. Prominent Disclosure: Notification Permission
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
                }
            }
        )
    }

    // 2. Prominent Disclosure: Exact Alarm Permission
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
        containerColor = CardSurface,
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
                    color = Color.White,
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
