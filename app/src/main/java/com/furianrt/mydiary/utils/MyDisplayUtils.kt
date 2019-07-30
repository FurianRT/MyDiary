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

import android.app.Activity
import android.content.res.Resources
import android.graphics.Point
import kotlin.math.roundToInt

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

fun dpToPx(dp: Float): Int = (dp * Resources.getSystem().displayMetrics.density).roundToInt()