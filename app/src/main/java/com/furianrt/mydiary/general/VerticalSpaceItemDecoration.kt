package com.furianrt.mydiary.general

import android.graphics.Rect
import android.view.View

class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int)
    : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView,
                                state: androidx.recyclerview.widget.RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1) {
            outRect.bottom = verticalSpaceHeight
        }
    }
}