package com.dalgeer.lifeplanner.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.dalgeer.lifeplanner.data.preferences.AppPreferences
import com.dalgeer.lifeplanner.ui.screens.LanguageScreen
import com.dalgeer.lifeplanner.ui.screens.HomeScreenContent
import com.dalgeer.lifeplanner.ui.screens.OnboardingScreen
import com.dalgeer.lifeplanner.ui.screens.SplashScreen
import com.dalgeer.lifeplanner.ui.screens.profile.ProfileSetupScreen
import com.dalgeer.lifeplanner.ui.screens.settings.PrivacyPolicyScreen
import com.dalgeer.lifeplanner.ui.screens.settings.SettingsScreen

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val isCompleted = remember { AppPreferences.isOnboardingCompleted(context) }
    val initialScreen = if (isCompleted) Screen.HomeScreenContent else Screen.SplashScreen

    val rootBackStack = rememberNavBackStack(initialScreen)

    NavDisplay(
        backStack = rootBackStack,
        onBack = {
            rootBackStack.removeLastOrNull()
        },
        entryProvider = entryProvider {
            entry<Screen.SplashScreen> {
                SplashScreen(
                    onNext = {
                        rootBackStack.removeLastOrNull()
                        rootBackStack.add(Screen.LanguageScreen)
                    }
                )
            }

            entry<Screen.LanguageScreen> {
                LanguageScreen(
                    onNext = {
                        rootBackStack.add(Screen.OnboardingScreen)
                    }
                )
            }

            entry<Screen.OnboardingScreen> {
                OnboardingScreen(
                    onFinish = {
                        rootBackStack.add(Screen.ProfileSetupScreen(isEditMode = false))
                    }
                )
            }

            entry<Screen.ProfileSetupScreen> { screenKey ->
                ProfileSetupScreen(
                    isEditMode = screenKey.isEditMode,
                    onComplete = {
                        if (!screenKey.isEditMode) {
                            AppPreferences.setOnboardingCompleted(context)
                            rootBackStack.clear()
                            rootBackStack.add(Screen.HomeScreenContent)
                        } else {
                            rootBackStack.removeLastOrNull()
                        }
                    }
                )
            }

            entry<Screen.HomeScreenContent> {
                HomeScreenContent(
                    onNavigateToProfile = {
                        rootBackStack.add(Screen.SettingsScreen)
                    },
                    onNavigateToTaskDetail = { taskId ->
                        rootBackStack.add(Screen.TaskDetailScreen(taskId))
                    }
                )
            }

            entry<Screen.TaskDetailScreen> { screenKey ->
                com.dalgeer.lifeplanner.ui.screens.tasks.TaskDetailScreen(
                    taskId = screenKey.taskId,
                    onBack = {
                        rootBackStack.removeLastOrNull()
                    }
                )
            }

            entry<Screen.SettingsScreen> {
                SettingsScreen(
                    onBack = {
                        rootBackStack.removeLastOrNull()
                    },
                    onNavigateToEditProfile = {
                        rootBackStack.add(Screen.ProfileSetupScreen(isEditMode = true))
                    },
                    onNavigateToLanguage = {
                        rootBackStack.add(Screen.LanguageScreen)
                    },
                    onNavigateToPrivacyPolicy = {
                        rootBackStack.add(Screen.PrivacyPolicyScreen)
                    }
                )
            }

            entry<Screen.PrivacyPolicyScreen> {
                PrivacyPolicyScreen(
                    onBack = {
                        rootBackStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}
