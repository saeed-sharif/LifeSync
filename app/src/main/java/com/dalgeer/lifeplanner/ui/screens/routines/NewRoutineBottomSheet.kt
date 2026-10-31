package com.dalgeer.lifeplanner.ui.screens.routines

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Notes
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
import com.dalgeer.lifeplanner.ui.common.CustomTimePickerDialog
import com.dalgeer.lifeplanner.ui.theme.*

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
                            .background(IconBoxBg),
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
                        tint = TextDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Preset Category Cards
            data class RoutinePresetUi(
                val type: RoutineTimeOfDay,
                val subtitle: String,
                val icon: ImageVector,
                val bgColor: Color,
                val iconBgColor: Color,
                val iconColor: Color,
                val selectedBorderColor: Color
            )

            val presets = listOf(
                RoutinePresetUi(
                    RoutineTimeOfDay.Morning,
                    "Start your day with purpose",
                    Icons.Outlined.WbSunny,
                    if (isDarkTheme) SurfaceVariantDark else Color(0xFFEAF7F1),
                    if (isDarkTheme) LightGreenDark else Color(0xFFCBECE0),
                    if (isDarkTheme) SuccessGreenDark else Color(0xFF00A878),
                    if (isDarkTheme) SuccessGreenDark else Color(0xFF00A878)
                ),
                RoutinePresetUi(
                    RoutineTimeOfDay.Evening,
                    "Wind down and be present",
                    Icons.Outlined.Nightlight,
                    if (isDarkTheme) SurfaceVariantDark else Color(0xFFF3F0FF),
                    if (isDarkTheme) PrimaryContainerDark else Color(0xFFE2DDFF),
                    if (isDarkTheme) PrimaryPurpleDark else Color(0xFF6C63FF),
                    if (isDarkTheme) PrimaryPurpleDark else Color(0xFF6C63FF)
                ),
                RoutinePresetUi(
                    RoutineTimeOfDay.Night,
                    "End the day with a goal",
                    Icons.Outlined.Star,
                    if (isDarkTheme) SurfaceVariantDark else Color(0xFFFFF3EA),
                    if (isDarkTheme) Color(0xFF3D2A12) else Color(0xFFFFE2CE),
                    if (isDarkTheme) Color(0xFFFFB74D) else Color(0xFFFF6B00),
                    if (isDarkTheme) Color(0xFFFFB74D) else Color(0xFFFF6B00)
                ),
                RoutinePresetUi(
                    RoutineTimeOfDay.Custom,
                    "Create your own routine",
                    Icons.Outlined.TrackChanges,
                    if (isDarkTheme) SurfaceVariantDark else Color(0xFFFFF0F5),
                    if (isDarkTheme) Color(0xFF422338) else Color(0xFFFFD6E8),
                    if (isDarkTheme) Color(0xFFF472B6) else Color(0xFFEC4899),
                    if (isDarkTheme) Color(0xFFF472B6) else Color(0xFFEC4899)
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
                        border = if (isSelected) BorderStroke(1.5.dp, preset.selectedBorderColor) else BorderStroke(1.dp, DividerColor)
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

            // Field 1: Routine Name
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
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(IconBoxBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ROUTINE NAME *",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGray,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        BasicTextField(
                            value = routineName,
                            onValueChange = { routineName = it },
                            cursorBrush = SolidColor(TextDark),
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            ),
                            decorationBox = { innerTextField ->
                                if (routineName.isEmpty()) {
                                    Text(
                                        text = "e.g. Morning Hydration",
                                        fontSize = 15.sp,
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

            Spacer(modifier = Modifier.height(12.dp))

            // Field 2 & 3: Frequency & Time Pickers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Frequency Dropdown
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
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(IconBoxBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Repeat,
                                    contentDescription = null,
                                    tint = PrimaryPurple,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "REPEAT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextGray
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = frequency,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = showFrequencyDropdown,
                        onDismissRequest = { showFrequencyDropdown = false },
                        modifier = Modifier.background(CardSurface)
                    ) {
                        frequencyOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt, color = TextDark) },
                                onClick = {
                                    frequency = opt
                                    showFrequencyDropdown = false
                                }
                            )
                        }
                    }
                }

                // Time Picker Clickable Box
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
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
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(IconBoxBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Schedule,
                                    contentDescription = null,
                                    tint = PrimaryPurple,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "TIME",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextGray
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = time,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Custom Day Selector Bar
            if (frequency == "Custom") {
                Spacer(modifier = Modifier.height(10.dp))
                Text("Select Days", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    days.forEach { day ->
                        val isDaySelected = selectedCustomDays.contains(day)
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isDaySelected) PrimaryPurple else SurfaceVariantColor)
                                .clickable {
                                    selectedCustomDays = if (isDaySelected) {
                                        selectedCustomDays - day
                                    } else {
                                        selectedCustomDays + day
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.take(1),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDaySelected) Color.White else TextDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Field 4 & 5: Reminder & Notes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Reminder Dropdown Box
                Box(modifier = Modifier.weight(1f)) {
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
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
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
                                    text = "REMINDER",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextGray
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = reminder,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = showReminderDropdown,
                        onDismissRequest = { showReminderDropdown = false },
                        modifier = Modifier.background(CardSurface)
                    ) {
                        reminderOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt, color = TextDark) },
                                onClick = {
                                    reminder = opt
                                    showReminderDropdown = false
                                }
                            )
                        }
                    }
                }

                // Notes Input Box
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(IconBoxBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Notes,
                                contentDescription = null,
                                tint = PrimaryPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "NOTES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextGray
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            BasicTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                cursorBrush = SolidColor(TextDark),
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                ),
                                decorationBox = { innerTextField ->
                                    if (notes.isEmpty()) {
                                        Text(
                                            text = "Optional",
                                            fontSize = 14.sp,
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

            errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = msg, color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Routine Button
            Button(
                onClick = {
                    if (routineName.isBlank()) {
                        errorMessage = "Please enter a routine name"
                    } else {
                        val freqEnum = when (frequency) {
                            "Weekdays" -> RoutineFrequency.Weekdays
                            "Weekends" -> RoutineFrequency.Weekends
                            "Weekly" -> RoutineFrequency.Weekly
                            "Custom" -> RoutineFrequency.Custom
                            else -> RoutineFrequency.Daily
                        }
                        val routine = RoutineModel(
                            id = java.util.UUID.randomUUID().toString(),
                            name = routineName.trim(),
                            timeOfDay = selectedPreset,
                            frequency = freqEnum,
                            customDays = if (freqEnum == RoutineFrequency.Custom) selectedCustomDays.joinToString(", ") else "",
                            time = time,
                            reminder = reminder != "Off",
                            notes = notes.trim(),
                            streak = 0,
                            isCompletedToday = false
                        )
                        onSave(routine)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text(
                    text = "Save Routine",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
