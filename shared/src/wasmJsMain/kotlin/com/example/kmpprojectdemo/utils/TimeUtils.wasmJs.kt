package com.example.kmpprojectdemo.utils

private fun dateNow(): Double = js("Date.now()")
private fun getYear(m: Double): Int = js("new Date(m).getFullYear()")
private fun getMonth(m: Double): Int = js("new Date(m).getMonth()")
private fun getDate(m: Double): Int = js("new Date(m).getDate()")

actual fun currentTimeMillis(): Long = dateNow().toLong()

actual fun formatDate(millis: Long): String {
    val m = millis.toDouble()
    val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val monthIdx = getMonth(m)
    val safeMonth = if (monthIdx in 0..11) months[monthIdx] else "Oct"
    return "$safeMonth ${getDate(m)}, ${getYear(m)}"
}

actual fun isToday(millis: Long): String {
    val m = millis.toDouble()
    val now = dateNow()
    return if (getYear(m) == getYear(now) && getMonth(m) == getMonth(now) && getDate(m) == getDate(now)) {
        "Today"
    } else {
        formatDate(millis)
    }
}
