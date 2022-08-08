package com.ygaberman.babypoo.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Activity(
    val type: String,
    val notes: String,
) {
    @PrimaryKey()
    var id: String = UUID.randomUUID().toString()

    @ColumnInfo(name = "created_at")
    var createdAt: Date = Date()

    @ColumnInfo(name = "updated_at")
    var updatedAt: Date = Date()
}
