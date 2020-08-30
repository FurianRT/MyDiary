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
import kotlin.math.roundToInt

fun getDisplayWidth(): Int = Resources.getSystem().displayMetrics.widthPixels

fun dpToPx(dp: Float): Int = (dp * Resources.getSystem().displayMetrics.density).roundToInt()