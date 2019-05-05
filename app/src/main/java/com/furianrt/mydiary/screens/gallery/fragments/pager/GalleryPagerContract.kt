package com.furianrt.mydiary.screens.gallery.fragments.pager

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyImage

interface GalleryPagerContract {

    interface View : BaseView {
        fun showImages(images: List<MyImage>)
        fun showListImagesView(noteId: String)
        fun showEditImageView(image: MyImage)
        fun showDeleteConfirmationDialog(image: MyImage)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onViewStart()
        abstract fun setNoteId(noteId: String)
        abstract fun onButtonListModeClick()
        abstract fun onButtonDeleteClick(image: MyImage)
        abstract fun onButtonEditClick(image: MyImage)
        abstract fun onImageEdited()
    }
}