package com.dalgeer.lifeplanner.ui.screens.goals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dalgeer.lifeplanner.data.model.*
import com.dalgeer.lifeplanner.ui.theme.*


import com.dalgeer.lifeplanner.ui.common.*
import com.dalgeer.lifeplanner.ui.screens.*

// 7. Add New Goal Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGoalBottomSheet(
    onDismiss: () -> Unit,
    onSave: (GoalModel) -> Unit
) {
    var selectedGoalType by remember { mutableStateOf(GoalType.Personal) }
    var goalTitle by remember { mutableStateOf("") }
    var targetDate by remember { mutableStateOf("Sep 23, 2025") }
    var targetMetricValue by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showCustomDatePicker by remember { mutableStateOf(false) }


    if (showCustomDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showCustomDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                        targetDate = sdf.format(java.util.Date(millis))
                    }
                    showCustomDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Dynamic field labels and placeholders based on selected goal type
    val (metricLabel, metricPlaceholder, defaultTitle) = when (selectedGoalType) {
        GoalType.Financial -> Triple("Target Amount ($)", "$5,000", "Save for Vacation")
        GoalType.Career -> Triple("Target Goal", "Get Promoted / New Job", "Land a New Job")
        GoalType.Fitness -> Triple("Target Metric", "70 kg / 10,000 steps", "Morning Jog Habit")
        else -> Triple("Target Metric (Optional)", "100 days", "Achieve My Dream Goal")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 38.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF0EFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TrackChanges,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Add Goal",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Set a goal, build a better you.",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Goal Type",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Goal Type Cards (Personal Goal, Save Money, Learn Something, Health & Fitness)
            data class GoalTypeCard(
                val type: GoalType,
                val title: String,
                val subtitle: String,
                val icon: ImageVector,
                val bgColor: Color,
                val iconBgColor: Color,
                val iconColor: Color,
                val selectedBorderColor: Color
            )

            val isDark = MaterialTheme.colorScheme.background == ScreenBackgroundDark
            val goalTypes = listOf(
                GoalTypeCard(
                    GoalType.Personal,
                    "Personal",
                    "Achieve your dreams",
                    Icons.Outlined.TrackChanges,
                    if (isDark) Color(0xFF241B3B) else Color(0xFFF3F0FF),
                    if (isDark) Color(0xFF332A52) else Color(0xFFE2DDFF),
                    if (isDark) Color(0xFF8B84FF) else Color(0xFF6C63FF),
                    if (isDark) Color(0xFF8B84FF) else Color(0xFF6C63FF)
                ),
                GoalTypeCard(
                    GoalType.Career,
                    "Career",
                    "Grow professionally",
                    Icons.AutoMirrored.Outlined.MenuBook,
                    if (isDark) Color(0xFF1A263B) else Color(0xFFEFF6FF),
                    if (isDark) Color(0xFF233554) else Color(0xFFDBEAFE),
                    if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB),
                    if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB)
                ),
                GoalTypeCard(
                    GoalType.Fitness,
                    "Fitness",
                    "Be a healthier you",
                    Icons.Outlined.FavoriteBorder,
                    if (isDark) Color(0xFF2E1F1A) else Color(0xFFFFF3EA),
                    if (isDark) Color(0xFF422C23) else Color(0xFFFFE2CE),
                    if (isDark) Color(0xFFFFB300) else Color(0xFFFF6B00),
                    if (isDark) Color(0xFFFFB300) else Color(0xFFFF6B00)
                ),
                GoalTypeCard(
                    GoalType.Financial,
                    "Financial",
                    "Build your savings",
                    Icons.Outlined.AccountBalanceWallet,
                    if (isDark) Color(0xFF162B28) else Color(0xFFEAF7F1),
                    if (isDark) Color(0xFF1F3D37) else Color(0xFFCBECE0),
                    if (isDark) Color(0xFF00E676) else Color(0xFF00A878),
                    if (isDark) Color(0xFF00E676) else Color(0xFF00A878)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                goalTypes.forEach { type ->
                    val isSelected = selectedGoalType == type.type
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp)
                            .clickable {
                                selectedGoalType = type.type
                                goalTitle = ""
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = type.bgColor,
                        border = if (isSelected) BorderStroke(1.5.dp, type.selectedBorderColor) else null
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(18.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(type.selectedBorderColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(type.iconBgColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = type.icon,
                                        contentDescription = null,
                                        tint = type.iconColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = type.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = type.subtitle,
                                    fontSize = 9.sp,
                                    color = if (isSelected) type.iconColor else TextGray,
                                    lineHeight = 11.sp,
                                    maxLines = 2,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 1: Goal Title
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = InputFieldBg,
                border = BorderStroke(1.dp, InputFieldBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(IconBoxBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TrackChanges,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Goal Title",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextGray
                        )
                        BasicTextField(
                            value = goalTitle,
                            onValueChange = { goalTitle = it },
                            cursorBrush = SolidColor(TextDark),
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            ),
                            decorationBox = { innerTextField ->
                                if (goalTitle.isEmpty()) {
                                    Text(
                                        text = "e.g. $defaultTitle",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = TextGray
                                    )
                                }
                                innerTextField()
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Field 2 & 3: Target Date & Dynamic Target Amount/Metric
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Target Date Field
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCustomDatePicker = true },
                        shape = RoundedCornerShape(16.dp),
                        color = InputFieldBg,
                        border = BorderStroke(1.dp, InputFieldBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(IconBoxBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = null,
                                    tint = PrimaryPurple,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Target Date",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextGray
                                )
                                Text(
                                    text = targetDate,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                            }
                        }
                    }
                }

                // Dynamic Metric/Amount Field
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = InputFieldBg,
                    border = BorderStroke(1.dp, InputFieldBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(IconBoxBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (selectedGoalType == GoalType.Financial) Icons.Outlined.AttachMoney else Icons.Outlined.EmojiEvents,
                                contentDescription = null,
                                tint = PrimaryPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = metricLabel,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextGray
                            )
                            BasicTextField(
                                value = targetMetricValue,
                                onValueChange = { targetMetricValue = it },
                                cursorBrush = SolidColor(TextDark),
                                textStyle = TextStyle(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                ),
                                decorationBox = { innerTextField ->
                                    if (targetMetricValue.isEmpty()) {
                                        Text(
                                            text = metricPlaceholder,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = TextGray
                                        )
                                    }
                                    innerTextField()
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Field 4: Notes (Optional)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = InputFieldBg,
                border = BorderStroke(1.dp, InputFieldBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(IconBoxBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Notes (Optional)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextGray
                        )
                        BasicTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            cursorBrush = SolidColor(TextDark),
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = TextDark
                            ),
                            decorationBox = { innerTextField ->
                                if (notes.isEmpty()) {
                                    Text(
                                        text = "Add a short note about your goal...",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = TextGray
                                    )
                                }
                                innerTextField()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = ErrorRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Action Button: Create Goal
            Button(
                onClick = {
                    when {
                        goalTitle.isBlank() -> {
                            errorMessage = "Goal title is compulsory!"
                        }
                        targetMetricValue.isBlank() -> {
                            errorMessage = "Target amount / metric is compulsory!"
                        }
                        else -> {
                            errorMessage = null
                            val newGoal = GoalModel(
                                title = goalTitle.trim(),
                                type = selectedGoalType,
                                targetDate = targetDate,
                                targetMetricValue = targetMetricValue.trim(),
                                currentMetricValue = "0",
                                notes = notes,
                                isCompleted = false
                            )
                            onSave(newGoal)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create Goal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}