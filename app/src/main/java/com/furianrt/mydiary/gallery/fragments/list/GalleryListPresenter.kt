package com.furianrt.mydiary.gallery.fragments.list

import android.util.Log
import com.furianrt.mydiary.LOG_TAG
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import io.reactivex.disposables.CompositeDisposable

class GalleryListPresenter(private val mDataManager: DataManager) : GalleryListContract.Presenter {

    private var mView: GalleryListContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var mNoteId: String
    private var mSelectedImages: MutableList<MyImage> = ArrayList()

    override fun attachView(view: GalleryListContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
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
        val disposable = mDataManager.getImagesForNote(noteId)
                .subscribe { images ->
                    var i = 0
                    images.forEach { it.order = i++ }
                    mView?.showImages(images, mSelectedImages)
                }

        mCompositeDisposable.add(disposable)
    }

    override fun onImagesOrderChange(images: List<MyImage>) {
        val disposable = mDataManager.updateImages(images)
                .subscribe()

        mCompositeDisposable.add(disposable)
    }

    override fun onMultiSelectionButtonClick() {
        mView?.activateSelection()
    }

    override fun onCabDeleteButtonClick() {
        Log.e(LOG_TAG, mSelectedImages.toString())
        val disposable = mDataManager.deleteImages(mSelectedImages)
                .subscribe {
                    mView?.closeCab()
                }

        mCompositeDisposable.add(disposable)
    }

    override fun onCabSelectAllButtonClick() {
        val disposable = mDataManager.getImagesForNote(mNoteId)
                .first(emptyList())
                .subscribe { images ->
                    val list = images.toMutableList()
                    list.removeAll(mSelectedImages)
                    mSelectedImages.addAll(list)
                    mView?.selectItems(list)
                }
        mCompositeDisposable.add(disposable)
    }

    override fun onRestoreInstanceState(selectedImages: MutableList<MyImage>?) {
        selectedImages?.let { mSelectedImages = selectedImages }
    }

    override fun onCabCloseSelection() {
        mSelectedImages.clear()
        mView?.deactivateSelection()
    }

    override fun onSaveInstanceState() = mSelectedImages
}