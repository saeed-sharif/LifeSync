package com.dalgeer.lifeplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalgeer.lifeplanner.data.preferences.AppPreferences
import com.dalgeer.lifeplanner.navigation.AppNavigation
import com.dalgeer.lifeplanner.ui.theme.LifePlannerTheme
import com.dalgeer.lifeplanner.util.InAppUpdateManager

class MainActivity : ComponentActivity() {

    private lateinit var inAppUpdateManager: InAppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        inAppUpdateManager = InAppUpdateManager(this)
        inAppUpdateManager.checkForUpdates(this, isUserInitiated = false)

        setContent {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                AppPreferences.getThemeMode(context)
            }
            val themeMode by AppPreferences.themeModeState.collectAsStateWithLifecycle()
            LifePlannerTheme(themeMode = themeMode) {
                AppNavigation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::inAppUpdateManager.isInitialized) {
            inAppUpdateManager.checkResumeUpdate(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::inAppUpdateManager.isInitialized) {
            inAppUpdateManager.unregisterListener()
        }
    }
}
