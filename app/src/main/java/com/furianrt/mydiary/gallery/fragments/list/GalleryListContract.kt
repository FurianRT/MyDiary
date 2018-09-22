package com.furianrt.mydiary.gallery.fragments.list

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyImage

interface GalleryListContract {

    interface View : BaseView {

        fun showViewImagePager(noteId: String, position: Int)

        fun showImages(images: List<MyImage>)

    }

    interface Presenter : BasePresenter<View> {

        fun onListItemClick(image: MyImage, position: Int)

        fun setNoteId(noteId: String)

        fun onViewCreate()

        fun onStop(images: List<MyImage>)
    }
}