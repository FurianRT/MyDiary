package com.furianrt.mydiary.gallery.fragments.list

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Flowable
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
        when {
            mSelectedImages.contains(image) -> {
                mSelectedImages.remove(image)
                view?.deselectItem(image)
            }
            else -> {
                mSelectedImages.add(image)
                view?.selectItem(image)
            }
        }
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
                    var i = 0
                    images.forEach { it.order = i++ }
                    view?.showImages(images, mSelectedImages)
                })
    }

    override fun onImagesOrderChange(images: List<MyImage>) {
        addDisposable(mDataManager.updateImages(images)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onMultiSelectionButtonClick() {
        view?.activateSelection()
    }

    override fun onCabDeleteButtonClick() {
        addDisposable(mDataManager.deleteImages(mSelectedImages)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.closeCab() })
    }

    override fun onCabSelectAllButtonClick() {
        addDisposable(mDataManager.getImagesForNote(mNoteId)
                .first(emptyList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { images ->
                    val list = images.toMutableList()
                    list.removeAll(mSelectedImages)
                    mSelectedImages.addAll(list)
                    view?.selectItems(list)
                })
    }

    override fun onRestoreInstanceState(selectedImages: MutableList<MyImage>?) {
        selectedImages?.let { mSelectedImages = selectedImages }
    }

    override fun onCabCloseSelection() {
        mSelectedImages.clear()
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
                .subscribe())
    }
}