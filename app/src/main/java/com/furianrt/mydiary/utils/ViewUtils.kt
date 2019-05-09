package com.furianrt.mydiary.utils

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.res.Resources
import android.view.View
import android.view.animation.OvershootInterpolator

fun dpToPx(dp: Float): Int = Math.round(dp * Resources.getSystem().displayMetrics.density)

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