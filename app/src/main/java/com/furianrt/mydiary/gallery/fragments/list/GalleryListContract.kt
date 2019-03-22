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
        fun deselectImage(image: MyImage)
        fun selectImage(image: MyImage)
        fun selectImages(images: MutableList<MyImage>)
        fun closeCab()
        fun requestStoragePermissions()
        fun showImageExplorer()
        fun showEmptyList()
        fun showLoading()
        fun hideLoading()
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
        abstract fun onAddImageButtonClick()
        abstract fun onStoragePermissionsGranted()
        abstract fun onNoteImagesPicked(imageUrls: List<String>)
        abstract fun onImageDeleted(image: MyImage)
    }
}