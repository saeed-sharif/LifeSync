package com.dalgeer.lifeplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.koin.androidx.compose.koinViewModel
import com.dalgeer.lifeplanner.navigation.BottomNavGraph
import com.dalgeer.lifeplanner.navigation.NavTab
import com.dalgeer.lifeplanner.presentation.viewmodel.GoalViewModel
import com.dalgeer.lifeplanner.presentation.viewmodel.RoutineViewModel
import com.dalgeer.lifeplanner.presentation.viewmodel.TaskViewModel
import com.dalgeer.lifeplanner.ui.screens.goals.NewGoalBottomSheet
import com.dalgeer.lifeplanner.ui.screens.home.AddNewBottomSheet
import com.dalgeer.lifeplanner.ui.screens.routines.NewRoutineBottomSheet
import com.dalgeer.lifeplanner.ui.screens.tasks.NewTaskBottomSheet
import com.dalgeer.lifeplanner.ui.theme.*
import io.jadu.animatedBottomBar.CurvedBottomNavigation
import io.jadu.animatedBottomBar.models.IconSource
import io.jadu.animatedBottomBar.models.NavItem

@Composable
fun HomeScreenContent(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToTaskDetail: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(NavTab.HOME) }
    var highlightedTaskId by remember { mutableStateOf<String?>(null) }
    var showQuickAddSheet by remember { mutableStateOf(false) }
    var showNewTaskSheet by remember { mutableStateOf(false) }
    var showNewRoutineSheet by remember { mutableStateOf(false) }
    var showNewGoalSheet by remember { mutableStateOf(false) }

    val taskViewModel: TaskViewModel = koinViewModel()
    val routineViewModel: RoutineViewModel = koinViewModel()
    val goalViewModel: GoalViewModel = koinViewModel()

    val animatedNavItems = remember {
        listOf(
            NavItem(
                icon = IconSource.Vector(Icons.Outlined.Home),
                selectedIcon = IconSource.Vector(Icons.Filled.Home),
                label = "Home",
                route = "home"
            ),
            NavItem(
                icon = IconSource.Vector(Icons.AutoMirrored.Outlined.Assignment),
                selectedIcon = IconSource.Vector(Icons.AutoMirrored.Filled.Assignment),
                label = "Tasks",
                route = "tasks"
            ),
            NavItem(
                icon = IconSource.Vector(Icons.Outlined.LocalFireDepartment),
                selectedIcon = IconSource.Vector(Icons.Filled.LocalFireDepartment),
                label = "Routines",
                route = "routines"
            ),
            NavItem(
                icon = IconSource.Vector(Icons.Outlined.TrackChanges),
                selectedIcon = IconSource.Vector(Icons.Filled.TrackChanges),
                label = "Goals",
                route = "goals"
            )
        )
    }

    val selectedIndex = when (selectedTab) {
        NavTab.HOME -> 0
        NavTab.TASKS -> 1
        NavTab.ROUTINES -> 2
        NavTab.GOALS -> 3
    }

    var expandCompletedTasks by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ScreenBackground,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                CurvedBottomNavigation(
                    items = animatedNavItems,
                    selectedIndex = selectedIndex,
                    onItemSelected = { index ->
                        selectedTab = when (index) {
                            0 -> NavTab.HOME
                            1 -> NavTab.TASKS
                            2 -> NavTab.ROUTINES
                            3 -> NavTab.GOALS
                            else -> NavTab.HOME
                        }
                    },
                    onFabClick = { showQuickAddSheet = true },
                    fabBackgroundColor = PrimaryPurple,
                    fabIconTint = Color.White,
                    navBarBackgroundColor = White,
                    unselectedIconTint = TextGray,
                    unselectedTextColor = TextGray
                )
            }
        }
    ) { innerPadding ->
        com.dalgeer.lifeplanner.ui.common.NotificationAndAlarmPermissionHandler()

        BottomNavGraph(
            selectedTab = selectedTab,
            onSelectTab = { tab ->
                selectedTab = tab
                if (tab != NavTab.TASKS) expandCompletedTasks = false
            },
            onNavigateToProfile = onNavigateToProfile,
            onNavigateToTask = { taskId ->
                onNavigateToTaskDetail(taskId)
            },
            onNavigateToCompletedTasks = {
                expandCompletedTasks = true
                selectedTab = NavTab.TASKS
            },
            expandCompletedTasks = expandCompletedTasks,
            highlightedTaskId = highlightedTaskId,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        )
        
        if (showQuickAddSheet) {
            AddNewBottomSheet(
                onDismiss = { showQuickAddSheet = false },
                onOptionSelect = { option ->
                    showQuickAddSheet = false
                    when (option) {
                        "Task" -> showNewTaskSheet = true
                        "Routine" -> showNewRoutineSheet = true
                        "Goal" -> showNewGoalSheet = true
                    }
                }
            )
        }

        if (showNewTaskSheet) {
            NewTaskBottomSheet(
                onDismiss = { showNewTaskSheet = false },
                onSave = { task ->
                    taskViewModel.addTask(task)
                    showNewTaskSheet = false
                }
            )
        }

        if (showNewRoutineSheet) {
            NewRoutineBottomSheet(
                onDismiss = { showNewRoutineSheet = false },
                onSave = { routine ->
                    routineViewModel.addRoutine(routine)
                    showNewRoutineSheet = false
                }
            )
        }

        if (showNewGoalSheet) {
            NewGoalBottomSheet(
                onDismiss = { showNewGoalSheet = false },
                onSave = { goal ->
                    goalViewModel.addGoal(goal)
                    showNewGoalSheet = false
                }
            )
        }
    }
}
