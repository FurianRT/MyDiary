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

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.furianrt.mydiary.R
import com.furianrt.mydiary.utils.dpToPx
import com.furianrt.mydiary.utils.getColorSupport
import kotlin.math.max

class LinedEditText : AppCompatEditText {

    private var mLinePaint: Paint = Paint()
    private var mBounds =  Rect()

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    var selectionListener: ((selStart: Int, selEnd: Int) -> Unit)? = null
    var enableLines = false

    init {
        mLinePaint.color = context.getColorSupport(R.color.grey)
        mLinePaint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        if (enableLines) {
            val firstLineY = getLineBounds(0, mBounds)
            val totalLines = max(lineCount, height / lineHeight)
            for (i in 0 until totalLines) {
                val lineY = firstLineY + i * lineHeight + dpToPx(4f)
                canvas.drawLine(mBounds.left.toFloat(), lineY.toFloat(), mBounds.right.toFloat(), lineY.toFloat(), mLinePaint)
            }
        }
        super.onDraw(canvas)
    }

    fun setLineColor(color: Int) {
        mLinePaint.color = color
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        selectionListener?.invoke(selStart, selEnd)
    }
}