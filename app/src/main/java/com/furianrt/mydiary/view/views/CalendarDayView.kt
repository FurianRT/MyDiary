package com.furianrt.mydiary.view.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView
import kotlin.math.min
import android.graphics.PointF
import com.furianrt.mydiary.R
import com.furianrt.mydiary.utils.getThemeAccentColor
import com.furianrt.mydiary.utils.setTextColorResource
import kotlin.math.cos
import kotlin.math.sin

class CalendarDayView : TextView {

    companion object {
        private const val DEFAULT_PORTION_WIDTH = 11f
        private const val DEFAULT_CURRENT_DAY_CIRCLE_WIDTH = 7f
        private const val DEFAULT_PORTION_SPACING = 5f
        private const val START_DEGREE = 270f
        private const val DEFAULT_COLOR = Color.GRAY
    }

    inner class Portion(
            val color: Int,
            val startAngle: Float,
            val sweepAngle : Float
    )

    private var mRadius: Float = 0f
    private var mPortionColor = DEFAULT_COLOR
    private var mPortionWidth = DEFAULT_PORTION_WIDTH
    private val mBorderRect = RectF()
    private val mPaint: Paint = Paint()
    private val mPortionPaint: Paint = Paint()
    private val mSliceVector = PointF()
    private var mIsSelected: Boolean = false
    private val mPortions = mutableListOf<Portion>()

    var showCircle = false
    var isCurrentDay = false
    var isCurrentMonth = true

    init {
        mPortionPaint.color = mPortionColor
        mPortionPaint.style = Paint.Style.STROKE
        mPortionPaint.isAntiAlias = true
        mPortionPaint.strokeWidth = mPortionWidth
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBorderRect.set(calculateBounds())
        mRadius = min((mBorderRect.height() - mPortionWidth) / 2.0f, (mBorderRect.width() - mPortionWidth) / 2.0f)
    }

    override fun setSelected(selected: Boolean) {
        when {
            selected -> setTextColorResource(R.color.white)
            isCurrentMonth -> setTextColorResource(R.color.black)
            else -> setTextColorResource(R.color.black30)
        }
        mIsSelected = selected
    }

    override fun onDraw(canvas: Canvas?) {
        if (showCircle) {
            drawArcs(canvas)
            if (mPortions.size > 1) {
                drawSeparationLines(canvas)
            }
        }

        drawCenterCircle(canvas)

        if (isCurrentDay) {
            drawCurrentDayCircle(canvas)
        }

        super.onDraw(canvas)
    }

    private fun drawArcs(canvas: Canvas?) {
        val centerX = mBorderRect.centerX()
        val centerY = mBorderRect.centerY()

        mPortions.forEach {
            mPortionPaint.color = it.color
            canvas?.drawArc(
                    centerX - mRadius,
                    centerY - mRadius,
                    centerX + mRadius,
                    centerY + mRadius,
                    it.startAngle,
                    it.sweepAngle,
                    false,
                    mPortionPaint
            )
        }
    }

    private fun drawSeparationLines(canvas: Canvas?) {
        mPaint.color = Color.WHITE
        mPaint.strokeWidth = DEFAULT_PORTION_SPACING
        mPaint.style = Paint.Style.STROKE

        val circleRadius = mBorderRect.width() / 2f

        mPortions.forEach { portion ->
            mSliceVector.set(
                    cos(Math.toRadians(portion.startAngle.toDouble())).toFloat(),
                    sin(Math.toRadians(portion.startAngle.toDouble())).toFloat()
            )
            normalizeVector(mSliceVector)

            val x1 = mSliceVector.x * circleRadius + mBorderRect.centerX()
            val y1 = mSliceVector.y * circleRadius + mBorderRect.centerY()

            canvas?.drawLine(mBorderRect.centerX(), mBorderRect.centerY(), x1, y1, mPaint)
        }

        mPaint.color = Color.WHITE
        mPaint.style = Paint.Style.FILL

        canvas?.drawCircle(
                mBorderRect.centerX(),
                mBorderRect.centerY(),
                mRadius - mPortionWidth / 2,
                mPaint
        )
    }

    private fun drawCenterCircle(canvas: Canvas?) {
        mPaint.style = Paint.Style.FILL

        context?.let { context ->
            if (mIsSelected) {
                mPaint.color = context.getThemeAccentColor()
            } else {
                mPaint.color = Color.WHITE
            }

            when {
                showCircle && isCurrentDay -> {
                    canvas?.drawCircle(
                            mBorderRect.centerX(),
                            mBorderRect.centerY(),
                            mRadius - mPortionWidth - DEFAULT_CURRENT_DAY_CIRCLE_WIDTH - 2f,
                            mPaint
                    )
                }
                isCurrentDay -> {
                    canvas?.drawCircle(
                            mBorderRect.centerX(),
                            mBorderRect.centerY(),
                            mRadius - DEFAULT_CURRENT_DAY_CIRCLE_WIDTH - 2f,
                            mPaint
                    )
                }
                showCircle -> {
                    canvas?.drawCircle(
                            mBorderRect.centerX(),
                            mBorderRect.centerY(),
                            mRadius - mPortionWidth,
                            mPaint
                    )
                }
                else -> {
                    canvas?.drawCircle(
                            mBorderRect.centerX(),
                            mBorderRect.centerY(),
                            min(mBorderRect.height() / 2.0f, mBorderRect.width() / 2.0f),
                            mPaint
                    )
                }
            }
        }
    }

    private fun drawCurrentDayCircle(canvas: Canvas?) {
        mPaint.color = Color.GRAY
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = DEFAULT_CURRENT_DAY_CIRCLE_WIDTH

        val radius = if (showCircle) {
            mRadius - mPortionWidth - 2f
        } else {
            mRadius
        }

        canvas?.drawCircle(
                mBorderRect.centerX(),
                mBorderRect.centerY(),
                radius,
                mPaint
        )
    }

    private fun normalizeVector(point: PointF) {
        val abs = point.length()
        point.set(point.x / abs, point.y / abs)
    }

    private fun calculateBounds(): RectF {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom

        val sideLength = min(availableWidth, availableHeight)

        val left = paddingLeft + (availableWidth - sideLength) / 2f
        val top = paddingTop + (availableHeight - sideLength) / 2f

        return RectF(left, top, left + sideLength, top + sideLength)
    }

    fun setColors(colors: List<Int>) {
        val result = mutableListOf<Portion>()
        val groups = colors.groupingBy { it }
                .eachCount()
                .toList()
                .sortedByDescending { it.second }
                .toMap()
        for (i in 0 until groups.size) {
            val start = if (i == 0) {
                START_DEGREE
            } else {
                result[i - 1].startAngle + result[i - 1].sweepAngle
            }
            val portion = Portion(
                    groups.keys.toList()[i],
                    start,
                    (groups.getValue(groups.keys.toList()[i]).toFloat() / colors.size.toFloat() * 360f)
            )
            result.add(portion)
        }
        mPortions.clear()
        mPortions.addAll(result)
        invalidate()
    }
}