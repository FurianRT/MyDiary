package com.furianrt.mydiary.gallery.fragments.list

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Flowable
import org.joda.time.DateTime

class GalleryListPresenter(private val mDataManager: DataManager) : GalleryListContract.Presenter() {

    private var mView: GalleryListContract.View? = null
    private lateinit var mNoteId: String
    private var mSelectedImages: MutableList<MyImage> = ArrayList()

    override fun attachView(view: GalleryListContract.View) {
        mView = view
    }

    override fun detachView() {
        super.detachView()
        mView = null
    }

    override fun onListItemClick(image: MyImage, position: Int, selectionActive: Boolean) {
        if (selectionActive) {
            selectListItem(image)
        } else {
            mView?.showViewImagePager(image.noteId, position)
        }
    }

    private fun selectListItem(image: MyImage) {
        when {
            mSelectedImages.contains(image) -> {
                mSelectedImages.remove(image)
                mView?.deselectItem(image)
            }
            else -> {
                mSelectedImages.add(image)
                mView?.selectItem(image)
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
                .subscribe { images ->
                    var i = 0
                    images.forEach { it.order = i++ }
                    mView?.showImages(images, mSelectedImages)
                })
    }

    override fun onImagesOrderChange(images: List<MyImage>) {
        addDisposable(mDataManager.updateImages(images)
                .subscribe())
    }

    override fun onMultiSelectionButtonClick() {
        mView?.activateSelection()
    }

    override fun onCabDeleteButtonClick() {
        addDisposable(mDataManager.deleteImages(mSelectedImages)
                .subscribe { mView?.closeCab() })
    }

    override fun onCabSelectAllButtonClick() {
        addDisposable(mDataManager.getImagesForNote(mNoteId)
                .first(emptyList())
                .subscribe { images ->
                    val list = images.toMutableList()
                    list.removeAll(mSelectedImages)
                    mSelectedImages.addAll(list)
                    mView?.selectItems(list)
                })
    }

    override fun onRestoreInstanceState(selectedImages: MutableList<MyImage>?) {
        selectedImages?.let { mSelectedImages = selectedImages }
    }

    override fun onCabCloseSelection() {
        mSelectedImages.clear()
        mView?.deactivateSelection()
    }

    override fun onSaveInstanceState() = mSelectedImages

    override fun onAddImageButtonClick() {
        mView?.requestStoragePermissions()
    }

    override fun onStoragePermissionsGranted() {
        mView?.showImageExplorer()
    }

    override fun onNoteImagesPicked(imageUrls: List<String>) {
        addDisposable(Flowable.fromIterable(imageUrls)
                .map { url ->
                    val name = mNoteId + "_" + generateUniqueId()
                    return@map MyImage(name, url, mNoteId, DateTime.now().millis)
                }
                .flatMapSingle { image -> mDataManager.saveImageToStorage(image) }
                .flatMapCompletable { savedImage -> mDataManager.insertImage(savedImage) }
                .subscribe())
    }
}