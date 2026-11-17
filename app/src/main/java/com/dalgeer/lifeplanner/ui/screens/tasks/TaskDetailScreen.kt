package com.dalgeer.lifeplanner.ui.screens.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalgeer.lifeplanner.data.model.TaskPriority
import com.dalgeer.lifeplanner.data.model.TaskStatus
import com.dalgeer.lifeplanner.data.model.getCategoryColor
import com.dalgeer.lifeplanner.data.model.getCategoryIcon
import com.dalgeer.lifeplanner.data.model.getCategoryTint
import com.dalgeer.lifeplanner.presentation.uiState.UiState
import com.dalgeer.lifeplanner.presentation.viewmodel.TaskViewModel
import com.dalgeer.lifeplanner.util.AlarmScheduler
import com.dalgeer.lifeplanner.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String,
    onBack: () -> Unit,
    viewModel: TaskViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tasks = (uiState as? UiState.Success)?.data ?: emptyList()
    val task = tasks.find { it.id == taskId }

    var showMenu by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    if (task == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Task not found", color = TextGray, fontSize = 14.sp)
        }
        return
    }

    val isCompleted = task.status == TaskStatus.Completed
    val isHigh = task.priority == TaskPriority.High

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingLarge, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                        .border(1.dp, DividerColor, CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Task Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Direct Visible Delete Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ErrorRed.copy(alpha = 0.12f))
                        .border(1.dp, ErrorRed.copy(alpha = 0.3f), CircleShape)
                        .clickable { showDeleteConfirmDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete Task",
                        tint = ErrorRed,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = TextDark
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(CardSurface)
                    ) {
                        if (!isCompleted) {
                            DropdownMenuItem(
                                text = { Text("Edit Task", color = TextDark) },
                                onClick = {
                                    showMenu = false
                                    showEditSheet = true
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Delete Task", color = ErrorRed) },
                            onClick = {
                                showMenu = false
                                showDeleteConfirmDialog = true
                            }
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = PaddingLarge)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Main Header Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = CardElevation,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = PrimaryPurple.copy(alpha = 0.12f),
                        spotColor = PrimaryPurple.copy(alpha = 0.12f)
                    ),
                shape = RoundedCornerShape(20.dp),
                color = CardSurface
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(task.category.getCategoryColor()),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = task.category.getCategoryIcon(),
                            contentDescription = null,
                            tint = task.category.getCategoryTint(),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryPurple)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = task.category.name,
                                fontSize = 12.sp,
                                color = TextGray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Surface(
                        color = if (isHigh) LightRed else SurfaceVariantColor,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = task.priority.name.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = if (isHigh) ErrorRed else TextGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Details Section Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = CardSurface,
                border = BorderStroke(1.dp, DividerColor)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // 1. Date
                    DetailRowItem(
                        icon = Icons.Outlined.CalendarToday,
                        label = "Date",
                        value = task.date
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)

                    // 2. Time
                    DetailRowItem(
                        icon = Icons.Outlined.Schedule,
                        label = "Time",
                        value = task.time
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)

                    // 3. Repeat
                    DetailRowItem(
                        icon = Icons.Outlined.Autorenew,
                        label = "Repeat",
                        value = task.repeatOption
                    )

                    if (task.notes.isNotBlank()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)
                        // 4. Notes
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(IconBoxBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notes,
                                    contentDescription = null,
                                    tint = PrimaryPurple,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Notes", fontSize = 11.sp, color = TextGray)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = task.notes,
                                    fontSize = 14.sp,
                                    color = TextDark,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Toggle Mark Complete Button
            val buttonBg = if (isCompleted) {
                if (isDarkTheme) Color(0xFF064E3B) else Color(0xFFDCFCE7)
            } else {
                PrimaryPurple
            }

            val buttonTextColor = if (isCompleted) {
                if (isDarkTheme) SuccessGreenDark else Color(0xFF15803D)
            } else {
                Color.White
            }

            val buttonBorder = if (isCompleted) {
                BorderStroke(1.5.dp, if (isDarkTheme) SuccessGreen else Color(0xFF16A34A))
            } else {
                null
            }

            val isRepeating = AlarmScheduler.isRepeatingTask(task)
            val isButtonEnabled = !isCompleted || !isRepeating

            Button(
                onClick = { viewModel.toggleTaskStatus(task.id) },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .then(
                        if (buttonBorder != null) Modifier.border(buttonBorder, RoundedCornerShape(16.dp)) else Modifier
                    )
                    .shadow(
                        elevation = if (isCompleted) 2.dp else 6.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = if (isCompleted) SuccessGreen.copy(alpha = 0.3f) else PrimaryPurple.copy(alpha = 0.3f),
                        spotColor = if (isCompleted) SuccessGreen.copy(alpha = 0.3f) else PrimaryPurple.copy(alpha = 0.3f)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBg,
                    disabledContainerColor = buttonBg,
                    disabledContentColor = buttonTextColor
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (!isCompleted) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = buttonTextColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (isCompleted) "Completed ✓" else "Mark as Complete",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = buttonTextColor
                    )
                }
            }

            // Completed Banner Alert (Shown BELOW the button)
            if (isCompleted) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDarkTheme) Color(0xFF064E3B).copy(alpha = 0.35f) else Color(0xFFECFDF5),
                    border = BorderStroke(1.dp, if (isDarkTheme) SuccessGreen.copy(alpha = 0.4f) else Color(0xFFA7F3D0))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Task Completed ✓",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) SuccessGreenDark else Color(0xFF065F46)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Great job! This task has been marked as finished.",
                                fontSize = 11.sp,
                                color = if (isDarkTheme) SuccessGreenDark.copy(alpha = 0.85f) else Color(0xFF047857)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = CardSurface,
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ErrorRed.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Delete Task?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${task.title}'? This action cannot be undone.",
                    fontSize = 14.sp,
                    color = TextGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteTask(task.id)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteConfirmDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    if (showEditSheet) {
        NewTaskBottomSheet(
            existingTask = task,
            onDismiss = { showEditSheet = false },
            onSave = { updatedTask ->
                viewModel.addTask(updatedTask)
                showEditSheet = false
            }
        )
    }
}

@Composable
fun DetailRowItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(IconBoxBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryPurple,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = TextGray)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }
    }
}
