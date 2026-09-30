package com.dalgeer.lifeplanner.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurpleDark,
    secondary = AccentBlueDark,
    tertiary = SuccessGreenDark,
    background = ScreenBackgroundDark,    // 0xFF0D0F18
    surface = SurfaceDark,                // 0xFF141722
    surfaceVariant = Color(0xFF1A1F2E),   // 0xFF1A1F2E
    onBackground = TextDarkTheme,         // 0xFFFFFFFF
    onSurface = TextDarkTheme,            // 0xFFFFFFFF
    onSurfaceVariant = TextGrayDarkTheme, // 0xFF8E95A5
    outlineVariant = DividerColorDark,    // 0xFF222738
    error = ErrorRedDark,
    onPrimary = Color.White,
    onSecondary = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurpleLight,
    secondary = AccentBlueLight,
    tertiary = SuccessGreenLight,
    background = ScreenBackgroundLight,
    surface = WhiteLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = TextDarkLight,
    onSurface = TextDarkLight,
    onSurfaceVariant = TextGrayLight,
    outlineVariant = DividerColorLight,
    error = ErrorRedLight,
    onPrimary = Color.White,
    onSecondary = Color.White
)

@Composable
fun LifePlannerTheme(
    themeMode: String = "System",
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // When darkTheme is true, status bar icons should be light/white (isAppearanceLightStatusBars = false)
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
