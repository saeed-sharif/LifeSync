package com.dalgeer.lifeplanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Brand Colors (Light Theme)
val PrimaryPurpleLight = Color(0xFF6C63FF)
val LightPurpleLight = Color(0xFFF0EFFF)
val SuccessGreenLight = Color(0xFF00B894)
val LightGreenLight = Color(0xFFEFFFFB)
val ErrorRedLight = Color(0xFFFF7675)
val LightRedLight = Color(0xFFFFECEC)
val AccentBlueLight = Color(0xFF3F51B5)

// Brand Colors (Dark Theme)
val PrimaryPurpleDark = Color(0xFF8B7CFF)
val PrimaryContainerDark = Color(0xFF292344)
val LightPurpleDark = Color(0xFF241B3B)
val SuccessGreenDark = Color(0xFF00E676)
val LightGreenDark = Color(0xFF122C28)
val ErrorRedDark = Color(0xFFFF5252)
val LightRedDark = Color(0xFF3B1E28)
val AccentBlueDark = Color(0xFF42A5F5)

// Text and UI (Light Theme)
val TextDarkLight = Color(0xFF1A1C1E)
val TextGrayLight = Color(0xFF74777F)
val ScreenBackgroundLight = Color(0xFFF8F9FF)
val WhiteLight = Color(0xFFFFFFFF)
val DividerColorLight = Color(0xFFE1E2EC)

// Text and UI (Dark Theme)
val TextDarkTheme = Color(0xFFF5F5FA)
val TextGrayDarkTheme = Color(0xFFA6ADBD)
val ScreenBackgroundDark = Color(0xFF0D0F18)
val SurfaceDark = Color(0xFF191C29)
val SurfaceVariantDark = Color(0xFF222638)
val WhiteDark = Color(0xFFFFFFFF)
val DividerColorDark = Color(0xFF2A3040)

// Dynamic Composable Adaptive Color Helper
val isDarkTheme: Boolean
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background == ScreenBackgroundDark

// Dynamic Adaptive Colors
val PrimaryPurple: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primary

val LightPurple: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primaryContainer

val SuccessGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.tertiary

val LightGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isDarkTheme) LightGreenDark else LightGreenLight

val ErrorRed: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.error

val LightRed: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isDarkTheme) LightRedDark else LightRedLight

val TextDark: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onBackground

val TextGray: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val ScreenBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background

val CardSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

val White: Color
    @Composable
    @ReadOnlyComposable
    get() = Color.White

val DividerColor: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outlineVariant

val SurfaceVariantColor: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceVariant

val InputFieldBg: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isDarkTheme) SurfaceVariantDark else Color(0xFFF8F9FE)

val InputFieldBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isDarkTheme) DividerColorDark else Color(0xFFECEFF8)

val IconBoxBg: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isDarkTheme) PrimaryContainerDark else Color(0xFFF0EFFF)
