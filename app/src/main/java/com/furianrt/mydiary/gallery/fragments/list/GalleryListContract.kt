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
    }

    interface Presenter : BasePresenter<View> {

        fun onListItemClick(image: MyImage, position: Int, selectionActive: Boolean)

        fun setNoteId(noteId: String)

        fun onViewStart()

        fun onStop(images: List<MyImage>)

        fun onMultiSelectionButtonClick()

        fun onCabDeleteButtonClick()

        fun onCabSelectAllButtonClick()

        fun onSaveInstanceState(): MutableList<MyImage>

        fun onRestoreInstanceState(selectedImages: MutableList<MyImage>?)

        fun onCabCloseSelection()
    }
}