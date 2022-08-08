package com.ygaberman.babypoo.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertActivities(vararg activities: Activity)

    @Query("DELETE FROM activity")
    suspend fun deleteAll()

    @Query("SELECT * FROM activity")
    fun loadActivities(): Flow<List<Activity>>
}
