/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.gallery.fragments.list

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.model.entity.MyImage

interface GalleryListContract {

    interface MvpView : BaseMvpView {
        fun showViewImagePager(noteId: String, position: Int)
        fun showImages(images: List<MyImage>, selectedImageNames: Set<String>)
        fun activateSelection()
        fun deactivateSelection()
        fun deselectImage(imageName: String)
        fun selectImage(imageName: String)
        fun selectImages(imageNames: Set<String>)
        fun closeCab()
        fun requestStoragePermissions()
        fun showImageExplorer()
        fun showEmptyList()
        fun showLoading()
        fun hideLoading()
        fun showSelectedImageCount(count: Int)
        fun showDeleteConfirmationDialog(imageNames: List<String>)
        fun showErrorSaveImage()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onListItemClick(image: MyImage, position: Int, selectionActive: Boolean)
        abstract fun init(noteId: String)
        abstract fun onImagesOrderChange(images: List<MyImage>)
        abstract fun onButtonMultiSelectionClick()
        abstract fun onButtonCabDeleteClick()
        abstract fun onButtonCabSelectAllClick()
        abstract fun onSaveInstanceState(): Set<String>
        abstract fun onRestoreInstanceState(selectedImageNames: Set<String>?)
        abstract fun onCabCloseSelection()
        abstract fun onButtonAddImageClick()
        abstract fun onStoragePermissionsGranted()
        abstract fun onNoteImagesPicked(imageUrls: List<String>)
        abstract fun onImageTrashed(image: MyImage)
        abstract fun onButtonDeleteConfirmClick()
    }
}