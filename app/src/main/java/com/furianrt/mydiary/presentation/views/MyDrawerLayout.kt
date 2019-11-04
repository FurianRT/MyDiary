/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.views

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout

class MyDrawerLayout : DrawerLayout {

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

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