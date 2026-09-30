package com.dalgeer.lifeplanner.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dalgeer.lifeplanner.data.local.AppDatabase
import com.dalgeer.lifeplanner.data.local.entities.RoutineEntity
import kotlinx.coroutines.flow.first

class DailyResetWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getInstance(applicationContext)
            com.dalgeer.lifeplanner.data.repository.RoomRoutineRepository.checkAndResetDailyRoutines(
                applicationContext,
                db.routineDao()
            )
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "DailyResetWorker"
    }
}
