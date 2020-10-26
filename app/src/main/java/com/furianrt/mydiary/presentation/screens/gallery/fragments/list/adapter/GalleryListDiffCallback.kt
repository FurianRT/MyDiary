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

class GalleryListDiffCallback(
        private val oldList: List<ImagesListItem>,
        private val newList: List<ImagesListItem>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return when {
            oldItem is ImagesListItem.Footer && newItem is ImagesListItem.Footer -> {
                true
            }
            oldItem is ImagesListItem.Image && newItem is ImagesListItem.Image -> {
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
            oldItem is ImagesListItem.Footer && newItem is ImagesListItem.Footer -> {
                true
            }
            oldItem is ImagesListItem.Image && newItem is ImagesListItem.Image -> {
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