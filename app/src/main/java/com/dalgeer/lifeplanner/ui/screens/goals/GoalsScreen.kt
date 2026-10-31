package com.dalgeer.lifeplanner.ui.screens.goals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalgeer.lifeplanner.data.model.GoalModel
import com.dalgeer.lifeplanner.data.model.calculateProgressRatio
import com.dalgeer.lifeplanner.presentation.uiState.UiState
import com.dalgeer.lifeplanner.presentation.viewmodel.GoalViewModel
import com.dalgeer.lifeplanner.ui.components.AppFloatingActionButton
import com.dalgeer.lifeplanner.ui.screens.goals.NewGoalBottomSheet
import com.dalgeer.lifeplanner.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun GoalsScreen(
    viewModel: GoalViewModel = koinViewModel()
) {
    var showNewGoalSheet by remember { mutableStateOf(false) }
    var selectedGoalForProgress by remember { mutableStateOf<GoalModel?>(null) }
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
                        text = "Goals",
                        style = Typography.headlineLarge,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Turn your dreams into achievements.",
                        style = Typography.bodyMedium,
                        color = TextGray
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryPurple)
                        .clickable { showNewGoalSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Goal", tint = Color.White, modifier = Modifier.size(22.dp))
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

                // Summary Card
                val activeCount = if (uiState is UiState.Success) (uiState as UiState.Success<List<GoalModel>>).data.count { !it.isCompleted } else 0
                val completedCount = if (uiState is UiState.Success) (uiState as UiState.Success<List<GoalModel>>).data.count { it.isCompleted } else 0
                val totalGoals = activeCount + completedCount
                val successRate = if (totalGoals > 0) ((completedCount.toFloat() / totalGoals) * 100).toInt() else 0

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = PrimaryPurple.copy(alpha = 0.2f),
                            spotColor = PrimaryPurple.copy(alpha = 0.3f)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF6C63FF), Color(0xFF4338CA), Color(0xFF3730A3))
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.EmojiEvents,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "GOAL OVERVIEW",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(
                                        text = "$activeCount Active Goals",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = Color.White.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "$completedCount Completed",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "• $successRate% Done",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }

                            // Trophy / Target Background Icon
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.TrackChanges,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Your Goals Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Goals",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Surface(
                        color = IconBoxBg,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(PrimaryPurple, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$activeCount Active",
                                color = PrimaryPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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
                        val goals = state.data
                        if (goals.isEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = CardSurface
                            ) {
                                Box(
                                    modifier = Modifier.padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No goals added yet. Tap + to create one!",
                                        style = Typography.bodyMedium,
                                        color = TextGray
                                    )
                                }
                            }
                        } else {
                            goals.forEach { goal ->
                                GoalCardDynamicItem(
                                    goal = goal,
                                    onDelete = { viewModel.deleteGoal(goal.id) },
                                    onUpdateProgress = { selectedGoalForProgress = goal }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Contextual FAB
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 32.dp)
        ) {
            AppFloatingActionButton(
                onClick = { showNewGoalSheet = true },
                isRoundedSquare = false
            )
        }

        if (showNewGoalSheet) {
            NewGoalBottomSheet(
                onDismiss = { showNewGoalSheet = false },
                onSave = { goal ->
                    viewModel.addGoal(goal)
                    showNewGoalSheet = false
                }
            )
        }

        selectedGoalForProgress?.let { goal ->
            UpdateProgressBottomSheet(
                goal = goal,
                onDismiss = { selectedGoalForProgress = null },
                onSave = { newValue ->
                    viewModel.updateGoalProgress(goal.id, newValue)
                    selectedGoalForProgress = null
                }
            )
        }
    }
}

