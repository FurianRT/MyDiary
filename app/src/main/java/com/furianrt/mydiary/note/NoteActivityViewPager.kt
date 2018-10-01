package com.furianrt.mydiary.note

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

class NoteActivityViewPager : androidx.viewpager.widget.ViewPager {

    var swipeEnabled = true

    constructor(context: Context) : super(context, null)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if (swipeEnabled) {
            super.onTouchEvent(ev)
        } else {
            false
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (swipeEnabled) {
            super.onInterceptTouchEvent(ev)
        } else {
            false
        }
    }
}