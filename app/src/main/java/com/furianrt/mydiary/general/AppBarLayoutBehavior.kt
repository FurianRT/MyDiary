package com.furianrt.mydiary.general

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class AppBarLayoutBehavior(context: Context, attrs: AttributeSet)
    : AppBarLayout.Behavior(context, attrs) {

    var shouldScroll = false

    override fun onStartNestedScroll(parent: CoordinatorLayout, child: AppBarLayout,
                                     directTargetChild: View, target: View, nestedScrollAxes: Int,
                                     type: Int): Boolean {
        return shouldScroll
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: AppBarLayout, ev: MotionEvent): Boolean {
        return if (shouldScroll) {
            super.onTouchEvent(parent, child, ev)
        } else {
            false
        }
    }
}