package com.dalgeer.lifeplanner

import android.app.Application
import androidx.work.*
import com.dalgeer.lifeplanner.di.appModules
import com.dalgeer.lifeplanner.util.NotificationHelper
import com.dalgeer.lifeplanner.worker.DailyResetWorker
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.Calendar
import java.util.concurrent.TimeUnit

class LifePlannerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin Dependency Injection
        startKoin {
            androidContext(this@LifePlannerApplication)
            modules(appModules)
        }

        // Initialize Notification Channels
        NotificationHelper.createNotificationChannels(this)
        
        // Schedule Daily Habit Reset WorkManager at Midnight
        scheduleDailyResetWork()
    }

    private fun scheduleDailyResetWork() {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(currentDate)) {
                add(Calendar.HOUR_OF_DAY, 24)
            }
        }

        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

        val dailyResetRequest = PeriodicWorkRequest.Builder(
            DailyResetWorker::class.java,
            1,
            TimeUnit.DAYS
        )
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DailyResetWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyResetRequest
        )
    }
}
