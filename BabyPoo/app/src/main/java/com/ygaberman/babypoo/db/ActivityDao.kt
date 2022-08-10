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

    @Query("DELETE FROM activity where id = :id")
    suspend fun deleteActivity(id: String)

    @Query("SELECT * FROM activity")
    fun loadActivities(): Flow<List<Activity>>
}