@Composable
fun GoalCardDynamicItem(
    goal: GoalModel,
    onDelete: () -> Unit,
    onUpdateProgress: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val (icon, iconColor, iconBgColor) = when (goal.type) {
        com.dalgeer.lifeplanner.data.model.GoalType.Personal -> Triple(Icons.Outlined.TrackChanges, PrimaryPurple, if (isDarkTheme) PrimaryContainerDark else Color(0xFFE2DDFF))
        com.dalgeer.lifeplanner.data.model.GoalType.Career -> Triple(Icons.AutoMirrored.Outlined.MenuBook, Color(0xFF42A5F5), if (isDarkTheme) SurfaceVariantDark else Color(0xFFDBEAFE))
        com.dalgeer.lifeplanner.data.model.GoalType.Fitness -> Triple(Icons.Outlined.FavoriteBorder, Color(0xFFFFB74D), if (isDarkTheme) Color(0xFF3D2A12) else Color(0xFFFFE2CE))
        com.dalgeer.lifeplanner.data.model.GoalType.Financial -> Triple(Icons.Outlined.AccountBalanceWallet, SuccessGreen, if (isDarkTheme) LightGreenDark else Color(0xFFCBECE0))
    }

    val displayProgress = goal.calculateProgressRatio()
    val progressPercentage = (displayProgress * 100).toInt()

    val cardAccentColor = if (goal.isCompleted) SuccessGreen else iconColor

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = cardAccentColor.copy(alpha = 0.1f),
                spotColor = cardAccentColor.copy(alpha = 0.15f)
            )
            .border(
                width = 1.dp,
                color = DividerColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onUpdateProgress() },
        shape = RoundedCornerShape(16.dp),
        color = CardSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            // Left Accent Bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(cardAccentColor, cardAccentColor.copy(alpha = 0.6f))
                        ),
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Icon Box
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (goal.isCompleted) (if (isDarkTheme) LightGreenDark else Color(0xFFE8F5E9)) else iconBgColor)
                            .border(1.dp, if (goal.isCompleted) SuccessGreen.copy(alpha = 0.3f) else iconColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (goal.isCompleted) Icons.Default.CheckCircle else icon,
                            contentDescription = null,
                            tint = if (goal.isCompleted) SuccessGreen else iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            // Category Tag
                            Surface(
                                color = iconBgColor.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = goal.type.name,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = iconColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }

                            // Status Tag
                            Surface(
                                color = if (goal.isCompleted) LightGreen else IconBoxBg,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (goal.isCompleted) "Completed" else "In Progress",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (goal.isCompleted) SuccessGreen else PrimaryPurple,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = goal.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }

                    // 3-dot More Menu
                    Box {
                        IconButton(
                            onClick = { expanded = true },
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(SurfaceVariantColor)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = TextGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(CardSurface)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Update Progress", color = TextDark, fontWeight = FontWeight.Medium, fontSize = 13.sp) },
                                onClick = {
                                    expanded = false
                                    onUpdateProgress()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.AutoMirrored.Outlined.TrendingUp,
                                        contentDescription = null,
                                        tint = PrimaryPurple,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Goal", color = ErrorRed, fontWeight = FontWeight.Medium, fontSize = 13.sp) },
                                onClick = {
                                    expanded = false
                                    onDelete()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Delete,
                                        contentDescription = null,
                                        tint = ErrorRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { displayProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = cardAccentColor,
                    trackColor = if (isDarkTheme) DividerColorDark else Color(0xFFF0F2F8)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${goal.currentMetricValue} / ${goal.targetMetricValue}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "$progressPercentage%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = cardAccentColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProgressBottomSheet(
    goal: GoalModel,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var currentValue by remember { mutableStateOf(goal.currentMetricValue) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = CardSurface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 38.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(DividerColor)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Update Goal Progress",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDark)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Current Progress Value", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = InputFieldBg,
                border = BorderStroke(1.dp, InputFieldBorder)
            ) {
                BasicTextField(
                    value = currentValue,
                    onValueChange = { currentValue = it },
                    cursorBrush = SolidColor(TextDark),
                    textStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark),
                    decorationBox = { inner ->
                        Box(modifier = Modifier.padding(14.dp)) {
                            if (currentValue.isEmpty()) {
                                Text("Enter value (e.g. 50)", fontSize = 15.sp, color = TextGray)
                            }
                            inner()
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onSave(currentValue.trim())
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Update Progress", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
