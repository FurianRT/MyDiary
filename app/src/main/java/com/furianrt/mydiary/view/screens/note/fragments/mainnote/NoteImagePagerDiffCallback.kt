package com.furianrt.mydiary.view.screens.note.fragments.mainnote

import androidx.recyclerview.widget.DiffUtil
import com.furianrt.mydiary.data.model.MyImage

class NoteImagePagerDiffCallback(
        private val oldList: List<MyImage>,
        private val newList: List<MyImage>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].name == newList[newItemPosition].name

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] == newList[newItemPosition]

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size
}