package com.furianrt.mydiary.view.screens.gallery.fragments.list

import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.domain.save.SaveImagesUseCase
import com.furianrt.mydiary.domain.get.GetImagesUseCase
import com.furianrt.mydiary.domain.update.UpdateImageUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class GalleryListPresenter @Inject constructor(
        private val getImages: GetImagesUseCase,
        private val updateImage: UpdateImageUseCase,
        private val saveImages: SaveImagesUseCase
) : GalleryListContract.Presenter() {

    private lateinit var mNoteId: String
    private var mSelectedImageNames = HashSet<String>()

    override fun init(noteId: String) {
        mNoteId = noteId
    }

    override fun attachView(view: GalleryListContract.MvpView) {
        super.attachView(view)
        loadImages(mNoteId)
    }

    override fun onListItemClick(image: MyImage, position: Int, selectionActive: Boolean) {
        if (selectionActive) {
            selectListItem(image.name)
        } else {
            view?.showViewImagePager(image.noteId, position)
        }
    }

    private fun selectListItem(imageName: String) {
        if (mSelectedImageNames.contains(imageName)) {
            mSelectedImageNames.remove(imageName)
            view?.deselectImage(imageName)
        } else {
            mSelectedImageNames.add(imageName)
            view?.selectImage(imageName)
        }
        view?.showSelectedImageCount(mSelectedImageNames.size)
    }

    private fun loadImages(noteId: String) {
        addDisposable(getImages.invoke(noteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { images ->
                    view?.showSelectedImageCount(mSelectedImageNames.size)
                    if (images.isEmpty()) {
                        view?.closeCab()
                        view?.showEmptyList()
                    } else {
                        var i = 0
                        view?.showImages(images.apply { forEach { it.order = i++ } }, mSelectedImageNames)
                    }
                })
    }

    override fun onImagesOrderChange(images: List<MyImage>) {
        addDisposable(updateImage.invoke(images)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onButtonMultiSelectionClick() {
        view?.activateSelection()
        view?.showSelectedImageCount(mSelectedImageNames.size)
    }

    override fun onButtonCabDeleteClick() {
        if (mSelectedImageNames.isEmpty()) {
            view?.closeCab()
        } else {
            view?.showDeleteConfirmationDialog(mSelectedImageNames.toList())
        }
    }

    override fun onButtonDeleteConfirmClick() {
        mSelectedImageNames.clear()
        view?.closeCab()
    }

    override fun onButtonCabSelectAllClick() {
        addDisposable(getImages.invoke(mNoteId)
                .first(emptyList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { images ->
                    mSelectedImageNames.clear()
                    mSelectedImageNames.addAll(images.map { it.name })
                    view?.showSelectedImageCount(mSelectedImageNames.size)
                    view?.selectImages(mSelectedImageNames)
                })
    }

    override fun onRestoreInstanceState(selectedImageNames: Set<String>?) {
        selectedImageNames?.let {
            mSelectedImageNames.clear()
            mSelectedImageNames.addAll(it)
        }
    }

    override fun onCabCloseSelection() {
        mSelectedImageNames.clear()
        view?.showSelectedImageCount(mSelectedImageNames.size)
        view?.deactivateSelection()
    }

    override fun onSaveInstanceState() = mSelectedImageNames

    override fun onButtonAddImageClick() {
        view?.requestStoragePermissions()
    }

    override fun onStoragePermissionsGranted() {
        view?.showImageExplorer()
    }

    override fun onNoteImagesPicked(imageUrls: List<String>) {
        addDisposable(saveImages.invoke(mNoteId, imageUrls)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.hideLoading()
                }, { error ->
                    error.printStackTrace()
                    view?.showErrorSaveImage()
                }))
    }

    override fun onImageTrashed(image: MyImage) {
        view?.showDeleteConfirmationDialog(listOf(image.name))
    }
}