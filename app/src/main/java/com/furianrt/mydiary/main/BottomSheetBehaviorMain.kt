package com.furianrt.mydiary.main

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BottomSheetBehaviorMain<V : View>(context: Context, attrs: AttributeSet)
    : BottomSheetBehavior<V>(context, attrs) {

    companion object {
        fun <V : View> from(view: V): BottomSheetBehaviorMain<V> {
            val params =
                    view.layoutParams as? CoordinatorLayout.LayoutParams ?: throw IllegalArgumentException("The view is not a child of CoordinatorLayout")
            val behavior =
                    params.behavior as? BottomSheetBehaviorMain<*> ?: throw IllegalArgumentException(
                    "The view is not associated with BottomSheetBehavior")
            return behavior as BottomSheetBehaviorMain<V>
        }
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        return false
    }
}