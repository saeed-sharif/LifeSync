package com.dalgeer.lifeplanner.ui.screens.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
                                    color = TextGray
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                categoryList.take(3).forEach { (catEnum, catLabel) ->
                    val isSelected = selectedCategory == catEnum
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = catEnum },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) IconBoxBg else CardSurface,
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                categoryList.drop(3).forEach { (catEnum, catLabel) ->
                    val isSelected = selectedCategory == catEnum
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = catEnum },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) IconBoxBg else CardSurface,
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                priorityList.forEach { (prioEnum, prioLabel) ->
                    val isSelected = selectedPriority == prioEnum
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedPriority = prioEnum },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) IconBoxBg else CardSurface,
                        border = BorderStroke(1.dp, if (isSelected) PrimaryPurple else InputFieldBorder)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = prioLabel,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryPurple else TextDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Field 4 & 5: Date & Time Pickers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Date", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
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
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = date, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark, maxLines = 1)
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Time", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCustomTimePicker = true },
                        shape = RoundedCornerShape(14.dp),
                        color = InputFieldBg,
                        border = BorderStroke(1.dp, InputFieldBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Schedule, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = time, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark, maxLines = 1)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Field 6: Repeat Selector Dropdown
            Text("Repeat", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
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
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Autorenew, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = repeatOption, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp))
                    }
                }

                DropdownMenu(
                    expanded = showRepeatDropdown,
                    onDismissRequest = { showRepeatDropdown = false },
                    modifier = Modifier.background(CardSurface)
                ) {
                    repeatOptions.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt, color = TextDark) },
                            onClick = {
                                repeatOption = opt
                                showRepeatDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Field 7: Notes / Description
            Text("Notes (Optional)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = InputFieldBg,
                border = BorderStroke(1.dp, InputFieldBorder)
            ) {
                BasicTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    cursorBrush = SolidColor(TextDark),
                    textStyle = TextStyle(fontSize = 14.sp, color = TextDark),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.padding(14.dp)) {
                            if (notes.isEmpty()) {
                                Text("Add extra details...", fontSize = 14.sp, color = TextGray)
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp, max = 100.dp)
                )
            }

            errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = msg, color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Task Button
            Button(
                onClick = {
                    if (taskTitle.isBlank()) {
                        errorMessage = "Please enter a task title"
                    } else {
                        val task = TaskModel(
                            id = existingTask?.id ?: java.util.UUID.randomUUID().toString(),
                            title = taskTitle.trim(),
                            category = selectedCategory,
                            priority = selectedPriority,
                            date = date,
                            time = time,
                            notes = notes.trim(),
                            repeatOption = repeatOption,
                            status = existingTask?.status ?: TaskStatus.Pending
                        )
                        onSave(task)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text(
                    text = if (existingTask != null) "Update Task" else "Save Task",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
