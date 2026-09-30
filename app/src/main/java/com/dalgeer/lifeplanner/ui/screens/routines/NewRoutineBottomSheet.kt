package com.dalgeer.lifeplanner.ui.screens.routines

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

// 6. Add New Routine Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRoutineBottomSheet(
    onDismiss: () -> Unit,
    onSave: (RoutineModel) -> Unit
) {
    val currentTimeFormatted = remember {
        java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
    }
    var selectedPreset by remember { mutableStateOf(RoutineTimeOfDay.Morning) }
    var routineName by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Daily") }
    var selectedCustomDays by remember { mutableStateOf(setOf("Mon", "Wed", "Fri")) }
    var time by remember { mutableStateOf(currentTimeFormatted) }
    var reminder by remember { mutableStateOf("On") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showFrequencyDropdown by remember { mutableStateOf(false) }
    var showReminderDropdown by remember { mutableStateOf(false) }

    var showCustomTimePicker by remember { mutableStateOf(false) }

    val frequencyOptions = listOf("Daily", "Weekdays", "Weekends", "Weekly", "Custom")
    val reminderOptions = listOf("On", "Off", "10 mins before", "30 mins before")

    if (showCustomTimePicker) {
        CustomTimePickerDialog(
            initialTime = time,
            preset = selectedPreset,
            onTimeSelected = { time = it },
            onDismiss = { showCustomTimePicker = false }
        )
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
                            imageVector = Icons.Outlined.WbSunny,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Add Routine",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Build better habits. Create a healthier you.",
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

            Spacer(modifier = Modifier.height(18.dp))

            // Preset Category Cards (Morning, Evening, Night, Custom)
            data class RoutinePresetUi(
                val type: RoutineTimeOfDay,
                val subtitle: String,
                val icon: ImageVector,
                val bgColor: Color,
                val iconBgColor: Color,
                val iconColor: Color,
                val selectedBorderColor: Color
            )

            val isDark = MaterialTheme.colorScheme.background == ScreenBackgroundDark
            val presets = listOf(
                RoutinePresetUi(
                    RoutineTimeOfDay.Morning,
                    "Start your day with purpose",
                    Icons.Outlined.WbSunny,
                    if (isDark) Color(0xFF162B28) else Color(0xFFEAF7F1),
                    if (isDark) Color(0xFF1F3D37) else Color(0xFFCBECE0),
                    if (isDark) Color(0xFF00E676) else Color(0xFF00A878),
                    if (isDark) Color(0xFF00E676) else Color(0xFF00A878)
                ),
                RoutinePresetUi(
                    RoutineTimeOfDay.Evening,
                    "Wind down and be present",
                    Icons.Outlined.Nightlight,
                    if (isDark) Color(0xFF241B3B) else Color(0xFFF3F0FF),
                    if (isDark) Color(0xFF332A52) else Color(0xFFE2DDFF),
                    if (isDark) Color(0xFF8B84FF) else Color(0xFF6C63FF),
                    if (isDark) Color(0xFF8B84FF) else Color(0xFF6C63FF)
                ),
                RoutinePresetUi(
                    RoutineTimeOfDay.Night,
                    "End the day with a goal",
                    Icons.Outlined.Star,
                    if (isDark) Color(0xFF2E1F1A) else Color(0xFFFFF3EA),
                    if (isDark) Color(0xFF422C23) else Color(0xFFFFE2CE),
                    if (isDark) Color(0xFFFFB300) else Color(0xFFFF6B00),
                    if (isDark) Color(0xFFFFB300) else Color(0xFFFF6B00)
                ),
                RoutinePresetUi(
                    RoutineTimeOfDay.Custom,
                    "Create your own routine",
                    Icons.Outlined.TrackChanges,
                    if (isDark) Color(0xFF2D1B28) else Color(0xFFFFF0F5),
                    if (isDark) Color(0xFF422338) else Color(0xFFFFD6E8),
                    if (isDark) Color(0xFFF472B6) else Color(0xFFEC4899),
                    if (isDark) Color(0xFFF472B6) else Color(0xFFEC4899)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    val isSelected = selectedPreset == preset.type
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp)
                            .clickable {
                                selectedPreset = preset.type
                                time = preset.type.defaultTime
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = preset.bgColor,
                        border = if (isSelected) BorderStroke(1.5.dp, preset.selectedBorderColor) else null
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(18.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(preset.selectedBorderColor),
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
                                        .background(preset.iconBgColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = preset.icon,
                                        contentDescription = null,
                                        tint = preset.iconColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = preset.type.label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = preset.subtitle,
                                    fontSize = 9.sp,
                                    color = TextGray,
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

            // Field 1: Routine Name (FULL Row Width)
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
                            text = "Routine Name",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextGray
                        )
                        val routinePlaceholder = when (selectedPreset) {
                            RoutineTimeOfDay.Morning -> "e.g. Morning Stretch & Meditation"
                            RoutineTimeOfDay.Evening -> "e.g. Evening Walk & Unwind"
                            RoutineTimeOfDay.Night   -> "e.g. Read 20 Pages before Sleep"
                            RoutineTimeOfDay.Custom  -> "e.g. Read 20 Minutes"
                        }
                        BasicTextField(
                            value = routineName,
                            onValueChange = { if (it.length <= 50) routineName = it },
                            cursorBrush = SolidColor(TextDark),
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            ),
                            decorationBox = { innerTextField ->
                                if (routineName.isEmpty()) {
                                    Text(
                                        text = routinePlaceholder,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.LightGray
                                    )
                                }
                                innerTextField()
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Text(
                        text = "${routineName.length}/50",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Field 2 & 3: Frequency & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Frequency Field
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showFrequencyDropdown = true },
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
                                    text = "Frequency",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextGray
                                )
                                Text(
                                    text = frequency,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showFrequencyDropdown,
                        onDismissRequest = { showFrequencyDropdown = false },
                        modifier = Modifier.background(White)
                    ) {
                        frequencyOptions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item, color = TextDark, fontWeight = FontWeight.Medium) },
                                onClick = {
                                    frequency = item
                                    showFrequencyDropdown = false
                                }
                            )
                        }
                    }
                }

                // Time Field
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showCustomTimePicker = true },
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
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = null,
                                tint = PrimaryPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Time",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextGray
                            )
                            Text(
                                text = time,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (frequency == "Custom" || frequency == "Custom Days") {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = InputFieldBg,
                    border = BorderStroke(1.dp, InputFieldBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Select Custom Days",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                                val isSelected = selectedCustomDays.contains(day)
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) PrimaryPurple else DividerColor)
                                        .clickable {
                                            selectedCustomDays = if (isSelected) {
                                                selectedCustomDays - day
                                            } else {
                                                selectedCustomDays + day
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.take(1),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TextDark
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Field 4: Reminder
            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showReminderDropdown = true },
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
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = null,
                                tint = PrimaryPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Reminder",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextGray
                            )
                            Text(
                                text = reminder,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showReminderDropdown,
                    onDismissRequest = { showReminderDropdown = false },
                    modifier = Modifier.background(White)
                ) {
                    reminderOptions.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item, color = TextDark, fontWeight = FontWeight.Medium) },
                            onClick = {
                                reminder = item
                                showReminderDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Field 5: Notes (Optional)
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
                                        text = "Add a short note about your routine...",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.LightGray
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

            Spacer(modifier = Modifier.height(20.dp))

            errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = ErrorRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Action Button: Add Routine
            Button(
                onClick = {
                    if (routineName.isBlank()) {
                        errorMessage = "Routine name is compulsory!"
                        return@Button
                    }
                    errorMessage = null
                    val freqEnum = when (frequency) {
                        "Weekly" -> RoutineFrequency.Weekly
                        "Weekdays" -> RoutineFrequency.Weekdays
                        "Weekends" -> RoutineFrequency.Weekends
                        "Custom", "Custom Days" -> RoutineFrequency.Custom
                        else -> RoutineFrequency.Daily
                    }
                    val newRoutine = RoutineModel(
                        name = routineName,
                        timeOfDay = selectedPreset,
                        frequency = freqEnum,
                        customDays = if (freqEnum == RoutineFrequency.Custom) selectedCustomDays.joinToString(",") else "",
                        time = time,
                        reminder = reminder == "On" || reminder.contains("before"),
                        notes = notes,
                        streak = 0,
                        isCompletedToday = false
                    )
                    onSave(newRoutine)
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
                        text = "Add Routine",
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
