package com.furianrt.mydiary.gallery.fragments.pager

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import io.reactivex.android.schedulers.AndroidSchedulers
import org.joda.time.DateTime

class GalleryPagerPresenter(
        private val mDataManager: DataManager
) : GalleryPagerContract.Presenter() {

    private lateinit var mNoteId: String
    private var mEditedImage: MyImage? = null

    override fun onViewStart() {
        loadImages(mNoteId)
    }

    private fun loadImages(noteId: String) {
        addDisposable(mDataManager.getImagesForNote(noteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { images ->
                    if (images.isEmpty()) {
                        view?.showListImagesView(noteId)
                    } else {
                        view?.showImages(images.sortedWith(compareBy(MyImage::order, MyImage::addedTime)))
                    }
                })
    }

    override fun onButtonListModeClick() {
        view?.showListImagesView(mNoteId)
    }

    override fun setNoteId(noteId: String) {
        mNoteId = noteId
    }

    override fun onButtonDeleteClick(image: MyImage) {
        view?.showDeleteConfirmationDialog(image)
    }

    override fun onButtonDeleteConfirmClick(image: MyImage) {
        addDisposable(mDataManager.deleteImage(image)
                .andThen(mDataManager.deleteImageFromStorage(image.name))
                .ignoreElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onButtonEditClick(image: MyImage) {
        mEditedImage = image
        view?.showEditImageView(image)
    }

    override fun onImageEdited() {
        mEditedImage?.let { image ->
            addDisposable(mDataManager.getDbProfile()
                    .firstOrError()
                    .flatMapCompletable {
                        mDataManager.updateImage(image.apply {
                            fileSyncWith.clear()
                            editedTime = DateTime.now().millis
                        })
                    }
                    .subscribe())
        }
    }
}