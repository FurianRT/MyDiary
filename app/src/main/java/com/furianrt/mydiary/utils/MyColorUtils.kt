package com.furianrt.mydiary.utils

import android.content.Context
import android.util.TypedValue
import com.furianrt.mydiary.R

fun getThemeAccentColor(context: Context): Int {
    val value = TypedValue()
    context.theme.resolveAttribute(R.attr.colorAccent, value, true)
    return value.data
}

fun getThemePrimaryColor(context: Context): Int {
    val value = TypedValue()
    context.theme.resolveAttribute(R.attr.colorPrimary, value, true)
    return value.data
}

fun getThemePrimaryDarkColor(context: Context): Int {
    val value = TypedValue()
    context.theme.resolveAttribute(R.attr.colorPrimaryDark, value, true)
    return value.data
}