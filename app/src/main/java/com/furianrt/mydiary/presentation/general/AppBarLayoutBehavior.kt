/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.general

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

class AppBarLayoutBehavior(
        context: Context,
        attrs: AttributeSet
) : AppBarLayout.Behavior(context, attrs) {

    var shouldScroll = false

    override fun onStartNestedScroll(
            parent: CoordinatorLayout, child: AppBarLayout,
            directTargetChild: View, target: View, nestedScrollAxes: Int,
            type: Int
    ) = shouldScroll

    override fun onTouchEvent(parent: CoordinatorLayout, child: AppBarLayout, ev: MotionEvent) =
            if (shouldScroll) super.onTouchEvent(parent, child, ev) else false
}