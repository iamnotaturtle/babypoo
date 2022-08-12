package com.ygaberman.babypoo.io

import com.ygaberman.babypoo.db.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

fun exportToCSV(
    filename: String,
    path: File?,
    header: String,
    activities: List<Activity>
): File {
    val fileOut = File(path, filename)

    CoroutineScope(Dispatchers.IO).launch {
        runCatching {
            FileOutputStream(fileOut).use {
                it.write(header.toByteArray())
                for (activity in activities) {
                    it.write("${activity.createdAt},${activity.type},${activity.notes}\n".toByteArray())
                }
            }
        }
    }
    return fileOut
}