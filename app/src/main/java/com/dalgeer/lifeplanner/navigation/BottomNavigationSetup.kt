package com.dalgeer.lifeplanner.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.dalgeer.lifeplanner.ui.screens.home.HomeDashboard
import com.dalgeer.lifeplanner.ui.screens.tasks.TasksScreen
import com.dalgeer.lifeplanner.ui.screens.routines.RoutinesScreen
import com.dalgeer.lifeplanner.ui.screens.goals.GoalsScreen

enum class NavTab {
    HOME,
    TASKS,
    ROUTINES,
    GOALS
}

private fun NavTab.toScreen(): BottomNavScreen = when (this) {
    NavTab.HOME -> BottomNavScreen.Home
    NavTab.TASKS -> BottomNavScreen.Tasks
    NavTab.ROUTINES -> BottomNavScreen.Routines
    NavTab.GOALS -> BottomNavScreen.Goals
}

@Composable
fun BottomNavGraph(
    selectedTab: NavTab,
    onSelectTab: (NavTab) -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToTask: (String) -> Unit = {},
    onNavigateToCompletedTasks: () -> Unit = {},
    expandCompletedTasks: Boolean = false,
    highlightedTaskId: String? = null,
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(selectedTab.toScreen())

    LaunchedEffect(selectedTab) {
        if (backStack.lastOrNull() != selectedTab.toScreen()) {
            backStack.clear()
            backStack.add(selectedTab.toScreen())
        }
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            } else {
                onSelectTab(NavTab.HOME)
            }
        },
        entryProvider = entryProvider {
            entry<BottomNavScreen.Home> {
                HomeDashboard(
                    onNavigateToProfile = onNavigateToProfile,
                    onNavigateToTask = onNavigateToTask,
                    onNavigateToCompletedTasks = onNavigateToCompletedTasks
                )
            }
            entry<BottomNavScreen.Tasks> {
                TasksScreen(
                    highlightedTaskId = highlightedTaskId,
                    expandCompleted = expandCompletedTasks,
                    initialFilterChip = if (expandCompletedTasks) "Completed" else "Today"
                )
            }
            entry<BottomNavScreen.Routines> {
                RoutinesScreen()
            }
            entry<BottomNavScreen.Goals> {
                GoalsScreen()
            }
        }
    )
}
