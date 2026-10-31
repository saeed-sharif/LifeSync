package com.dalgeer.lifeplanner.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dalgeer.lifeplanner.data.model.*
import com.dalgeer.lifeplanner.ui.theme.*

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
        colors = DatePickerDefaults.colors(containerColor = CardSurface)
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
                            clockDialColor = SurfaceVariantColor,
                            clockDialSelectedContentColor = Color.White,
                            clockDialUnselectedContentColor = TextDark,
                            selectorColor = PrimaryPurple,
                            containerColor = CardSurface,
                            periodSelectorBorderColor = PrimaryPurple,
                            periodSelectorSelectedContainerColor = PrimaryPurple,
                            periodSelectorSelectedContentColor = Color.White,
                            periodSelectorUnselectedContainerColor = SurfaceVariantColor,
                            periodSelectorUnselectedContentColor = TextDark,
                            timeSelectorSelectedContainerColor = IconBoxBg,
                            timeSelectorSelectedContentColor = PrimaryPurple,
                            timeSelectorUnselectedContainerColor = SurfaceVariantColor,
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
                Text("Confirm Time", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextGray)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = CardSurface
    )
}

@Composable
fun ExitConfirmationDialog(
    onConfirmExit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = CardSurface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(IconBoxBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                        contentDescription = "Exit App",
                        tint = PrimaryPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Exit LifeSync?",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Close Application",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }
        },
        text = {
            Text(
                text = "Are you sure you want to close the app? All your tasks, routines, and goals are saved and up to date.",
                fontSize = 13.sp,
                color = TextGray,
                lineHeight = 18.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmExit,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Exit App",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, DividerColor),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Stay in App",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
            }
        }
    )
}
