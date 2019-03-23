package com.furianrt.mydiary.gallery.fragments.list

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.joda.time.DateTime

class GalleryListPresenter(private val mDataManager: DataManager) : GalleryListContract.Presenter() {

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
        addDisposable(mDataManager.getImagesForNote(noteId)
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
        addDisposable(mDataManager.updateImages(images)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onMultiSelectionButtonClick() {
        view?.activateSelection()
        view?.showSelectedImageCount(mSelectedImages.size)
    }

    override fun onCabDeleteButtonClick() {
        if (mSelectedImages.isEmpty()) {
            view?.closeCab()
            return
        }
        addDisposable(mDataManager.deleteImage(mSelectedImages)
                .andThen(Observable.fromIterable(mSelectedImages))
                .flatMapSingle { mDataManager.deleteImageFromStorage(it.name) }
                .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                .ignoreElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mSelectedImages.clear()
                    view?.showSelectedImageCount(mSelectedImages.size)
                    view?.closeCab()
                })
    }

    override fun onCabSelectAllButtonClick() {
        addDisposable(mDataManager.getImagesForNote(mNoteId)
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

    override fun onAddImageButtonClick() {
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
                .flatMapSingle { image -> mDataManager.saveImageToStorage(image) }
                .flatMapCompletable { savedImage -> mDataManager.insertImage(savedImage) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.hideLoading() })
    }

    override fun onImageDeleted(image: MyImage) {
        mSelectedImages.remove(image)
        addDisposable(mDataManager.deleteImage(image)
                .andThen(mDataManager.deleteImageFromStorage(image.name))
                .ignoreElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    view?.showSelectedImageCount(mSelectedImages.size)
                })
    }
}