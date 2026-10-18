package com.example.kmpprojectdemo.utils

expect fun currentTimeMillis(): Long
expect fun formatDate(millis: Long): String
expect fun isToday(millis: Long): String

fun getDisplayDate(storedTimestamp: String?, createdAt: Long): String {
    if (createdAt > 0L) {
        val dateStatus = isToday(createdAt)
        if (dateStatus == "Today") return "Today"
        if (dateStatus.isNotBlank()) return dateStatus
    }
    if (!storedTimestamp.isNullOrBlank() && storedTimestamp != "Today") {
        return storedTimestamp
    }
    return if (createdAt > 0L) formatDate(createdAt) else "Today"
}
