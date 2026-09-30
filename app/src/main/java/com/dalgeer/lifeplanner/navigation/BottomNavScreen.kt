package com.dalgeer.lifeplanner.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class BottomNavScreen : NavKey {
    @Serializable
    data object Home : BottomNavScreen()
    @Serializable
    data object Tasks : BottomNavScreen()
    @Serializable
    data object Routines : BottomNavScreen()
    @Serializable
    data object Goals : BottomNavScreen()
}
