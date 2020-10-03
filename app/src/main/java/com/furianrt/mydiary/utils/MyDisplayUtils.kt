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

import android.content.res.Resources
import android.os.Build
import android.view.WindowInsets
import kotlin.math.roundToInt

fun getDisplayWidth(): Int = Resources.getSystem().displayMetrics.widthPixels

fun dpToPx(dp: Float): Int = (dp * Resources.getSystem().displayMetrics.density).roundToInt()

fun WindowInsets.getWindowInsetTop() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    getInsets(WindowInsets.Type.systemBars()).top
} else {
    @Suppress("DEPRECATION")
    systemWindowInsetTop
}