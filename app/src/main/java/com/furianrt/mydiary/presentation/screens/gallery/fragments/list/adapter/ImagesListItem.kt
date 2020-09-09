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

import com.furianrt.mydiary.model.entity.MyImage

sealed class ImagesListItem {

    fun getTypeId() = when (this) {
        is Image -> Image.TYPE_ID
        is Footer -> Footer.TYPE_ID
    }

    fun clone(): ImagesListItem = when (this) {
        is Image -> this.copy()
        is Footer -> Footer()
    }

    data class Image(val image: MyImage) : ImagesListItem() {
        companion object {
            const val TYPE_ID = 0
        }
    }

    class Footer : ImagesListItem() {
        companion object {
            const val TYPE_ID = 1
        }
    }
}