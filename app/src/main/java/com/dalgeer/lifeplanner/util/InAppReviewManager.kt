package com.dalgeer.lifeplanner.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.android.play.core.review.ReviewManagerFactory

object InAppReviewManager {

    /**
     * Prompts the Google Play In-App Review dialog.
     * Fallbacks to opening Google Play Store page if in-app review is unavailable.
     */
    fun launchInAppReview(activity: Activity) {
        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener {
                    Toast.makeText(activity, "Thank you for rating LifeSync! ⭐", Toast.LENGTH_SHORT).show()
                }
            } else {
                openPlayStorePage(activity)
            }
        }
    }

    /**
     * Fallback method to open Google Play Store app detail page.
     */
    fun openPlayStorePage(activity: Activity) {
        val packageName = activity.packageName
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            activity.startActivity(intent)
        } catch (e: Exception) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            activity.startActivity(webIntent)
        }
    }
}
