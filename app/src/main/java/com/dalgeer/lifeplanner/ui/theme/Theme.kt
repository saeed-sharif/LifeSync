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
    primary = PrimaryPurpleDark,             // 0xFF8B7CFF
    primaryContainer = PrimaryContainerDark,  // 0xFF292344
    secondary = AccentBlueDark,              // 0xFF42A5F5
    tertiary = SuccessGreenDark,             // 0xFF00E676
    background = ScreenBackgroundDark,       // 0xFF0D0F18
    surface = SurfaceDark,                   // 0xFF191C29
    surfaceVariant = SurfaceVariantDark,     // 0xFF222638
    onBackground = TextDarkTheme,            // 0xFFF5F5FA
    onSurface = TextDarkTheme,               // 0xFFF5F5FA
    onSurfaceVariant = TextGrayDarkTheme,    // 0xFFA6ADBD
    outlineVariant = DividerColorDark,       // 0xFF2A3040
    error = ErrorRedDark,
    onPrimary = Color.White,
    onSecondary = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurpleLight,            // 0xFF6C63FF
    primaryContainer = LightPurpleLight,     // 0xFFF0EFFF
    secondary = AccentBlueLight,             // 0xFF3F51B5
    tertiary = SuccessGreenLight,            // 0xFF00B894)
    background = ScreenBackgroundLight,      // 0xFFF8F9FF
    surface = WhiteLight,                    // 0xFFFFFFFF
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = TextDarkLight,            // 0xFF1A1C1E
    onSurface = TextDarkLight,               // 0xFF1A1C1E
    onSurfaceVariant = TextGrayLight,        // 0xFF74777F
    outlineVariant = DividerColorLight,      // 0xFFE1E2EC
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
