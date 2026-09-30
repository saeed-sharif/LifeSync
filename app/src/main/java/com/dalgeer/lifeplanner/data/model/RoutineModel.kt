package com.dalgeer.lifeplanner.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
enum class RoutineFrequency {
    Daily, Weekly, Weekdays, Weekends, Custom
}

@Serializable
enum class RoutineTimeOfDay(val label: String) {
    Morning("Morning"),
    Evening("Evening"),
    Night("Night"),
    Custom("Custom");

    val ruleText: String
        get() = when (this) {
            Morning -> "Allowed: 04:00 AM – 11:59 AM"
            Evening -> "Allowed: 12:00 PM – 07:59 PM"
            Night   -> "Allowed: 08:00 PM – 03:59 AM"
            Custom  -> "Select any time"
        }

    val errorText: String
        get() = when (this) {
            Morning -> "Morning time must be AM (4-11)"
            Evening -> "Evening time must be PM (12-7)"
            Night   -> "Night time must be 8 PM - 3 AM"
            Custom  -> ""
        }

    val defaultTime: String
        get() = when (this) {
            Morning -> "08:00 AM"
            Evening -> "06:00 PM"
            Night   -> "09:00 PM"
            Custom  -> "12:00 PM"
        }

    fun isValidTime(hour: Int, isAm: Boolean): Boolean = when (this) {
        Morning -> isAm && hour in 4..11
        Evening -> !isAm && (hour in 1..7 || hour == 12)
        Night   -> (!isAm && (hour in 8..11 || hour == 12)) || (isAm && hour in 1..3)
        Custom  -> true
    }
}

@Serializable
data class RoutineModel(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val timeOfDay: RoutineTimeOfDay = RoutineTimeOfDay.Morning,
    val frequency: RoutineFrequency = RoutineFrequency.Daily,
    val customDays: String = "",
    val time: String = "08:00 AM",
    val reminder: Boolean = true,
    val notes: String = "",
    val streak: Int = 0,
    val isCompletedToday: Boolean = false
)

fun RoutineTimeOfDay.getRoutineIcon(): ImageVector = when (this) {
    RoutineTimeOfDay.Morning -> Icons.Outlined.WbSunny
    RoutineTimeOfDay.Evening -> Icons.Outlined.Nightlight
    RoutineTimeOfDay.Night -> Icons.Outlined.Bedtime
    RoutineTimeOfDay.Custom -> Icons.Outlined.Star
}

@Composable
fun RoutineTimeOfDay.getRoutineColor(): Color {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    return when (this) {
        RoutineTimeOfDay.Morning -> if (isDark) Color(0xFF162B2F) else Color(0xFFE0F2F1)
        RoutineTimeOfDay.Evening -> if (isDark) Color(0xFF1D223B) else Color(0xFFE8EAF6)
        RoutineTimeOfDay.Night -> if (isDark) Color(0xFF281B38) else Color(0xFFF3E5F5)
        RoutineTimeOfDay.Custom -> if (isDark) Color(0xFF222738) else Color(0xFFECEFF1)
    }
}

@Composable
fun RoutineTimeOfDay.getRoutineTint(): Color {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    return when (this) {
        RoutineTimeOfDay.Morning -> if (isDark) Color(0xFF26A69A) else Color(0xFF00796B)
        RoutineTimeOfDay.Evening -> if (isDark) Color(0xFF7986CB) else Color(0xFF3F51B5)
        RoutineTimeOfDay.Night -> if (isDark) Color(0xFFAB47BC) else Color(0xFF7B1FA2)
        RoutineTimeOfDay.Custom -> if (isDark) Color(0xFF90A4AE) else Color(0xFF455A64)
    }
}
