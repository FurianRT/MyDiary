package com.furianrt.mydiary.general

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class LockableBottomSheetBehavior<V : View>(context: Context, attrs: AttributeSet)
    : BottomSheetBehavior<V>(context, attrs) {

    var locked: Boolean = false

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean =
            if (locked) false else super.onInterceptTouchEvent(parent, child, event)

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean =
            if (locked) false else super.onTouchEvent(parent, child, event)
}