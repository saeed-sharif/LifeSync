package com.dalgeer.lifeplanner.util

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class InAppUpdateManager(private val context: Context) {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null

    companion object {
        const val UPDATE_REQUEST_CODE = 5001
    }

    /**
     * Checks for app updates. If an update is available, triggers the update flow.
     * @param activity Current Activity reference needed to launch the update UI.
     * @param isUserInitiated Whether the check was triggered manually by user tap (shows toast if up to date).
     * @param updateType AppUpdateType.FLEXIBLE or AppUpdateType.IMMEDIATE
     */
    fun checkForUpdates(
        activity: Activity,
        isUserInitiated: Boolean = false,
        updateType: Int = AppUpdateType.FLEXIBLE
    ) {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(updateType)
            ) {
                if (updateType == AppUpdateType.FLEXIBLE) {
                    registerFlexibleUpdateListener(activity)
                }
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activity,
                        AppUpdateOptions.newBuilder(updateType).build(),
                        UPDATE_REQUEST_CODE
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (isUserInitiated) {
                        Toast.makeText(context, "Could not start update flow: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                // Resume immediate update in progress
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activity,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                        UPDATE_REQUEST_CODE
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                if (isUserInitiated) {
                    Toast.makeText(context, "LifeSync is up to date! 🎉", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { e ->
            if (isUserInitiated) {
                Toast.makeText(context, "LifeSync is up to date! 🎉", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerFlexibleUpdateListener(activity: Activity) {
        if (installStateUpdatedListener != null) return

        installStateUpdatedListener = InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                Toast.makeText(activity, "Update downloaded! Restarting app...", Toast.LENGTH_LONG).show()
                appUpdateManager.completeUpdate()
            }
        }

        installStateUpdatedListener?.let {
            appUpdateManager.registerListener(it)
        }
    }

    fun unregisterListener() {
        installStateUpdatedListener?.let {
            appUpdateManager.unregisterListener(it)
            installStateUpdatedListener = null
        }
    }

    fun checkResumeUpdate(activity: Activity) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activity,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                        UPDATE_REQUEST_CODE
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager.completeUpdate()
            }
        }
    }
}
