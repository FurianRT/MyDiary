/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.gallery.fragments.list.adapter

import androidx.recyclerview.widget.DiffUtil
import com.furianrt.mydiary.presentation.screens.gallery.fragments.list.adapter.entity.BaseImageListItem
import com.furianrt.mydiary.presentation.screens.gallery.fragments.list.adapter.entity.ImageListFooter
import com.furianrt.mydiary.presentation.screens.gallery.fragments.list.adapter.entity.ImageListItem

class GalleryListDiffCallback(
        private val oldList: List<BaseImageListItem>,
        private val newList: List<BaseImageListItem>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return when {
            oldItem is ImageListFooter && newItem is ImageListFooter -> {
                true
            }
            oldItem is ImageListItem && newItem is ImageListItem -> {
                oldItem.image.name == newItem.image.name
            }
            else -> {
                false
            }
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return when {
            oldItem is ImageListFooter && newItem is ImageListFooter -> {
                true
            }
            oldItem is ImageListItem && newItem is ImageListItem -> {
                oldItem.image == newItem.image
            }
            else -> {
                false
            }
        }
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size
}