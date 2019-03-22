package com.furianrt.mydiary.utils

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager

fun pxToDp(px: Float): Float {
    val densityDpi = Resources.getSystem().displayMetrics.densityDpi.toFloat()
    return px / (densityDpi / 160f)
}

fun dpToPx(dp: Float): Int {
    val density = Resources.getSystem().displayMetrics.density
    return Math.round(dp * density)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Activity.hideKeyboard() {
    currentFocus?.let {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
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

fun View.animateShake(duration: Long) {
    ObjectAnimator
            .ofFloat(this, "translationX", 0f, 52f, -26f, 52f, 0f)
            .setDuration(duration)
            .start()
}

fun View.animateAlpha(from: Float, to: Float, duration: Long) {
    ObjectAnimator
            .ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat(View.ALPHA, from, to))
            .apply { this.duration = duration }
            .start()
}