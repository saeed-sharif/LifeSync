package com.dalgeer.lifeplanner.ui.screens.tasks

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dalgeer.lifeplanner.data.model.*
import com.dalgeer.lifeplanner.ui.common.CustomDatePickerDialog
import com.dalgeer.lifeplanner.ui.common.CustomTimePickerDialog
import com.dalgeer.lifeplanner.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskBottomSheet(
    onDismiss: () -> Unit,
    onSave: (TaskModel) -> Unit,
    existingTask: TaskModel? = null
) {
    val currentTimeFormatted = remember {
        java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
    }
    val currentDateFormatted = remember {
        java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date())
    }

    var taskTitle by remember { mutableStateOf(existingTask?.title ?: "") }
    var selectedCategory by remember { mutableStateOf(existingTask?.category ?: TaskCategory.Work) }
    var selectedPriority by remember { mutableStateOf(existingTask?.priority ?: TaskPriority.Medium) }
    var date by remember { mutableStateOf(existingTask?.date ?: "Today, $currentDateFormatted") }
    var time by remember { mutableStateOf(existingTask?.time ?: currentTimeFormatted) }
    var notes by remember { mutableStateOf(existingTask?.notes ?: "") }
    var repeatOption by remember { mutableStateOf(existingTask?.repeatOption ?: "Doesn't repeat") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showRepeatDropdown by remember { mutableStateOf(false) }
    var showCustomDatePicker by remember { mutableStateOf(false) }
    var showCustomTimePicker by remember { mutableStateOf(false) }

    val categoryList = listOf(
        TaskCategory.Work to "💼 Work",
        TaskCategory.Personal to "👤 Personal",
        TaskCategory.Health to "❤️ Health",
        TaskCategory.Utilities to "🎓 Study",
        TaskCategory.General to "📌 Other"
    )

    val priorityList = listOf(
        TaskPriority.Low to "🟢 Low",
        TaskPriority.Medium to "🟡 Medium",
        TaskPriority.High to "🔴 High"
    )

    val repeatOptions = listOf("Doesn't repeat", "Every Day", "Every Week", "Every Month")

    if (showCustomDatePicker) {
        CustomDatePickerDialog(
            initialDate = date,
            onDateSelected = { date = it },
            onDismiss = { showCustomDatePicker = false }
        )
    }

    if (showCustomTimePicker) {
        CustomTimePickerDialog(
            initialTime = time,
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
            // Header: Title & Close Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (existingTask != null) "Edit Task" else "Add Task",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

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

            Spacer(modifier = Modifier.height(16.dp))

            // Field 1: Title *
            Text(
                text = "Title *",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = InputFieldBg,
                border = BorderStroke(1.dp, InputFieldBorder)
            ) {
                BasicTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    cursorBrush = SolidColor(TextDark),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    ),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.padding(14.dp)) {
                            if (taskTitle.isEmpty()) {
                                Text(
                                    text = "Task title...",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.LightGray
                                )
                            }
                            innerTextField()
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Field 2: Category Grid Chips
            Text(
                text = "Category",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoryList.take(3).forEach { (catEnum, catLabel) ->
                    val isSelected = selectedCategory == catEnum
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = catEnum },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFFF0EFFF) else White,
                        border = BorderStroke(1.dp, if (isSelected) PrimaryPurple else InputFieldBorder)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = catLabel,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryPurple else TextDark
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoryList.drop(3).forEach { (catEnum, catLabel) ->
                    val isSelected = selectedCategory == catEnum
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = catEnum },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFFF0EFFF) else White,
                        border = BorderStroke(1.dp, if (isSelected) PrimaryPurple else InputFieldBorder)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = catLabel,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryPurple else TextDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Field 3: Priority Chips
            Text(
                text = "Priority",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                priorityList.forEach { (pEnum, pLabel) ->
                    val isSelected = selectedPriority == pEnum
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedPriority = pEnum },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFFF0EFFF) else White,
                        border = BorderStroke(1.dp, if (isSelected) PrimaryPurple else InputFieldBorder)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = pLabel,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryPurple else TextDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Field 4: Date
            Text(
                text = "Date",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCustomDatePicker = true },
                shape = RoundedCornerShape(14.dp),
                color = InputFieldBg,
                border = BorderStroke(1.dp, InputFieldBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = date,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Field 5: Time & Reminder
            Text(
                text = "Time",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Time Card
                Surface(
                    modifier = Modifier
                        .weight(1.3f)
                        .clickable { showCustomTimePicker = true },
                    shape = RoundedCornerShape(14.dp),
                    color = InputFieldBg,
                    border = BorderStroke(1.dp, InputFieldBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = time,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                    }
                }

                // Reminder Pill
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF0EFFF),
                    border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Reminder",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Field 6: Repeat
            Text(
                text = "Repeat",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showRepeatDropdown = true },
                    shape = RoundedCornerShape(14.dp),
                    color = InputFieldBg,
                    border = BorderStroke(1.dp, InputFieldBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Sync,
                                contentDescription = null,
                                tint = PrimaryPurple,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = repeatOption,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
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

                DropdownMenu(
                    expanded = showRepeatDropdown,
                    onDismissRequest = { showRepeatDropdown = false },
                    modifier = Modifier.background(White)
                ) {
                    repeatOptions.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item, color = TextDark) },
                            onClick = {
                                repeatOption = item
                                showRepeatDropdown = false
                            }
                        )
                    }
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

            // Bottom Button: Save Task
            Button(
                onClick = {
                    if (taskTitle.isBlank()) {
                        errorMessage = "Task title is compulsory!"
                        return@Button
                    }
                    errorMessage = null
                    val taskToSave = (existingTask ?: TaskModel(title = taskTitle.trim())).copy(
                        title = taskTitle.trim(),
                        category = selectedCategory,
                        priority = selectedPriority,
                        date = date,
                        time = time,
                        notes = notes,
                        repeatOption = repeatOption
                    )
                    onSave(taskToSave)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = if (existingTask != null) "Save Changes" else "Save Task",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
