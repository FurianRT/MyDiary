package com.furianrt.mydiary.general

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.android.material.appbar.AppBarLayout

class AppBarLayoutBehavior(context: Context, attrs: AttributeSet)
    : AppBarLayout.Behavior(context, attrs) {

    var shouldScroll = false

    override fun onStartNestedScroll(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: AppBarLayout,
                                     directTargetChild: View, target: View, nestedScrollAxes: Int,
                                     type: Int): Boolean {
        return shouldScroll
    }

    override fun onTouchEvent(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: AppBarLayout, ev: MotionEvent): Boolean {
        return if (shouldScroll) {
            super.onTouchEvent(parent, child, ev)
        } else {
            false
        }
    }
}