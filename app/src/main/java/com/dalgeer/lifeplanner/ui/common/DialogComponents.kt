package com.dalgeer.lifeplanner.ui.common

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

// --- Official Material 3 Date Picker Dialog ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    initialDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                        onDateSelected(formatter.format(java.util.Date(millis)))
                    }
                    onDismiss()
                }
            ) {
                Text("OK", fontWeight = FontWeight.Bold, color = PrimaryPurple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextGray)
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = DatePickerDefaults.colors(containerColor = White)
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePickerDialog(
    initialTime: String,
    preset: RoutineTimeOfDay = RoutineTimeOfDay.Custom,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val initialParts = initialTime.trim().split(" ")
    val timeParts = initialParts.firstOrNull()?.split(":")
    val parsedHour12 = timeParts?.getOrNull(0)?.toIntOrNull() ?: 8
    val parsedMinute = timeParts?.getOrNull(1)?.toIntOrNull() ?: 30
    val isPm = initialParts.getOrNull(1)?.equals("PM", ignoreCase = true) ?: false

    var initialHour24 = parsedHour12
    if (isPm && parsedHour12 < 12) initialHour24 += 12
    if (!isPm && parsedHour12 == 12) initialHour24 = 0

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour24,
        initialMinute = parsedMinute,
        is24Hour = false
    )

    var dialogError by remember { mutableStateOf<String?>(null) }

    val currentHour12 = when (val h = timePickerState.hour % 12) {
        0 -> 12
        else -> h
    }
    val currentIsAm = timePickerState.hour < 12

    val isValid = preset.isValidTime(currentHour12, currentIsAm)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Select Time (${preset.label})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextDark
                )
                if (preset != RoutineTimeOfDay.Custom) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(preset.ruleText, fontSize = 11.sp, color = PrimaryPurple)
                }
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (preset != RoutineTimeOfDay.Custom) {
                    Surface(
                        color = if (isValid) LightGreen else LightRed,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = if (isValid) "✓ Valid ${preset.label} Time" else "⚠️ Invalid for ${preset.label} (${preset.ruleText})",
                            color = if (isValid) SuccessGreen else ErrorRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                if (dialogError != null) {
                    Text(
                        text = dialogError!!,
                        color = ErrorRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = Color(0xFFF3F4F6),
                            clockDialSelectedContentColor = White,
                            clockDialUnselectedContentColor = TextDark,
                            selectorColor = PrimaryPurple,
                            containerColor = White,
                            periodSelectorBorderColor = PrimaryPurple,
                            periodSelectorSelectedContainerColor = PrimaryPurple,
                            periodSelectorSelectedContentColor = White,
                            periodSelectorUnselectedContainerColor = Color(0xFFF3F4F6),
                            periodSelectorUnselectedContentColor = TextDark,
                            timeSelectorSelectedContainerColor = Color(0xFFF0EFFF),
                            timeSelectorSelectedContentColor = PrimaryPurple,
                            timeSelectorUnselectedContainerColor = Color(0xFFF3F4F6),
                            timeSelectorUnselectedContentColor = TextDark
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!preset.isValidTime(currentHour12, currentIsAm)) {
                        dialogError = preset.errorText
                    } else {
                        val cal = java.util.Calendar.getInstance().apply {
                            set(java.util.Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(java.util.Calendar.MINUTE, timePickerState.minute)
                        }
                        val formatter = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                        onTimeSelected(formatter.format(cal.time))
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirm Time", fontWeight = FontWeight.Bold, color = White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextGray)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = White
    )
}
