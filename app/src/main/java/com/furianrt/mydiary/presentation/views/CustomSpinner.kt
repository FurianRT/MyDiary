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
import android.util.AttributeSet
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner

class CustomSpinner : AppCompatSpinner {

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    var onItemSelectListener: ((adapter: Spinner, position: Int) -> Unit)? = null

    override fun setSelection(position: Int) {
        super.setSelection(position)
        onItemSelectListener?.invoke(this, position)
    }

    override fun setSelection(position: Int, animate: Boolean) {
        super.setSelection(position, animate)
        onItemSelectListener?.invoke(this, position)
    }
}