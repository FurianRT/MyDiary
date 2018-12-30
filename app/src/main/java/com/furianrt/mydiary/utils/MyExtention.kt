package com.furianrt.mydiary.utils

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.view.View
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT)
}

fun View.animateScale(from: Float, to: Float, duration: Long) {
    ObjectAnimator.ofPropertyValuesHolder(
            this,
            PropertyValuesHolder.ofFloat(View.SCALE_X, from, to),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, from, to)
    ).apply {
        this.duration = duration
        interpolator = OvershootInterpolator()
    }.start()
}