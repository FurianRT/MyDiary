package com.furianrt.mydiary.utils

import android.content.Context
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R

fun Context.getThemeAccentColor(): Int {
    val value = TypedValue()
    theme.resolveAttribute(R.attr.colorAccent, value, true)
    return value.data
}

fun Context.getThemePrimaryColor(): Int {
    val value = TypedValue()
    theme.resolveAttribute(R.attr.colorPrimary, value, true)
    return value.data
}

fun Context.getThemePrimaryDarkColor(): Int {
    val value = TypedValue()
    theme.resolveAttribute(R.attr.colorPrimaryDark, value, true)
    return value.data
}

fun Fragment.getColor(@ColorRes id: Int) = ContextCompat.getColor(requireContext(), id)

fun TextView.setTextColorResource(@ColorRes id: Int) {
    setTextColor(ContextCompat.getColor(this.context, id))
}