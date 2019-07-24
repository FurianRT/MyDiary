package com.furianrt.mydiary.view.views

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout

class MyDrawerLayout(context: Context, attributes: AttributeSet) : DrawerLayout(context, attributes) {

    var touchEventChildId: Int = -1

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (touchEventChildId != -1) {
            val scroll = findViewById<View>(touchEventChildId)
            if (scroll != null) {
                val rect = Rect()
                scroll.getGlobalVisibleRect(rect)
                if (rect.contains(ev.x.toInt(), ev.y.toInt())) {
                    requestDisallowInterceptTouchEvent(true)
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}