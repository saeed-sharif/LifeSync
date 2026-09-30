package com.dalgeer.lifeplanner.ui.screens.home

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

// 1. Home Quick Add (Task, Routine, Goal)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewBottomSheet(
    onDismiss: () -> Unit,
    onOptionSelect: (String) -> Unit = {}
) {
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Quick Add",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "What would you like to add?",
                        fontSize = 13.sp,
                        color = TextGray
                    )
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

            Spacer(modifier = Modifier.height(20.dp))

            val isDark = androidx.compose.foundation.isSystemInDarkTheme()
            val options = listOf(
                QuickAddOption(
                    type = "Task",
                    title = "Add Task",
                    subtitle = "Create a new task, shopping item or reminder.",
                    icon = Icons.AutoMirrored.Outlined.Assignment,
                    cardBgColor = if (isDark) Color(0xFF241B3B) else Color(0xFFF3F0FF),
                    iconBgColor = if (isDark) Color(0xFF332A52) else Color(0xFFE2DDFF),
                    iconColor = if (isDark) Color(0xFF8B84FF) else Color(0xFF6C5CE7)
                ),
                QuickAddOption(
                    type = "Routine",
                    title = "Add Routine",
                    subtitle = "Build a healthy habit and track your streak.",
                    icon = Icons.Outlined.Timer,
                    cardBgColor = if (isDark) Color(0xFF162B28) else Color(0xFFEAF7F1),
                    iconBgColor = if (isDark) Color(0xFF1F3D37) else Color(0xFFCBECE0),
                    iconColor = if (isDark) Color(0xFF00E676) else Color(0xFF00A878)
                ),
                QuickAddOption(
                    type = "Goal",
                    title = "Add Goal",
                    subtitle = "Set a goal and break it into milestones.",
                    icon = Icons.Outlined.TrackChanges,
                    cardBgColor = if (isDark) Color(0xFF2E1F1A) else Color(0xFFFFF3EA),
                    iconBgColor = if (isDark) Color(0xFF422C23) else Color(0xFFFFE2CE),
                    iconColor = if (isDark) Color(0xFFFFB300) else Color(0xFFFF6B00)
                )
            )

            options.forEach { option ->
                QuickAddOptionItem(option = option, onClick = { onOptionSelect(option.type) })
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
