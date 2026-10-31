package com.dalgeer.lifeplanner.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dalgeer.lifeplanner.ui.common.QuickAddOption
import com.dalgeer.lifeplanner.ui.common.QuickAddOptionItem
import com.dalgeer.lifeplanner.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewBottomSheet(
    onDismiss: () -> Unit,
    onOptionSelect: (String) -> Unit = {}
) {
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
                        tint = TextGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val options = listOf(
                QuickAddOption(
                    type = "Task",
                    title = "Add Task",
                    subtitle = "Create a new task, shopping item or reminder.",
                    icon = Icons.AutoMirrored.Outlined.Assignment,
                    cardBgColor = if (isDarkTheme) SurfaceVariantDark else Color(0xFFF3F0FF),
                    iconBgColor = if (isDarkTheme) PrimaryContainerDark else Color(0xFFE2DDFF),
                    iconColor = if (isDarkTheme) PrimaryPurpleDark else Color(0xFF6C5CE7)
                ),
                QuickAddOption(
                    type = "Routine",
                    title = "Add Routine",
                    subtitle = "Build a healthy habit and track your streak.",
                    icon = Icons.Outlined.Timer,
                    cardBgColor = if (isDarkTheme) SurfaceVariantDark else Color(0xFFEAF7F1),
                    iconBgColor = if (isDarkTheme) LightGreenDark else Color(0xFFCBECE0),
                    iconColor = if (isDarkTheme) SuccessGreenDark else Color(0xFF00A878)
                ),
                QuickAddOption(
                    type = "Goal",
                    title = "Add Goal",
                    subtitle = "Set a goal and break it into milestones.",
                    icon = Icons.Outlined.TrackChanges,
                    cardBgColor = if (isDarkTheme) SurfaceVariantDark else Color(0xFFFFF3EA),
                    iconBgColor = if (isDarkTheme) Color(0xFF3D2A12) else Color(0xFFFFE2CE),
                    iconColor = if (isDarkTheme) Color(0xFFFFB74D) else Color(0xFFFF6B00)
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
