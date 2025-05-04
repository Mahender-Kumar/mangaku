package com.example.mangaku.core.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.Duration

@RequiresApi(Build.VERSION_CODES.O)
fun getTimeAgo(timestampMillis: Long): String {
    val updateTime = Instant.ofEpochMilli(timestampMillis)
    val now = Instant.now()
    val duration = Duration.between(updateTime, now)

    return when {
        duration.toMinutes() < 1 -> "Updated just now"
        duration.toMinutes() < 60 -> "Updated ${duration.toMinutes()} min ago"
        duration.toHours() < 24 -> "Updated ${duration.toHours()} hr ago"
        else -> "Updated ${duration.toDays()} day(s) ago"
    }
}
