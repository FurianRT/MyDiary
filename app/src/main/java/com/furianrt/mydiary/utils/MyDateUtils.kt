package com.furianrt.mydiary.utils

import java.text.SimpleDateFormat
import java.util.*

fun getDay(time: Long): String {
    val spf = SimpleDateFormat("dd", Locale.getDefault())
    return spf.format(Date(time))

}

fun getDayOfWeek(time: Long): String {
    val spf = SimpleDateFormat("EEE", Locale.getDefault())
    return spf.format(Date(time))
}

fun formatTime(time: Long): String {
    val spf = SimpleDateFormat("EEE, dd MMM, yyyy", Locale.getDefault())
    return spf.format(Date(time))
}

fun getMonth(time: Long): String {
    val spf = SimpleDateFormat("LLLL", Locale.getDefault())
    return spf.format(Date(time))

}

fun getYear(time: Long): String {
    val spf = SimpleDateFormat("yyyy", Locale.getDefault())
    return spf.format(Date(time))

}

fun getTime(time: Long, is24TimeFormat: Boolean): String {
    val spf = if (is24TimeFormat) {
        SimpleDateFormat("HH:mm", Locale.ENGLISH)
    } else {
        SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    }
    return spf.format(Date(time)).toLowerCase()
}