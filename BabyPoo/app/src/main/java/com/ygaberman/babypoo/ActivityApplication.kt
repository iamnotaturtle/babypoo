package com.ygaberman.babypoo

import android.app.Application
import com.ygaberman.babypoo.db.ActivityRepository
import com.ygaberman.babypoo.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ActivityApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { ActivityRepository(database.activityDao()) }
}
