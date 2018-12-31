package com.furianrt.mydiary.gallery.fragments.list

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyImage

interface GalleryListContract {

    interface View : BaseView {

        fun showViewImagePager(noteId: String, position: Int)

        fun showImages(images: List<MyImage>, selectedImages: List<MyImage>)

        fun activateSelection()

        fun deactivateSelection()

        fun deselectItem(image: MyImage)

        fun selectItem(image: MyImage)

        fun selectItems(items: MutableList<MyImage>)

        fun closeCab()
    }

    abstract class Presenter : BasePresenter<View>() {

        abstract fun onListItemClick(image: MyImage, position: Int, selectionActive: Boolean)

        abstract fun setNoteId(noteId: String)

        abstract fun onViewStart()

        abstract fun onImagesOrderChange(images: List<MyImage>)

        abstract fun onMultiSelectionButtonClick()

        abstract fun onCabDeleteButtonClick()

        abstract fun onCabSelectAllButtonClick()

        abstract fun onSaveInstanceState(): MutableList<MyImage>

        abstract fun onRestoreInstanceState(selectedImages: MutableList<MyImage>?)

        abstract fun onCabCloseSelection()
    }
}