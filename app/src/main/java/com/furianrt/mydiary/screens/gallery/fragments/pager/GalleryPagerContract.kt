package com.furianrt.mydiary.screens.gallery.fragments.pager

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.model.MyImage

interface GalleryPagerContract {

    interface MvpView : BaseMvpView {
        fun showImages(images: List<MyImage>)
        fun showListImagesView(noteId: String)
        fun showEditImageView(image: MyImage)
        fun showDeleteConfirmationDialog(image: MyImage)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onViewResume(noteId: String)
        abstract fun onButtonListModeClick(noteId: String)
        abstract fun onButtonDeleteClick(image: MyImage)
        abstract fun onButtonEditClick(image: MyImage)
        abstract fun onImageEdited()
    }
}