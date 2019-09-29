/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class LockableViewPager : ViewPager {

    var swipeEnabled = true

    constructor(context: Context) : super(context, null)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean =
            if (swipeEnabled) {
                try {
                    super.onTouchEvent(ev)
                } catch (e: IllegalArgumentException) {
                    false
                }
            } else {
                false
            }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean =
            if (swipeEnabled) {
                try {
                    super.onInterceptTouchEvent(ev)
                } catch (e: IllegalArgumentException) {
                    false
                }
            } else {
                false
            }
}