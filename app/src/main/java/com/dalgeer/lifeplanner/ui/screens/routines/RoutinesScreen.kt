package com.dalgeer.lifeplanner.ui.screens.routines

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalgeer.lifeplanner.data.model.RoutineModel
import com.dalgeer.lifeplanner.data.model.getRoutineColor
import com.dalgeer.lifeplanner.data.model.getRoutineIcon
import com.dalgeer.lifeplanner.data.model.getRoutineTint
import com.dalgeer.lifeplanner.presentation.uiState.UiState
import com.dalgeer.lifeplanner.presentation.viewmodel.RoutineViewModel
import com.dalgeer.lifeplanner.ui.components.AppFloatingActionButton
import com.dalgeer.lifeplanner.ui.screens.routines.NewRoutineBottomSheet
import com.dalgeer.lifeplanner.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun RoutinesScreen(
    viewModel: RoutineViewModel = koinViewModel()
) {
    var showNewRoutineSheet by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .padding(horizontal = PaddingLarge)
        ) {
            // FIXED Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Routines",
                        style = Typography.headlineLarge,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Build habits. Stay consistent.",
                        style = Typography.bodyMedium,
                        color = TextGray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val currentRoutines = (uiState as? UiState.Success)?.data ?: emptyList()
                    val maxStreak = currentRoutines.maxOfOrNull { it.streak } ?: 0

                    Surface(
                        color = if (isDarkTheme) Color(0xFF3D2612) else Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🔥 $maxStreak day streak", style = Typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (isDarkTheme) Color(0xFFFFB74D) else Color(0xFFF57C00))
                        }
                    }
                }
            }

            // SCROLLABLE Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                when (val state = uiState) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryPurple)
                        }
                    }
                    is UiState.Error -> {
                        Text(text = state.message, color = ErrorRed)
                    }
                    is UiState.Success -> {
                        val routines = state.data
                        val maxStreak = routines.maxOfOrNull { it.streak } ?: 0

                        // Streak Banner Card
                        StreakSummaryBannerCard(streak = maxStreak)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Today's Routines Section Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Today's Routines",
                                style = Typography.titleLarge,
                                color = TextDark
                            )
                            Text(
                                text = "+ Add Habit",
                                style = Typography.titleMedium,
                                color = PrimaryPurple,
                                modifier = Modifier.clickable { showNewRoutineSheet = true }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (routines.isEmpty()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(CardElevation, RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                color = CardSurface
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text("No routines added yet. Tap + Add Habit.", style = Typography.bodyMedium, color = TextGray)
                                }
                            }
                        } else {
                            val timeOfDayCategories = listOf(
                                com.dalgeer.lifeplanner.data.model.RoutineTimeOfDay.Morning to "Morning Routines",
                                com.dalgeer.lifeplanner.data.model.RoutineTimeOfDay.Evening to "Evening Routines",
                                com.dalgeer.lifeplanner.data.model.RoutineTimeOfDay.Night to "Night Routines",
                                com.dalgeer.lifeplanner.data.model.RoutineTimeOfDay.Custom to "Custom Routines"
                            )

                            timeOfDayCategories.forEach { (timeOfDay, title) ->
                                val categoryRoutines = routines.filter { it.timeOfDay == timeOfDay }
                                if (categoryRoutines.isNotEmpty()) {
                                    val comp = categoryRoutines.count { it.isCompletedToday }
                                    val tot = categoryRoutines.size
                                    val prog = if (tot > 0) comp.toFloat() / tot else 0f

                                    RichRoutineGroupCard(
                                        timeOfDay = timeOfDay,
                                        title = title,
                                        completedCount = comp,
                                        totalCount = tot,
                                        progress = prog,
                                        routines = categoryRoutines,
                                        onToggle = { id -> viewModel.toggleRoutineCompletion(id) },
                                        onDelete = { id -> viewModel.deleteRoutine(id) }
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tip Card
                RoutineTipCard()

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // FAB
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 32.dp)
        ) {
            AppFloatingActionButton(
                onClick = { showNewRoutineSheet = true },
                isRoundedSquare = true
            )
        }

        if (showNewRoutineSheet) {
            NewRoutineBottomSheet(
                onDismiss = { showNewRoutineSheet = false },
                onSave = { routine ->
                    viewModel.addRoutine(routine)
                    showNewRoutineSheet = false
                }
            )
        }
    }
}

@Composable
fun StreakSummaryBannerCard(streak: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .shadow(CardElevation, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(listOf(Color(0xFF6C63FF), Color(0xFF8E84FF))))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.LocalFireDepartment, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("$streak Day Streak", style = Typography.headlineSmall, color = Color.White)
                    Text(if (streak > 0) "You're on fire! 🔥" else "Start your streak today!", style = Typography.bodyLarge, color = Color.White.copy(alpha = 0.95f))
                    Text("Keep up the great habits!", style = Typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
                }
            }
        }
    }
}

@Composable
fun RichRoutineGroupCard(
    timeOfDay: com.dalgeer.lifeplanner.data.model.RoutineTimeOfDay,
    title: String,
    completedCount: Int,
    totalCount: Int,
    progress: Float,
    routines: List<RoutineModel>,
    onToggle: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    val iconBg = timeOfDay.getRoutineColor()
    val iconTint = timeOfDay.getRoutineTint()
    val iconVector = timeOfDay.getRoutineIcon()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(CardElevation, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = CardSurface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(iconVector, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = Typography.titleLarge, color = TextDark)
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = iconTint, fontWeight = FontWeight.Bold)) {
                                append("$completedCount / $totalCount ")
                            }
                            append("completed today")
                        },
                        style = Typography.bodyMedium,
                        color = TextGray
                    )
                }
                Surface(color = iconBg, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = if (completedCount == totalCount && totalCount > 0) "Completed" else "In Progress",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = iconTint,
                        style = Typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = iconTint,
                trackColor = if (isDarkTheme) DividerColorDark else Color(0xFFF0F2F8)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (routines.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No routines added yet. Tap + Add Habit.", style = Typography.bodyMedium, color = TextGray)
                }
            } else {
                routines.forEach { routine ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggle(routine.id) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (routine.isCompletedToday) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                            contentDescription = null,
                            tint = if (routine.isCompletedToday) SuccessGreen else TextGray,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(routine.timeOfDay.getRoutineColor(), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = routine.timeOfDay.getRoutineIcon(),
                                contentDescription = null,
                                tint = routine.timeOfDay.getRoutineTint(),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = routine.name,
                                style = Typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = routine.time,
                                    style = Typography.bodyMedium,
                                    color = TextGray
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🔥 ${routine.streak}d streak",
                                    style = Typography.bodyMedium,
                                    color = iconTint,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        IconButton(onClick = { onDelete(routine.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = TextGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoutineTipCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(CardElevation, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = if (isDarkTheme) PrimaryContainerDark else Color(0xFFF0EFFF)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(PrimaryPurple),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Habit Stacking Tip 💡",
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Attach a new routine to an existing habit to build consistency faster!",
                    style = Typography.bodyMedium,
                    color = TextGray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
