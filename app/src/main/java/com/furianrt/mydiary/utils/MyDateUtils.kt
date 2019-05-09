package com.furianrt.mydiary.utils

import java.text.SimpleDateFormat
import java.util.*

fun getDay(time: Long): String =
        SimpleDateFormat("dd", Locale.getDefault()).format(Date(time))

fun getDayOfWeek(time: Long): String =
        SimpleDateFormat("EEE", Locale.getDefault()).format(Date(time))

fun formatTime(time: Long): String =
        SimpleDateFormat("EEE, dd MMM, yyyy", Locale.getDefault()).format(Date(time))

fun getMonth(time: Long): String =
        SimpleDateFormat("LLLL", Locale.getDefault()).format(Date(time))

fun getYear(time: Long): String =
        SimpleDateFormat("yyyy", Locale.getDefault()).format(Date(time))

fun getTime(time: Long, is24TimeFormat: Boolean): String =
    if (is24TimeFormat) {
        SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(Date(time)).toLowerCase(Locale.getDefault())
    } else {
        SimpleDateFormat("hh:mm a", Locale.getDefault())
                .format(Date(time)).toLowerCase(Locale.getDefault())
    }