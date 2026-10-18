package com.example.kmpprojectdemo.utils

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970

actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

actual fun formatDate(millis: Long): String {
    val date = NSDate(timeIntervalSinceReferenceDate = millis / 1000.0 - NSTimeIntervalSince1970)
    val formatter = NSDateFormatter()
    formatter.dateFormat = "MMM d, yyyy"
    formatter.locale = NSLocale(localeIdentifier = "en_US")
    return formatter.stringFromDate(date)
}

actual fun isToday(millis: Long): String {
    val date = NSDate(timeIntervalSinceReferenceDate = millis / 1000.0 - NSTimeIntervalSince1970)
    val now = NSDate()
    val fmt = NSDateFormatter()
    fmt.dateFormat = "yyyy-MM-dd"
    fmt.locale = NSLocale(localeIdentifier = "en_US")
    return if (fmt.stringFromDate(date) == fmt.stringFromDate(now)) {
        "Today"
    } else {
        formatDate(millis)
    }
}
