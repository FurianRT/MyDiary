package com.furianrt.mydiary.gallery.fragments.pager

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyImage

interface GalleryPagerContract {

    interface View : BaseView {

        fun showImages(images: List<MyImage>)

        fun showListImagesView(noteId: String)
    }

    interface Presenter : BasePresenter<View> {

        fun onViewStart()

        fun setNoteId(noteId: String)

        fun onListModeButtonClick()
    }
}