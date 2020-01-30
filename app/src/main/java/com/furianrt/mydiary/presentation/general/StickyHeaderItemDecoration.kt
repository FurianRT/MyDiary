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

import android.graphics.Canvas
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.furianrt.mydiary.utils.animateAlpha

class StickyHeaderItemDecoration(
        private val recyclerView: RecyclerView,
        private val listener: StickyHeaderInterface
) : RecyclerView.ItemDecoration() {

    companion object {
        private const val HIDE_DELAY = 1000L
    }

    private val mHeaderContainer = FrameLayout(recyclerView.context)
    private var mStickyHeaderHeight: Int = 0
    private var mCurrentHeader: View? = null
    private var mCurrentHeaderPosition = 0
    private var mPrevMargin = -1
    private var mPrevPosition = -1

    //todo Well, yeah...
    private var mTempFlag = false
    private var mTempFlag2 = false
    private var mTempFlag3 = false

    private val mHandler = Handler()
    private val mHideRunnable = Runnable {
        mTempFlag3 = false
        mCurrentHeader?.let { it.animateAlpha(it.alpha, 0f) }
    }

    init {
        val layout = FrameLayout(recyclerView.context)
        val params = recyclerView.layoutParams
        val parent = recyclerView.parent as ViewGroup
        val index = parent.indexOfChild(recyclerView)
        parent.addView(layout, index, params)
        parent.removeView(recyclerView)
        layout.addView(recyclerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layout.addView(mHeaderContainer, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> if (recyclerView.childCount > 0) {
                        mHandler.removeCallbacks(mHideRunnable)
                        mTempFlag3 = true
                        mCurrentHeader?.alpha = 1f
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> mHandler.postDelayed(mHideRunnable, HIDE_DELAY)
                }
            }
        })
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val topChild = parent.getChildAt(0)
        if (topChild == null) {
            mHeaderContainer.visibility = View.GONE
            return
        }

        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) {
            mHeaderContainer.visibility = View.GONE
            return
        }

        if (mHeaderContainer.visibility == View.GONE) {
            mHeaderContainer.visibility = View.VISIBLE
        }

        val firstVisiblePosition = (parent.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        if (firstVisiblePosition == 0) {
            recyclerView.getChildAt(0)?.visibility = View.VISIBLE
        }

        val currentHeader = getHeaderViewForItem(topChildPosition, parent)
        fixLayoutSize(parent, currentHeader)
        val contactPoint = currentHeader.bottom

        (0 until parent.childCount)
                .map { parent.getChildAt(it) }
                .filter { child ->
                    val params = child.layoutParams as ViewGroup.MarginLayoutParams
                    val childBottomWithMargin = child.bottom + params.bottomMargin
                    childBottomWithMargin > contactPoint
                }
                .forEach { it.visibility = View.VISIBLE }

        val childInContact = getChildInContact(parent, contactPoint) ?: return
        if (childInContact.visibility == View.INVISIBLE) {
            childInContact.visibility = View.VISIBLE
        }

        if (mTempFlag2) {
            val itemPosition = parent.getChildAdapterPosition(parent.getChildAt(0))
            if (listener.isHeader(itemPosition)) {
                parent.getChildAt(0).visibility = View.INVISIBLE
            }
            mTempFlag2 = false
        }

        val nextPosition = parent.getChildAdapterPosition(childInContact)
        if (listener.isHeader(nextPosition)) {
            moveHeader(currentHeader, childInContact, topChildPosition, nextPosition)
            return
        }

        drawHeader(currentHeader, topChildPosition, parent)
    }

    private fun getHeaderViewForItem(itemPosition: Int, parent: RecyclerView): View {
        val headerPosition = listener.getHeaderPositionForItem(itemPosition)
        val layoutResId = listener.getHeaderLayout(headerPosition)
        val header = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        listener.bindHeaderData(header, headerPosition)
        return header
    }

    private fun drawHeader(header: View, position: Int, parent: RecyclerView) {
        if (mPrevPosition != position) {
            mPrevPosition = position
            val firstVisiblePosition = (parent.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            if (mTempFlag3 || firstVisiblePosition == 0) {
                mHeaderContainer.layoutParams.height = mStickyHeaderHeight
                setCurrentHeader(header, position)
            }
            mTempFlag2 = true
        }
    }

    private fun moveHeader(currentHead: View, nextHead: View, currentPos: Int, nextPos: Int) {
        val marginTop = nextHead.top - currentHead.height
        if (mPrevMargin != marginTop) {
            mPrevMargin = marginTop
            if (mTempFlag) {
                mHeaderContainer.removeAllViews()
                mHeaderContainer.addView(mCurrentHeader)
                mTempFlag = false
            }

            val params = mCurrentHeader?.layoutParams as? ViewGroup.MarginLayoutParams ?: return
            params.setMargins(0, marginTop, 0, 0)
            mCurrentHeader?.layoutParams = params

            mHeaderContainer.layoutParams.height = mStickyHeaderHeight + marginTop

            if (mCurrentHeaderPosition == nextPos && currentPos != nextPos) {
                mCurrentHeader = currentHead
                mCurrentHeaderPosition = currentPos
                mTempFlag = true
            }
        }
    }

    private fun setCurrentHeader(header: View, position: Int) {
        mCurrentHeader = header
        mCurrentHeaderPosition = position
        mHeaderContainer.removeAllViews()
        mHeaderContainer.addView(mCurrentHeader)
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? =
            (0 until parent.childCount)
                    .map { parent.getChildAt(it) }
                    .firstOrNull { child ->
                        val params = child.layoutParams as ViewGroup.MarginLayoutParams
                        val childBottomWithMargin = child.bottom + params.bottomMargin
                        val childTopWithMargin = child.top - params.topMargin
                        contactPoint in childTopWithMargin until childBottomWithMargin
                    }

    private fun fixLayoutSize(parent: ViewGroup, view: View) {

        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidthSpec = ViewGroup.getChildMeasureSpec(
                widthSpec,
                parent.paddingLeft + parent.paddingRight,
                view.layoutParams.width
        )
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
                heightSpec,
                parent.paddingTop + parent.paddingBottom,
                view.layoutParams.height
        )

        view.measure(childWidthSpec, childHeightSpec)

        mStickyHeaderHeight = view.measuredHeight
        view.layout(0, 0, view.measuredWidth, mStickyHeaderHeight)
    }

    interface StickyHeaderInterface {
        fun getHeaderPositionForItem(itemPosition: Int): Int
        fun getHeaderLayout(headerPosition: Int): Int
        fun bindHeaderData(header: View, headerPosition: Int)
        fun isHeader(itemPosition: Int): Boolean
    }
}