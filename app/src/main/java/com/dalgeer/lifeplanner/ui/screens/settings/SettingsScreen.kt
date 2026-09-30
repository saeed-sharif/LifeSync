package com.dalgeer.lifeplanner.ui.screens.settings

import android.app.Activity
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dalgeer.lifeplanner.data.preferences.AppPreferences
import com.dalgeer.lifeplanner.presentation.uiState.UiState
import com.dalgeer.lifeplanner.presentation.viewmodel.GoalViewModel
import com.dalgeer.lifeplanner.presentation.viewmodel.RoutineViewModel
import com.dalgeer.lifeplanner.presentation.viewmodel.TaskViewModel
import com.dalgeer.lifeplanner.util.InAppReviewManager
import com.dalgeer.lifeplanner.util.InAppUpdateManager
import com.dalgeer.lifeplanner.ui.screens.profile.AVATAR_PRESETS
import com.dalgeer.lifeplanner.ui.theme.*

import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit = {},
    taskViewModel: TaskViewModel = koinViewModel(),
    routineViewModel: RoutineViewModel = koinViewModel(),
    goalViewModel: GoalViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var isNotificationsEnabled by remember { mutableStateOf(AppPreferences.isNotificationsEnabled(context)) }
    var currentThemeMode by remember { mutableStateOf(AppPreferences.getThemeMode(context)) }
    var showThemeDialog by remember { mutableStateOf(false) }

    val userName = AppPreferences.getUserName(context)
    val avatarIndex = AppPreferences.getUserAvatarIndex(context)
    val avatarStyle = AVATAR_PRESETS.getOrElse(avatarIndex) { AVATAR_PRESETS[0] }
    val initial = if (userName.isNotBlank()) userName.first().uppercase() else "A"

    val taskState by taskViewModel.uiState.collectAsStateWithLifecycle()
    val routineState by routineViewModel.uiState.collectAsStateWithLifecycle()
    val goalState by goalViewModel.uiState.collectAsStateWithLifecycle()

    val completedTasksCount = (taskState as? UiState.Success)?.data?.count { it.status == com.dalgeer.lifeplanner.data.model.TaskStatus.Completed } ?: 0
    val routines = (routineState as? UiState.Success)?.data ?: emptyList()
    val maxStreak = routines.maxOfOrNull { it.streak } ?: 0
    val activeGoalsCount = (goalState as? UiState.Success)?.data?.count { !it.isCompleted } ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = PaddingLarge, vertical = PaddingMedium)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(White)
                    .border(1.dp, DividerColor, CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextDark,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Avatar & Edit Profile Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = PrimaryPurple.copy(alpha = 0.15f),
                    spotColor = PrimaryPurple.copy(alpha = 0.2f)
                ),
            shape = RoundedCornerShape(24.dp),
            color = White
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(avatarStyle.bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = avatarStyle.textColor
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    color = Color(0xFFF0EFFF),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable { onNavigateToEditProfile() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = PrimaryPurple,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit Profile",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SettingsStatItem(title = "Completed", value = "$completedTasksCount", icon = Icons.AutoMirrored.Outlined.Assignment, color = PrimaryPurple)
                    SettingsStatItem(title = "Max Streak", value = "$maxStreak Days", icon = Icons.Outlined.LocalFireDepartment, color = Color(0xFFF57C00))
                    SettingsStatItem(title = "Active Goals", value = "$activeGoalsCount", icon = Icons.Outlined.EmojiEvents, color = Color(0xFF00A878))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Reminders & Notifications
        Text(
            text = "Notifications & Reminders",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsCardRow(
            title = "Task & Habit Reminders",
            subtitle = if (com.dalgeer.lifeplanner.util.PermissionUtils.hasNotificationPermission(context)) {
                "Receive alerts for scheduled tasks & routines"
            } else {
                "Notification permission required ⚠️"
            },
            icon = Icons.Outlined.Notifications,
            iconTint = PrimaryPurple,
            onClick = {
                if (!com.dalgeer.lifeplanner.util.PermissionUtils.hasNotificationPermission(context)) {
                    com.dalgeer.lifeplanner.util.PermissionUtils.openNotificationSettings(context)
                } else if (!com.dalgeer.lifeplanner.util.PermissionUtils.canScheduleExactAlarms(context)) {
                    com.dalgeer.lifeplanner.util.PermissionUtils.openExactAlarmSettings(context)
                }
            },
            trailing = {
                Switch(
                    checked = isNotificationsEnabled && com.dalgeer.lifeplanner.util.PermissionUtils.hasNotificationPermission(context),
                    onCheckedChange = { enabled ->
                        if (enabled && !com.dalgeer.lifeplanner.util.PermissionUtils.hasNotificationPermission(context)) {
                            com.dalgeer.lifeplanner.util.PermissionUtils.openNotificationSettings(context)
                        } else {
                            isNotificationsEnabled = enabled
                            AppPreferences.setNotificationsEnabled(context, enabled)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = White,
                        checkedTrackColor = PrimaryPurple
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsCardRow(
            title = "System Notification Settings",
            subtitle = "Configure system alerts, sound and vibration",
            icon = Icons.Outlined.Tune,
            iconTint = Color(0xFF2563EB),
            onClick = {
                com.dalgeer.lifeplanner.util.PermissionUtils.openNotificationSettings(context)
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsCardRow(
            title = "Unrestricted Battery Mode",
            subtitle = if (com.dalgeer.lifeplanner.util.PermissionUtils.isIgnoringBatteryOptimizations(context)) {
                "Battery restriction disabled (Reliable Alarms ⚡)"
            } else {
                "Allow background alarms without battery killing ⚠️"
            },
            icon = Icons.Outlined.BatterySaver,
            iconTint = Color(0xFF059669),
            onClick = {
                com.dalgeer.lifeplanner.util.PermissionUtils.requestIgnoreBatteryOptimizations(context)
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsCardRow(
            title = "Background Autostart Settings",
            subtitle = "Enable for Xiaomi, Vivo, Oppo, Realme, Samsung devices",
            icon = Icons.Outlined.PhonelinkSetup,
            iconTint = Color(0xFFD97706),
            onClick = {
                com.dalgeer.lifeplanner.util.PermissionUtils.openAutoStartSettings(context)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Preferences & Language
        Text(
            text = "Preferences",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsCardRow(
            title = "App Language",
            subtitle = AppPreferences.getSelectedLanguage(context),
            icon = Icons.Outlined.Language,
            iconTint = Color(0xFF00A878),
            onClick = onNavigateToLanguage
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsCardRow(
            title = "App Theme & Appearance",
            subtitle = when (currentThemeMode) {
                "Light" -> "Light Mode ☀️"
                "Dark" -> "Dark Mode 🌙"
                else -> "System Default 📱"
            },
            icon = Icons.Outlined.Palette,
            iconTint = Color(0xFFFF6B00),
            onClick = { showThemeDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section: About App
        Text(
            text = "About LifeSync",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsCardRow(
            title = "Rate & Review LifeSync",
            subtitle = "Love LifeSync? Rate us on Google Play Store ⭐",
            icon = Icons.Outlined.StarRate,
            iconTint = Color(0xFFFFB800),
            onClick = {
                val activity = context as? Activity
                if (activity != null) {
                    InAppReviewManager.launchInAppReview(activity)
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsCardRow(
            title = "Check for App Updates",
            subtitle = "Ensure you have the latest features & stability",
            icon = Icons.Outlined.SystemUpdate,
            iconTint = Color(0xFF00A878),
            onClick = {
                val activity = context as? Activity
                if (activity != null) {
                    val updateManager = InAppUpdateManager(context)
                    updateManager.checkForUpdates(activity, isUserInitiated = true)
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsCardRow(
            title = "Privacy Policy",
            subtitle = "Learn how LifeSync protects your data",
            icon = Icons.Outlined.Security,
            iconTint = Color(0xFF2563EB),
            onClick = onNavigateToPrivacyPolicy
        )

        Spacer(modifier = Modifier.height(10.dp))

        val lastRestartTime = AppPreferences.getLastRestartTime(context)
        SettingsCardRow(
            title = "Last Device Boot / Restart",
            subtitle = lastRestartTime ?: "No boot event recorded yet",
            icon = Icons.Outlined.Refresh,
            iconTint = PrimaryPurple
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsCardRow(
            title = "Version & Build",
            subtitle = "v1.0.0 (Production Release)",
            icon = Icons.Outlined.Info,
            iconTint = TextGray
        )

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Select App Theme", fontWeight = FontWeight.Bold, color = TextDark) },
            text = {
                Column {
                    listOf("System" to "System Default 📱", "Light" to "Light Mode ☀️", "Dark" to "Dark Mode 🌙").forEach { (mode, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentThemeMode = mode
                                    AppPreferences.setThemeMode(context, mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentThemeMode == mode,
                                onClick = {
                                    currentThemeMode = mode
                                    AppPreferences.setThemeMode(context, mode)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextDark)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel", color = PrimaryPurple)
                }
            },
            containerColor = White
        )
    }
}

@Composable
fun SettingsStatItem(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text(text = title, fontSize = 11.sp, color = TextGray)
    }
}

@Composable
fun SettingsCardRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        color = White,
        border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 12.sp, color = TextGray)
            }
            trailing?.invoke()
        }
    }
}
