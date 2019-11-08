/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.gallery.fragments.list

import androidx.recyclerview.widget.DiffUtil
import com.furianrt.mydiary.presentation.screens.gallery.fragments.list.GalleryListAdapter.ViewItem.Companion.TYPE_FOOTER
import com.furianrt.mydiary.presentation.screens.gallery.fragments.list.GalleryListAdapter.ViewItem.Companion.TYPE_IMAGE

class GalleryListDiffCallback(
        private val oldList: List<GalleryListAdapter.ViewItem>,
        private val newList: List<GalleryListAdapter.ViewItem>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            if (oldList[oldItemPosition].type == TYPE_FOOTER
                    && newList[newItemPosition].type == TYPE_FOOTER) {
                true
            } else if (oldList[oldItemPosition].type == TYPE_IMAGE
                    && newList[newItemPosition].type == TYPE_IMAGE) {
                oldList[oldItemPosition].image?.name == newList[newItemPosition].image?.name
            } else {
                false
            }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            if (oldList[oldItemPosition].type == TYPE_FOOTER
                    && newList[newItemPosition].type == TYPE_FOOTER) {
                true
            } else if (oldList[oldItemPosition].type == TYPE_IMAGE
                    && newList[newItemPosition].type == TYPE_IMAGE) {
                oldList[oldItemPosition].image == newList[newItemPosition].image
            } else {
                false
            }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size
}