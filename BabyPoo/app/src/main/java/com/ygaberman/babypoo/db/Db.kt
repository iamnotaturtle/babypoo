package com.ygaberman.babypoo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Activity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao

    // Callback to populate the db manually
    private class ActivityDbCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                {
                    scope.launch {
                        populateDatabase(database.activityDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(activityDao: ActivityDao) {
            activityDao.deleteAll()
            activityDao.insertActivities(Activity(type = "poop", notes = "first one!"))
        }
    }

    companion object {
        // DB Singleton

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                )
                    .addCallback(ActivityDbCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
