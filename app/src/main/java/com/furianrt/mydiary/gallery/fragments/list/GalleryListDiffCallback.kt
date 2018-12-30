package com.furianrt.mydiary.gallery.fragments.list

import androidx.recyclerview.widget.DiffUtil
import com.furianrt.mydiary.data.model.MyImage

class GalleryListDiffCallback(
        private val mOldList: List<MyImage>,
        private val mNewList: List<MyImage>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            mOldList[oldItemPosition].name == mNewList[newItemPosition].name

    override fun getOldListSize(): Int = mOldList.size

    override fun getNewListSize(): Int = mNewList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            mOldList[oldItemPosition] == mNewList[newItemPosition]
}