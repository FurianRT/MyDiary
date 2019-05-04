package com.furianrt.mydiary.screens.gallery.fragments.list

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.joda.time.DateTime

class GalleryListPresenter(
        private val dataManager: DataManager
) : GalleryListContract.Presenter() {

    private lateinit var mNoteId: String
    private var mSelectedImages: MutableList<MyImage> = ArrayList()

    override fun onListItemClick(image: MyImage, position: Int, selectionActive: Boolean) {
        if (selectionActive) {
            selectListItem(image)
        } else {
            view?.showViewImagePager(image.noteId, position)
        }
    }

    private fun selectListItem(image: MyImage) {
        val foundedImage = mSelectedImages.find { it.name == image.name }
        if (foundedImage != null) {
            mSelectedImages.remove(foundedImage)
            view?.deselectImage(image)
        } else {
            mSelectedImages.add(image)
            view?.selectImage(image)
        }
        view?.showSelectedImageCount(mSelectedImages.size)
    }

    override fun setNoteId(noteId: String) {
        mNoteId = noteId
    }

    override fun onViewStart() {
        loadImages(mNoteId)
    }

    private fun loadImages(noteId: String) {
        addDisposable(dataManager.getImagesForNote(noteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { images ->
                    view?.showSelectedImageCount(mSelectedImages.size)
                    if (images.isEmpty()) {
                        view?.closeCab()
                        view?.showEmptyList()
                    } else {
                        var i = 0
                        view?.showImages(images
                                .sortedWith(compareBy(MyImage::order, MyImage::addedTime))
                                .apply { forEach { it.order = i++ } }, mSelectedImages)
                    }
                })
    }

    override fun onImagesOrderChange(images: List<MyImage>) {
        addDisposable(dataManager.updateImage(images)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onButtonMultiSelectionClick() {
        view?.activateSelection()
        view?.showSelectedImageCount(mSelectedImages.size)
    }

    override fun onButtonCabDeleteClick() {
        if (mSelectedImages.isEmpty()) {
            view?.closeCab()
        } else {
            view?.showDeleteConfirmationDialog(mSelectedImages)
        }
    }

    override fun onButtonDeleteConfirmClick(images: List<MyImage>) {
        addDisposable(dataManager.deleteImage(images)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    images.forEach { image -> mSelectedImages.removeAll { it.name == image.name } }
                    view?.showSelectedImageCount(mSelectedImages.size)
                    view?.closeCab()
                })
    }

    override fun onButtonCabSelectAllClick() {
        addDisposable(dataManager.getImagesForNote(mNoteId)
                .first(emptyList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { images ->
                    mSelectedImages.clear()
                    mSelectedImages.addAll(images)
                    view?.showSelectedImageCount(mSelectedImages.size)
                    view?.selectImages(mSelectedImages)
                })
    }

    override fun onRestoreInstanceState(selectedImages: MutableList<MyImage>?) {
        selectedImages?.let { mSelectedImages = selectedImages }
    }

    override fun onCabCloseSelection() {
        mSelectedImages.clear()
        view?.showSelectedImageCount(mSelectedImages.size)
        view?.deactivateSelection()
    }

    override fun onSaveInstanceState() = mSelectedImages

    override fun onButtonAddImageClick() {
        view?.requestStoragePermissions()
    }

    override fun onStoragePermissionsGranted() {
        view?.showImageExplorer()
    }

    override fun onNoteImagesPicked(imageUrls: List<String>) {
        addDisposable(Flowable.fromIterable(imageUrls)
                .map { url ->
                    val name = mNoteId + "_" + generateUniqueId()
                    return@map MyImage(name, url, mNoteId, DateTime.now().millis)
                }
                .flatMapSingle { image -> dataManager.saveImageToStorage(image) }
                .flatMapSingle { savedImage ->
                    dataManager.insertImage(savedImage).toSingleDefault(true)
                }
                .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                .ignoreElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.hideLoading() })
    }

    override fun onImageTrashed(image: MyImage) {
        view?.showDeleteConfirmationDialog(listOf(image))
    }
}