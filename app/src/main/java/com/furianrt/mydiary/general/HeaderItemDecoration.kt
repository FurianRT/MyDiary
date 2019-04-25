package com.furianrt.mydiary.general

import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HeaderItemDecoration(
        private val recyclerView: RecyclerView,
        private val listener: StickyHeaderInterface
) : RecyclerView.ItemDecoration() {

    private val mHeaderContainer = CardView(recyclerView.context)
    private var mStickyHeaderHeight: Int = 0
    private var mCurrentHeader: View? = null
    private var mCurrentHeaderPosition = 0
    private var mTempFlag = false

    init {
        mHeaderContainer.radius = 0f
        val layout = FrameLayout(recyclerView.context)
        val params = recyclerView.layoutParams
        val parent = recyclerView.parent as ViewGroup
        val index = parent.indexOfChild(recyclerView)
        parent.addView(layout, index, params)
        parent.removeView(recyclerView)
        layout.addView(recyclerView, FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT)
        layout.addView(mHeaderContainer, FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val topChild = parent.getChildAt(0) ?: return

        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) {
            mHeaderContainer.visibility = View.GONE
            return
        }

        if (mHeaderContainer.visibility == View.GONE) {
            mHeaderContainer.visibility = View.VISIBLE
        }

        if ((recyclerView.layoutManager as LinearLayoutManager)
                        .findFirstCompletelyVisibleItemPosition() == 0) {
            mHeaderContainer.cardElevation = 0f
            return
        }

        val currentHeader = getHeaderViewForItem(topChildPosition, parent)
        fixLayoutSize(parent, currentHeader)
        val contactPoint = currentHeader.bottom
        val childInContact = getChildInContact(parent, contactPoint) ?: return

        val nextPosition = parent.getChildAdapterPosition(childInContact)
        if (listener.isHeader(nextPosition)) {
            moveHeader(currentHeader, childInContact, topChildPosition, nextPosition)
            return
        }

        drawHeader(currentHeader, topChildPosition)
    }

    private fun getHeaderViewForItem(itemPosition: Int, parent: RecyclerView): View {
        val headerPosition = listener.getHeaderPositionForItem(itemPosition)
        val layoutResId = listener.getHeaderLayout(headerPosition)
        val header = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        listener.bindHeaderData(header, headerPosition)
        return header
    }

    private fun drawHeader(header: View, position: Int) {
        mHeaderContainer.layoutParams.height = mStickyHeaderHeight
        mHeaderContainer.cardElevation = 8f
        setCurrentHeader(header, position)
    }

    private fun moveHeader(currentHead: View, nextHead: View, currentPos: Int, nextPos: Int) {
        if (mTempFlag) {
            mHeaderContainer.removeAllViews()
            mHeaderContainer.addView(mCurrentHeader)
            mTempFlag = false
        }

        val marginTop = nextHead.top - currentHead.height
        mHeaderContainer.cardElevation = 0f

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

    private fun setCurrentHeader(header: View, position: Int) {
        mCurrentHeader = header
        mCurrentHeaderPosition = position
        mHeaderContainer.removeAllViews()
        mHeaderContainer.addView(mCurrentHeader)
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? =
            (0 until parent.childCount)
                    .map { parent.getChildAt(it) }
                    .firstOrNull { it.bottom > contactPoint && it.top <= contactPoint }

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