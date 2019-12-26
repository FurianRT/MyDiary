/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.gallery.fragments.pager

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter
import com.furianrt.mydiary.model.entity.MyImage

interface GalleryPagerContract {

    interface View : BaseView {
        fun showImages(images: List<MyImage>)
        fun showListImagesView(noteId: String)
        fun showEditImageView(image: MyImage)
        fun showDeleteConfirmationDialog(image: MyImage)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun init(noteId: String)
        abstract fun onButtonListModeClick()
        abstract fun onButtonDeleteClick(image: MyImage)
        abstract fun onButtonEditClick(image: MyImage)
        abstract fun onImageEdited()
    }
}