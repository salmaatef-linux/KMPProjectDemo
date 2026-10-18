package com.example.kmpprojectdemo.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual fun formatDate(millis: Long): String {
    return SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(millis))
}

actual fun isToday(millis: Long): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return if (fmt.format(Date(millis)) == fmt.format(Date(currentTimeMillis()))) {
        "Today"
    } else {
        formatDate(millis)
    }
}
