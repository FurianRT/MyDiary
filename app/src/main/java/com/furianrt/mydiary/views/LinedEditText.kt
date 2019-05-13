package com.furianrt.mydiary.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.EditText
import com.furianrt.mydiary.utils.dpToPx


class LinedEditText(context: Context, attributes: AttributeSet) : EditText(context, attributes) {

    private var mLinePaint: Paint = Paint()
    private var mBounds =  Rect()

    init {
        mLinePaint.color = Color.GRAY
        mLinePaint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        val firstLineY = getLineBounds(0, mBounds)
        val totalLines = Math.max(lineCount, height / lineHeight)
        for (i in 0 until totalLines) {
            val lineY = firstLineY + i * lineHeight + dpToPx(3f)
            canvas.drawLine(mBounds.left.toFloat(), lineY.toFloat(), mBounds.right.toFloat(), lineY.toFloat(), mLinePaint)
        }
        super.onDraw(canvas)
    }

    fun setLineColor(color: Int) {
        mLinePaint.color = color
    }
}