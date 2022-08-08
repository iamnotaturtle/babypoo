package com.ygaberman.babypoo.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ActivityRepository(private val activityDao: ActivityDao) {
    val activities: Flow<List<Activity>> = activityDao.loadActivities()

    @WorkerThread
    suspend fun insert(activity: Activity) {
        activityDao.insertActivities(activity)
    }
}