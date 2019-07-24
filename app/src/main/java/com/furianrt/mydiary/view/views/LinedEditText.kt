package com.furianrt.mydiary.view.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.furianrt.mydiary.utils.dpToPx
import kotlin.math.max


class LinedEditText(context: Context, attributes: AttributeSet) : AppCompatEditText(context, attributes) {

    private var mLinePaint: Paint = Paint()
    private var mBounds =  Rect()

    init {
        mLinePaint.color = Color.GRAY
        mLinePaint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        val firstLineY = getLineBounds(0, mBounds)
        val totalLines = max(lineCount, height / lineHeight)
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