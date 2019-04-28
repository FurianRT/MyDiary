package com.furianrt.mydiary.screens.gallery.fragments.list

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
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
        fun showSelectedImageCount(count: Int)
        fun showDeleteConfirmationDialog(images: List<MyImage>)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onListItemClick(image: MyImage, position: Int, selectionActive: Boolean)
        abstract fun setNoteId(noteId: String)
        abstract fun onViewStart()
        abstract fun onImagesOrderChange(images: List<MyImage>)
        abstract fun onButtonMultiSelectionClick()
        abstract fun onButtonCabDeleteClick()
        abstract fun onButtonCabSelectAllClick()
        abstract fun onSaveInstanceState(): MutableList<MyImage>
        abstract fun onRestoreInstanceState(selectedImages: MutableList<MyImage>?)
        abstract fun onCabCloseSelection()
        abstract fun onButtonAddImageClick()
        abstract fun onStoragePermissionsGranted()
        abstract fun onNoteImagesPicked(imageUrls: List<String>)
        abstract fun onImageTrashed(image: MyImage)
        abstract fun onButtonDeleteConfirmClick(images: List<MyImage>)
    }
}