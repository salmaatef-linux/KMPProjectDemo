package com.example.kmpprojectdemo.utils

import kotlin.js.Date

actual fun currentTimeMillis(): Long = Date.now().toLong()

actual fun formatDate(millis: Long): String {
    val d = Date(millis)
    val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return "${months[d.getMonth()]} ${d.getDate()}, ${d.getFullYear()}"
}

actual fun isToday(millis: Long): String {
    val d = Date(millis)
    val now = Date()
    return if (d.getFullYear() == now.getFullYear() && d.getMonth() == now.getMonth() && d.getDate() == now.getDate()) {
        "Today"
    } else {
        formatDate(millis)
    }
}
