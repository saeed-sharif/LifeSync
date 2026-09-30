package com.dalgeer.lifeplanner.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class GoalType {
    Personal, Career, Fitness, Financial
}

@Serializable
data class GoalModel(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val type: GoalType = GoalType.Personal,
    val targetDate: String = "Sep 23, 2025",
    val targetMetricValue: String = "100",
    val currentMetricValue: String = "0",
    val notes: String = "",
    val isCompleted: Boolean = false
)

fun extractFloatValue(text: String): Float? {
    val sanitized = text.replace(",", "")
    val match = Regex("""\d+(\.\d+)?""").find(sanitized)
    return match?.value?.toFloatOrNull()
}

fun GoalModel.getTargetFloat(): Float = extractFloatValue(targetMetricValue) ?: 100f
fun GoalModel.getCurrentFloat(): Float = extractFloatValue(currentMetricValue) ?: 0f
fun GoalModel.calculateProgressRatio(): Float {
    if (isCompleted) return 1f
    val target = getTargetFloat()
    val current = getCurrentFloat()
    return if (target > 0f) (current / target).coerceIn(0f, 1f) else 0f
}