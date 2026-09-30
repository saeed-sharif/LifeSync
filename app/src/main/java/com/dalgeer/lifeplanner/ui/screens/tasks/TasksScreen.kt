package com.dalgeer.lifeplanner.ui.screens.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalgeer.lifeplanner.data.model.*
import com.dalgeer.lifeplanner.presentation.uiState.UiState
import com.dalgeer.lifeplanner.presentation.viewmodel.TaskViewModel
import com.dalgeer.lifeplanner.ui.components.AppFloatingActionButton
import com.dalgeer.lifeplanner.ui.screens.tasks.NewTaskBottomSheet
import com.dalgeer.lifeplanner.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun TasksScreen(
    highlightedTaskId: String? = null,
    initialFilterChip: String = "Today",
    expandCompleted: Boolean = false,
    viewModel: TaskViewModel = koinViewModel()
) {
    var selectedFilterChip by remember(initialFilterChip) { mutableStateOf(initialFilterChip) }
    var searchQuery by remember { mutableStateOf("") }
    var showNewTaskSheet by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskModel?>(null) }
    var isUpcomingExpanded by remember { mutableStateOf(false) }
    var isCompletedExpanded by remember(expandCompleted, initialFilterChip) {
        mutableStateOf(expandCompleted || initialFilterChip == "Completed")
    }

    var selectedDayIndex by remember { mutableIntStateOf(0) }
    var showCustomDatePicker by remember { mutableStateOf(false) }

    val calendarStripDays = remember {
        val list = mutableListOf<Triple<String, String, String>>()
        val cal = java.util.Calendar.getInstance()
        val dayFormat = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault())
        val numFormat = java.text.SimpleDateFormat("d", java.util.Locale.getDefault())
        val fullFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())

        for (i in 0 until 5) {
            val dayName = dayFormat.format(cal.time)
            val dayNumber = numFormat.format(cal.time)
            val fullDateStr = fullFormat.format(cal.time)
            list.add(Triple(dayName, dayNumber, fullDateStr))
            cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .statusBarsPadding()
                .padding(horizontal = PaddingLarge)
        ) {
            // FIXED Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "Tasks",
                    style = Typography.headlineLarge,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Stay focused. Get things done.",
                    style = Typography.bodyMedium,
                    color = TextGray
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                placeholder = { Text("Search tasks...", color = TextGray, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextGray,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = TextGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = DividerColor,
                    cursorColor = TextDark,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark
                )
            )

            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryPurple)
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = ErrorRed)
                    }
                }
                is UiState.Success -> {
                    val rawTasks = state.data
                    val tasks = if (searchQuery.isBlank()) rawTasks else rawTasks.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.notes.contains(searchQuery, ignoreCase = true) ||
                        it.category.name.contains(searchQuery, ignoreCase = true)
                    }
                    val overdueTasks = tasks.filter { it.isOverdue() }
                    val nonOverdueTasks = tasks.filter { !it.isOverdue() }
                    val todayTasks = nonOverdueTasks.filter { it.status != TaskStatus.Completed }
                    val priorityTasks = tasks.filter { it.priority == TaskPriority.High && it.status != TaskStatus.Completed }
                    val completedTasks = tasks.filter { it.status == TaskStatus.Completed }
                    val upcomingTasks = nonOverdueTasks.filter { !it.date.equals("Today", ignoreCase = true) && it.status != TaskStatus.Completed }

                    val currentDayTriple = calendarStripDays.getOrElse(selectedDayIndex) { calendarStripDays[0] }
                    val dayFilteredTodayTasks = todayTasks.filter { task ->
                        val taskDate = task.date.trim()
                        val dayNum = currentDayTriple.second.trim()
                        val fullDate = currentDayTriple.third.trim()
                        when (selectedDayIndex) {
                            0 -> taskDate.equals("Today", ignoreCase = true) || taskDate.contains("Today", ignoreCase = true) || taskDate.contains(fullDate, ignoreCase = true) || taskDate.contains(dayNum, ignoreCase = true)
                            1 -> taskDate.equals("Tomorrow", ignoreCase = true) || taskDate.contains("Tomorrow", ignoreCase = true) || taskDate.contains(fullDate, ignoreCase = true) || taskDate.contains(dayNum, ignoreCase = true)
                            else -> taskDate.contains(fullDate, ignoreCase = true) || taskDate.contains(dayNum, ignoreCase = true)
                        }
                    }

                    // Filter Chips with Dynamic Counts
                    val chips = listOf(
                        "Today" to dayFilteredTodayTasks.size.toString(),
                        "Priorities" to priorityTasks.size.toString(),
                        "My Lists" to "2",
                        "Completed" to completedTasks.size.toString()
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(chips) { (chip, count) ->
                            val isSelected = selectedFilterChip == chip
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { selectedFilterChip = chip },
                                color = if (isSelected) PrimaryPurple else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when (chip) {
                                            "Today" -> "📅 $chip"
                                            "Priorities" -> "🏳️ $chip"
                                            "My Lists" -> "☰ $chip"
                                            else -> "☑️ $chip"
                                        },
                                        color = if (isSelected) Color.White else TextDark,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                    if (count.isNotEmpty() && count != "0") {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = if (isSelected) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.outlineVariant,
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                text = count,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                color = if (isSelected) Color.White else TextDark,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Date Strip Card Row (Sat 26, Sun 27, Mon 28, Tue 29, Wed 30 + Calendar Picker)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            calendarStripDays.forEachIndexed { index, (dayName, dayNumber, _) ->
                                val isSelected = selectedDayIndex == index
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { selectedDayIndex = index },
                                    color = if (isSelected) PrimaryPurple else White,
                                    shape = RoundedCornerShape(16.dp),
                                    border = if (!isSelected) BorderStroke(1.dp, DividerColor) else null,
                                    shadowElevation = if (isSelected) 4.dp else 0.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = dayName,
                                            fontSize = 11.sp,
                                            color = if (isSelected) White.copy(alpha = 0.85f) else TextGray,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = dayNumber,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) White else TextDark
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Calendar Icon Button
                        Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { showCustomDatePicker = true },
                            color = White,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, DividerColor)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = "Select Date",
                                    tint = PrimaryPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Task List based on Selected Chip
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        when (selectedFilterChip) {
                            "Today" -> {
                                // OVERDUE SECTION (if any overdue tasks exist)
                                if (overdueTasks.isNotEmpty()) {
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "⭕ ",
                                                fontSize = 13.sp,
                                                color = ErrorRed
                                            )
                                            Text(
                                                text = "Overdue (${overdueTasks.size})",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ErrorRed
                                            )
                                        }
                                    }

                                    items(overdueTasks, key = { "overdue_" + it.id }) { task ->
                                        TaskCardDynamicItem(
                                            task = task,
                                            onToggle = { viewModel.toggleTaskStatus(task.id) },
                                            onDelete = { viewModel.deleteTask(task.id) },
                                            onEdit = { editingTask = task },
                                            isHighlighted = task.id == highlightedTaskId,
                                            isOverdueStyle = true
                                        )
                                    }

                                    item { Spacer(modifier = Modifier.height(6.dp)) }
                                }

                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "⭕ ",
                                                fontSize = 13.sp,
                                                color = PrimaryPurple
                                            )
                                            Text(
                                                text = "${currentDayTriple.first} ${currentDayTriple.second} (${dayFilteredTodayTasks.size})",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextDark
                                            )
                                        }
                                        Surface(
                                            color = Color(0xFFF0EFFF),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = "${dayFilteredTodayTasks.size} task${if (dayFilteredTodayTasks.size != 1) "s" else ""}",
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                                color = PrimaryPurple,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                if (dayFilteredTodayTasks.isEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("No tasks for ${currentDayTriple.first} ${currentDayTriple.second}. Tap + to add one!", fontSize = 13.sp, color = TextGray)
                                        }
                                    }
                                } else {
                                    items(dayFilteredTodayTasks, key = { it.id }) { task ->
                                        TaskCardDynamicItem(
                                            task = task,
                                            onToggle = { viewModel.toggleTaskStatus(task.id) },
                                            onDelete = { viewModel.deleteTask(task.id) },
                                            onEdit = { editingTask = task },
                                            isHighlighted = task.id == highlightedTaskId
                                        )
                                    }
                                }

                                // UPCOMING SECTION (Collapsible)
                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { isUpcomingExpanded = !isUpcomingExpanded }
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "UPCOMING (${upcomingTasks.size})",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextDark
                                            )
                                        }
                                        Icon(
                                            imageVector = if (isUpcomingExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = TextGray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                if (isUpcomingExpanded) {
                                    if (upcomingTasks.isEmpty()) {
                                        item {
                                            Text("No upcoming tasks", fontSize = 12.sp, color = TextGray, modifier = Modifier.padding(start = 24.dp))
                                        }
                                    } else {
                                        items(upcomingTasks, key = { "up_" + it.id }) { task ->
                                            TaskCardDynamicItem(
                                                task = task,
                                                onToggle = { viewModel.toggleTaskStatus(task.id) },
                                                onDelete = { viewModel.deleteTask(task.id) },
                                                onEdit = { editingTask = task },
                                                isHighlighted = task.id == highlightedTaskId
                                            )
                                        }
                                    }
                                }

                                // COMPLETED SECTION (Collapsible)
                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { isCompletedExpanded = !isCompletedExpanded }
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "COMPLETED (${completedTasks.size})",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextDark
                                            )
                                        }
                                        Icon(
                                            imageVector = if (isCompletedExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = TextGray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                if (isCompletedExpanded) {
                                    if (completedTasks.isEmpty()) {
                                        item {
                                            Text("No completed tasks yet", fontSize = 12.sp, color = TextGray, modifier = Modifier.padding(start = 24.dp))
                                        }
                                    } else {
                                        items(completedTasks, key = { "comp_" + it.id }) { task ->
                                            TaskCardDynamicItem(
                                                task = task,
                                                onToggle = { viewModel.toggleTaskStatus(task.id) },
                                                onDelete = { viewModel.deleteTask(task.id) },
                                                onEdit = { editingTask = task },
                                                isHighlighted = task.id == highlightedTaskId
                                            )
                                        }
                                    }
                                }
                            }
                            "Priorities" -> {
                                item {
                                    Text("HIGH PRIORITY TASKS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                                }
                                if (priorityTasks.isEmpty()) {
                                    item {
                                        Text("No high priority tasks", fontSize = 13.sp, color = TextGray, modifier = Modifier.padding(top = 16.dp))
                                    }
                                } else {
                                    items(priorityTasks, key = { "pri_" + it.id }) { task ->
                                        TaskCardDynamicItem(
                                            task = task,
                                            onToggle = { viewModel.toggleTaskStatus(task.id) },
                                            onDelete = { viewModel.deleteTask(task.id) },
                                            onEdit = { editingTask = task },
                                            isHighlighted = task.id == highlightedTaskId
                                        )
                                    }
                                }
                            }
                            "Completed" -> {
                                item {
                                    Text("COMPLETED TASKS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                }
                                if (completedTasks.isEmpty()) {
                                    item {
                                        Text("No completed tasks yet", fontSize = 13.sp, color = TextGray, modifier = Modifier.padding(top = 16.dp))
                                    }
                                } else {
                                    items(completedTasks, key = { "all_comp_" + it.id }) { task ->
                                        TaskCardDynamicItem(
                                            task = task,
                                            onToggle = { viewModel.toggleTaskStatus(task.id) },
                                            onDelete = { viewModel.deleteTask(task.id) },
                                            onEdit = { editingTask = task },
                                            isHighlighted = task.id == highlightedTaskId
                                        )
                                    }
                                }
                            }
                            else -> {
                                item {
                                    Text("All Lists & Categories", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                }
                                if (tasks.isEmpty()) {
                                    item {
                                        Text("No tasks found", fontSize = 13.sp, color = TextGray, modifier = Modifier.padding(top = 16.dp))
                                    }
                                } else {
                                    items(tasks, key = { "all_" + it.id }) { task ->
                                        TaskCardDynamicItem(
                                            task = task,
                                            onToggle = { viewModel.toggleTaskStatus(task.id) },
                                            onDelete = { viewModel.deleteTask(task.id) },
                                            onEdit = { editingTask = task },
                                            isHighlighted = task.id == highlightedTaskId
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Contextual FAB
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 32.dp)
        ) {
            AppFloatingActionButton(
                onClick = { showNewTaskSheet = true },
                isRoundedSquare = true
            )
        }

        if (showNewTaskSheet) {
            NewTaskBottomSheet(
                onDismiss = { showNewTaskSheet = false },
                onSave = { task ->
                    viewModel.addTask(task)
                    showNewTaskSheet = false
                }
            )
        }

        editingTask?.let { taskToEdit ->
            NewTaskBottomSheet(
                existingTask = taskToEdit,
                onDismiss = { editingTask = null },
                onSave = { updatedTask ->
                    viewModel.addTask(updatedTask)
                    editingTask = null
                }
            )
        }

        if (showCustomDatePicker) {
            com.dalgeer.lifeplanner.ui.common.CustomDatePickerDialog(
                initialDate = "Today",
                onDateSelected = { _ ->
                    showCustomDatePicker = false
                },
                onDismiss = { showCustomDatePicker = false }
            )
        }
    }
}

@Composable
fun TaskCardDynamicItem(
    task: TaskModel,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit = {},
    isHighlighted: Boolean = false,
    isOverdueStyle: Boolean = false
) {
    val isCompleted = task.status == TaskStatus.Completed
    val isHigh = task.priority == TaskPriority.High

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isHighlighted) {
                    Modifier.border(1.5.dp, PrimaryPurple, RoundedCornerShape(16.dp))
                } else Modifier
            )
            .clickable { onEdit() },
        shape = RoundedCornerShape(16.dp),
        color = if (isHighlighted) Color(0xFFF4F0FF) else if (isOverdueStyle) Color(0xFFFFF0F0) else White,
        border = if (isOverdueStyle) BorderStroke(1.dp, Color(0xFFFFD0D0)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggle() },
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(task.category.getCategoryColor()),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = task.category.getCategoryIcon(),
                    contentDescription = null,
                    tint = task.category.getCategoryTint(),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = Typography.titleMedium,
                    color = if (isCompleted) TextGray else TextDark,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${task.category.name} • ${task.time}",
                    style = Typography.bodySmall,
                    color = TextGray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = if (isHigh) LightRed else Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isHigh) "HIGH PRIORITY" else task.priority.name.uppercase(),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = if (isHigh) ErrorRed else TextGray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextGray, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
