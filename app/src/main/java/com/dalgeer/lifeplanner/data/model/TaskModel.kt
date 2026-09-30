package com.dalgeer.lifeplanner.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
enum class TaskPriority {
    High, Medium, Low
}

@Serializable
enum class TaskCategory {
    Utilities, Work, Personal, Health, Shopping, Finance, General
}

@Serializable
enum class TaskStatus {
    Pending, Completed
}

@Serializable
data class TaskModel(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val category: TaskCategory = TaskCategory.General,
    val priority: TaskPriority = TaskPriority.Medium,
    val date: String = "Today",
    val time: String = "08:30 AM",
    val notes: String = "",
    val repeatOption: String = "Does not repeat",
    val status: TaskStatus = TaskStatus.Pending
)

fun TaskModel.isOverdue(): Boolean {
    if (status == TaskStatus.Completed) return false
    val timeInMillis = com.dalgeer.lifeplanner.util.AlarmScheduler.parseDateTimeToMillis(date, time) ?: return false
    return timeInMillis < System.currentTimeMillis()
}

fun TaskCategory.getCategoryIcon(): ImageVector = when (this) {
    TaskCategory.Utilities -> Icons.Default.FlashOn
    TaskCategory.Work -> Icons.Outlined.Work
    TaskCategory.Personal -> Icons.Outlined.Person
    TaskCategory.Health -> Icons.Outlined.Favorite
    TaskCategory.Shopping -> Icons.Outlined.ShoppingCart
    TaskCategory.Finance -> Icons.Outlined.AccountBalanceWallet
    TaskCategory.General -> Icons.Outlined.Assignment
}

@Composable
fun TaskCategory.getCategoryColor(): Color {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    return when (this) {
        TaskCategory.Utilities -> if (isDark) Color(0xFF2D2615) else Color(0xFFFFF3E0)
        TaskCategory.Work -> if (isDark) Color(0xFF1B283B) else Color(0xFFE8EAF6)
        TaskCategory.Personal -> if (isDark) Color(0xFF2E1A2B) else Color(0xFFFCE4EC)
        TaskCategory.Health -> if (isDark) Color(0xFF162B28) else Color(0xFFE0F2F1)
        TaskCategory.Shopping -> if (isDark) Color(0xFF2E2420) else Color(0xFFEFEBE9)
        TaskCategory.Finance -> if (isDark) Color(0xFF1B2E20) else Color(0xFFE8F5E9)
        TaskCategory.General -> if (isDark) Color(0xFF222738) else Color(0xFFF5F7FA)
    }
}

@Composable
fun TaskCategory.getCategoryTint(): Color {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    return when (this) {
        TaskCategory.Utilities -> if (isDark) Color(0xFFFFB300) else Color(0xFFF57C00)
        TaskCategory.Work -> if (isDark) Color(0xFF42A5F5) else Color(0xFF3F51B5)
        TaskCategory.Personal -> if (isDark) Color(0xFFF48FB1) else Color(0xFFC2185B)
        TaskCategory.Health -> if (isDark) Color(0xFF00E676) else Color(0xFF00796B)
        TaskCategory.Shopping -> if (isDark) Color(0xFFA1887F) else Color(0xFF5D4037)
        TaskCategory.Finance -> if (isDark) Color(0xFF66BB6A) else Color(0xFF388E3C)
        TaskCategory.General -> if (isDark) Color(0xFF90A4AE) else Color(0xFF78909C)
    }
}
