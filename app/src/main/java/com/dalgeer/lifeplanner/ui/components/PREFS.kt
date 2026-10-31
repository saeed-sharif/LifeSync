package com.dalgeer.lifeplanner.ui.components

import androidx.compose.runtime.Composable
import com.dalgeer.lifeplanner.ui.common.NotificationAndAlarmPermissionHandler

/**
 * Gate that invokes NotificationAndAlarmPermissionHandler to handle
 * prominent disclosures for runtime/special permissions (Notifications & Exact Alarms).
 */
@Composable
fun ReminderPermissionGate(resumeTick: Int) {
    NotificationAndAlarmPermissionHandler(resumeTick = resumeTick)
}