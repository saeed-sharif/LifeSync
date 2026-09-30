package com.dalgeer.lifeplanner.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {
    @Serializable
    data object SplashScreen : Screen()
    @Serializable
    data object LanguageScreen : Screen()
    @Serializable
    data object OnboardingScreen : Screen()
    @Serializable
    data class ProfileSetupScreen(val isEditMode: Boolean = false) : Screen()
    @Serializable
    data object HomeScreenContent : Screen()
    @Serializable
    data object SettingsScreen : Screen()
    @Serializable
    data object PrivacyPolicyScreen : Screen()
    @Serializable
    data class TaskDetailScreen(val taskId: String) : Screen()
}
