package com.dalgeer.lifeplanner.ui.screens.profile

import android.content.Intent
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
import com.dalgeer.lifeplanner.presentation.uiState.UiState
import com.dalgeer.lifeplanner.presentation.viewmodel.GoalViewModel
import com.dalgeer.lifeplanner.presentation.viewmodel.RoutineViewModel
import com.dalgeer.lifeplanner.presentation.viewmodel.TaskViewModel
import com.dalgeer.lifeplanner.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    taskViewModel: TaskViewModel = koinViewModel(),
    routineViewModel: RoutineViewModel = koinViewModel(),
    goalViewModel: GoalViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var isNotificationsEnabled by remember { mutableStateOf(true) }

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
                text = "Profile & Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Avatar Card
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
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD0BCFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "A",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF381E72)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Alex",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(2.dp))

                Surface(
                    color = Color(0xFFF0EFFF),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🌟 LifePlanner Pro Member",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPurple,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // User Performance Quick Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStatItem(title = "Completed", value = "$completedTasksCount", icon = Icons.AutoMirrored.Outlined.Assignment, color = PrimaryPurple)
                    ProfileStatItem(title = "Max Streak", value = "$maxStreak Days", icon = Icons.Outlined.LocalFireDepartment, color = Color(0xFFF57C00))
                    ProfileStatItem(title = "Active Goals", value = "$activeGoalsCount", icon = Icons.Outlined.EmojiEvents, color = Color(0xFF00A878))
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

        SettingsCardItem(
            title = "Task & Habit Reminders",
            subtitle = "Receive alerts for scheduled tasks & routines",
            icon = Icons.Outlined.Notifications,
            iconTint = PrimaryPurple,
            trailing = {
                Switch(
                    checked = isNotificationsEnabled,
                    onCheckedChange = { isNotificationsEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = White,
                        checkedTrackColor = PrimaryPurple
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsCardItem(
            title = "System Notification Settings",
            subtitle = "Configure system alerts, sound and vibration",
            icon = Icons.Outlined.Tune,
            iconTint = Color(0xFF2563EB),
            onClick = {
                val intent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                } else {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.fromParts("package", context.packageName, null)
                    }
                }
                context.startActivity(intent)
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

        SettingsCardItem(
            title = "App Language",
            subtitle = "Change display language",
            icon = Icons.Outlined.Language,
            iconTint = Color(0xFF00A878),
            onClick = onNavigateToLanguage
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsCardItem(
            title = "Theme / Appearance",
            subtitle = "System default (Light Mode)",
            icon = Icons.Outlined.Palette,
            iconTint = Color(0xFFFF6B00)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section: About App
        Text(
            text = "About LifePlanner",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsCardItem(
            title = "Version & Build",
            subtitle = "v1.0.0 (Production Release)",
            icon = Icons.Outlined.Info,
            iconTint = TextGray
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ProfileStatItem(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text(text = title, fontSize = 11.sp, color = TextGray)
    }
}

@Composable
fun SettingsCardItem(
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
