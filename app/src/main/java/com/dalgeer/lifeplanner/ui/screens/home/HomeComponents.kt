package com.dalgeer.lifeplanner.ui.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dalgeer.lifeplanner.data.model.*
import com.dalgeer.lifeplanner.ui.theme.*

@Composable
fun TopProgressCard(
    tasks: List<TaskModel>,
    onCardClick: () -> Unit = {},
    onCompletedClick: () -> Unit = {}
) {
    val completedCount = tasks.count { it.status == TaskStatus.Completed }
    val overdueCount = tasks.count { it.isOverdue() }
    val pendingCount = tasks.count { it.status == TaskStatus.Pending && !it.isOverdue() }
    val totalTasks = tasks.size
    val progress = if (totalTasks > 0) completedCount.toFloat() / totalTasks else 0f
    val percent = (progress * 100).toInt()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = CardElevation,
                shape = RoundedCornerShape(CornerRadiusLarge),
                ambientColor = PrimaryPurple.copy(alpha = 0.15f),
                spotColor = PrimaryPurple.copy(alpha = 0.15f)
            )
            .clickable { onCardClick() },
        shape = RoundedCornerShape(CornerRadiusLarge),
        color = White
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Compact Circular Progress Gauge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(62.dp)
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = PrimaryPurple,
                    trackColor = Color(0xFFECEFF1),
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$percent%",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Today",
                        fontSize = 9.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Center details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Progress",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "View Details",
                        tint = TextGray,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "$completedCount of $totalTasks tasks completed",
                    fontSize = 11.sp,
                    color = TextGray,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Done, Pending, Overdue Status Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Done Badge (Clickable to navigate to completed tasks)
                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { onCompletedClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = White,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$completedCount Done",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46),
                                maxLines = 1
                            )
                        }
                    }

                    // Pending Badge
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF59E0B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Schedule,
                                    contentDescription = null,
                                    tint = White,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$pendingCount Pending",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF92400E),
                                maxLines = 1
                            )
                        }
                    }

                    // Overdue Badge
                    Surface(
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .clip(CircleShape)
                                    .background(ErrorRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "!",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            }
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$overdueCount Overdue",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = ErrorRed,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OverdueTaskAlertCard(
    overdueTasks: List<TaskModel>,
    onViewClick: () -> Unit
) {
    if (overdueTasks.isEmpty()) return
    val latestTask = overdueTasks.first()
    val count = overdueTasks.size

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewClick() },
        shape = RoundedCornerShape(CornerRadiusLarge),
        color = Color(0xFFFFF0F0),
        border = BorderStroke(1.dp, Color(0xFFFFD0D0))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD6D6)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ErrorRed
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$count Overdue Task${if (count > 1) "s" else ""}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ErrorRed
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = latestTask.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${latestTask.category.name} • ${latestTask.date} • ${latestTask.time}",
                    fontSize = 11.sp,
                    color = TextGray,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                color = White,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFFFFC0C0))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun UpNextTaskCardList(
    tasks: List<TaskModel>,
    onToggleTask: (String) -> Unit,
    onTaskClick: ((String) -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = CardElevation,
                shape = RoundedCornerShape(CornerRadiusLarge),
                ambientColor = PrimaryPurple.copy(alpha = 0.15f),
                spotColor = PrimaryPurple.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(CornerRadiusLarge),
        color = White
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (tasks.isEmpty()) {
                Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                    Text("No upcoming tasks. Tap + to create one!", fontSize = 13.sp, color = TextGray)
                }
            } else {
                tasks.forEachIndexed { index, task ->
                    val isHigh = task.priority == TaskPriority.High
                    val isCompleted = task.status == TaskStatus.Completed

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTaskClick?.invoke(task.id) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isCompleted,
                            onCheckedChange = { onToggleTask(task.id) },
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Category Icon & Title
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCompleted) TextGray else TextDark,
                                textDecoration = if (isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = task.category.getCategoryIcon(),
                                    contentDescription = null,
                                    tint = task.category.getCategoryTint(),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = task.category.name,
                                    fontSize = 11.sp,
                                    color = TextGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Time, Priority, and Repeat Chips
                        Column(horizontalAlignment = Alignment.End) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Time Chip Badge (Purple Background with Bell Icon)
                                Surface(
                                    color = Color(0xFFF0EFFF),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Notifications,
                                            contentDescription = null,
                                            tint = PrimaryPurple,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = task.time,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryPurple
                                        )
                                    }
                                }

                                // Priority Chip
                                Surface(
                                    color = if (isHigh) LightRed else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = task.priority.name.uppercase(),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = if (isHigh) ErrorRed else TextGray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (task.repeatOption != "Does not repeat") {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Autorenew,
                                        contentDescription = null,
                                        tint = TextGray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = task.repeatOption.replace("Every ", ""),
                                        fontSize = 10.sp,
                                        color = TextGray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "View Task",
                            tint = TextGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (index < tasks.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), color = DividerColor)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeRoutineCard(routine: RoutineModel) {
    val iconBg = routine.timeOfDay.getRoutineColor()
    val iconTint = routine.timeOfDay.getRoutineTint()
    val iconVector = routine.timeOfDay.getRoutineIcon()

    val badgeBg = when (routine.timeOfDay) {
        RoutineTimeOfDay.Morning -> Color(0xFFFFF3E0)
        else -> Color(0xFFE8EAF6)
    }
    val badgeTextColor = when (routine.timeOfDay) {
        RoutineTimeOfDay.Morning -> Color(0xFFF57C00)
        else -> Color(0xFF3F51B5)
    }

    Surface(
        modifier = Modifier
            .width(135.dp)
            .shadow(
                elevation = CardElevation,
                shape = RoundedCornerShape(CornerRadiusLarge)
            ),
        shape = RoundedCornerShape(CornerRadiusLarge),
        color = White
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = routine.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (routine.isCompletedToday) "Completed" else "In Progress",
                fontSize = 10.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { if (routine.isCompletedToday) 1f else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                color = iconTint,
                trackColor = Color(0xFFF0F2F8)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = badgeBg,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "🔥 ${routine.streak} day streak",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    color = badgeTextColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
