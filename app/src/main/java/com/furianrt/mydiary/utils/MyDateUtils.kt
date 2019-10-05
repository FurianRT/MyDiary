/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.utils

import org.joda.time.DateTime
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.WeekFields
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

fun getDay(time: Long): String = SimpleDateFormat("dd", Locale.getDefault()).format(Date(time))

fun getDayOfWeek(time: Long): String =
        SimpleDateFormat("EEE", Locale.getDefault()).format(Date(time))

fun formatTime(time: Long): String =
        SimpleDateFormat("EEE, dd MMM, yyyy", Locale.getDefault()).format(Date(time))

fun getTime(time: Long, is24TimeFormat: Boolean): String =
        if (is24TimeFormat) {
            SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(Date(time)).toLowerCase(Locale.getDefault())
        } else {
            SimpleDateFormat("hh:mm a", Locale.getDefault())
                    .format(Date(time)).toLowerCase(Locale.getDefault())
        }

fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    var daysOfWeek = DayOfWeek.values()
    // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        daysOfWeek = rhs + lhs
    }
    return daysOfWeek
}

fun Long.toLocalDate(): LocalDate {
    val dateTime = DateTime(this)
    return LocalDate.of(dateTime.year, dateTime.monthOfYear, dateTime.dayOfMonth)
}

fun LocalDate.toMills(): Long =
    org.joda.time.LocalDate(year, monthValue, dayOfMonth).toDate().time