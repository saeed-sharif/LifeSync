package com.dalgeer.lifeplanner.di

import com.dalgeer.lifeplanner.data.local.AppDatabase
import com.dalgeer.lifeplanner.data.repository.GoalRepository
import com.dalgeer.lifeplanner.data.repository.RoomGoalRepository
import com.dalgeer.lifeplanner.data.repository.RoomRoutineRepository
import com.dalgeer.lifeplanner.data.repository.RoomTaskRepository
import com.dalgeer.lifeplanner.data.repository.RoutineRepository
import com.dalgeer.lifeplanner.data.repository.TaskRepository
import com.dalgeer.lifeplanner.presentation.viewmodel.GoalViewModel
import com.dalgeer.lifeplanner.presentation.viewmodel.RoutineViewModel
import com.dalgeer.lifeplanner.presentation.viewmodel.TaskViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.getInstance(androidApplication()) }
    single { get<AppDatabase>().taskDao() }
    single { get<AppDatabase>().routineDao() }
    single { get<AppDatabase>().goalDao() }
}

val repositoryModule = module {
    single<TaskRepository> { RoomTaskRepository(get()) }
    single<RoutineRepository> { RoomRoutineRepository(get(), androidApplication()) }
    single<GoalRepository> { RoomGoalRepository(get()) }
}

val viewModelModule = module {
    viewModel { TaskViewModel(androidApplication(), get<TaskRepository>()) }
    viewModel { RoutineViewModel(androidApplication(), get<RoutineRepository>()) }
    viewModel { GoalViewModel(androidApplication(), get<GoalRepository>()) }
}

val appModules = listOf(databaseModule, repositoryModule, viewModelModule)
