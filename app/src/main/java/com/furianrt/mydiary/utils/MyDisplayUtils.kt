package com.furianrt.mydiary.utils

import android.app.Activity
import android.graphics.Point

fun Activity.getDisplayWidth(): Int {
    val display = windowManager.defaultDisplay
    val size = Point()
    try {
        display.getRealSize(size)
    } catch (err: NoSuchMethodError) {
        display.getSize(size)
    }
    return size.x
}