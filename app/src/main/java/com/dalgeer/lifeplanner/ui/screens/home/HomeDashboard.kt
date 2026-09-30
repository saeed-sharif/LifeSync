package com.dalgeer.lifeplanner.ui.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalgeer.lifeplanner.data.model.*
import com.dalgeer.lifeplanner.data.preferences.AppPreferences
import com.dalgeer.lifeplanner.presentation.uiState.UiState
import com.dalgeer.lifeplanner.presentation.viewmodel.*
import com.dalgeer.lifeplanner.ui.screens.profile.AVATAR_PRESETS
import com.dalgeer.lifeplanner.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeDashboard(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToTask: (String) -> Unit = {},
    onNavigateToCompletedTasks: () -> Unit = {},
    taskViewModel: TaskViewModel = koinViewModel(),
    routineViewModel: RoutineViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val userName = AppPreferences.getUserName(context)
    val avatarIndex = AppPreferences.getUserAvatarIndex(context)
    val avatarStyle = AVATAR_PRESETS.getOrElse(avatarIndex) { AVATAR_PRESETS[0] }
    val initial = if (userName.isNotBlank()) userName.first().uppercase() else "A"

    val taskState by taskViewModel.uiState.collectAsStateWithLifecycle()
    val routineState by routineViewModel.uiState.collectAsStateWithLifecycle()

    val tasks = (taskState as? UiState.Success)?.data ?: emptyList()
    val routines = (routineState as? UiState.Success)?.data ?: emptyList()

    val currentDateStr = remember {
        java.text.SimpleDateFormat("EEEE, MMMM d", java.util.Locale.getDefault()).format(java.util.Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = PaddingLarge)
    ) {
        // FIXED Top Header (Does NOT scroll)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good morning,",
                    fontSize = 14.sp,
                    color = TextGray
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userName,
                        style = Typography.headlineLarge,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "👋", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = currentDateStr,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextGray
                )
            }

            // Right Header Profile Avatar
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(avatarStyle.bgColor)
                    .clickable { onNavigateToProfile() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = avatarStyle.textColor
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Top Progress Banner Card (Dynamic progress based on tasks)
            TopProgressCard(
                tasks = tasks,
                onCardClick = {
                    val firstOverdue = tasks.find { it.isOverdue() }
                    if (firstOverdue != null) onNavigateToTask(firstOverdue.id)
                },
                onCompletedClick = {
                    onNavigateToCompletedTasks()
                }
            )

            // Overdue Tasks Alert Card (if any overdue tasks exist)
            val overdueTasks = tasks.filter { it.isOverdue() }
            if (overdueTasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                OverdueTaskAlertCard(
                    overdueTasks = overdueTasks,
                    onViewClick = { onNavigateToTask(overdueTasks.first().id) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Up Next Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Up Next",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Up Next Tasks List
            UpNextTaskCardList(
                tasks = tasks,
                onToggleTask = { taskId -> taskViewModel.toggleTaskStatus(taskId) },
                onTaskClick = { taskId -> onNavigateToTask(taskId) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Today's Routines Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
            Text(
                text = "Today's Routines",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Today's Routines Horizontal Cards
        if (routines.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White
            ) {
                Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                    Text("No routines added yet.", fontSize = 13.sp, color = TextGray)
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(routines) { routine ->
                    HomeRoutineCard(routine)
                }
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
        }
    }
}
