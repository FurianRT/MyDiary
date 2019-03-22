package com.furianrt.mydiary.gallery.fragments.list

import androidx.recyclerview.widget.DiffUtil
import com.furianrt.mydiary.gallery.fragments.list.GalleryListAdapter.ViewItem.Companion.TYPE_FOOTER
import com.furianrt.mydiary.gallery.fragments.list.GalleryListAdapter.ViewItem.Companion.TYPE_IMAGE

class GalleryListDiffCallback(
        private val mOldList: List<GalleryListAdapter.ViewItem>,
        private val mNewList: List<GalleryListAdapter.ViewItem>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            if (mOldList[oldItemPosition].type == TYPE_FOOTER
                    && mNewList[newItemPosition].type == TYPE_FOOTER) {
                true
            } else if (mOldList[oldItemPosition].type == TYPE_IMAGE
                    && mNewList[newItemPosition].type == TYPE_IMAGE) {
                mOldList[oldItemPosition].image?.name == mNewList[newItemPosition].image?.name
            } else {
                false
            }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            if (mOldList[oldItemPosition].type == TYPE_FOOTER
                    && mNewList[newItemPosition].type == TYPE_FOOTER) {
                true
            } else if (mOldList[oldItemPosition].type == TYPE_IMAGE
                    && mNewList[newItemPosition].type == TYPE_IMAGE) {
                mOldList[oldItemPosition].image == mNewList[newItemPosition].image
            } else {
                false
            }

    override fun getOldListSize(): Int = mOldList.size

    override fun getNewListSize(): Int = mNewList.size
}