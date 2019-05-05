package com.furianrt.mydiary.screens.gallery.fragments.pager

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import io.reactivex.android.schedulers.AndroidSchedulers
import org.joda.time.DateTime

class GalleryPagerPresenter(
        private val dataManager: DataManager
) : GalleryPagerContract.Presenter() {

    private lateinit var mNoteId: String
    private var mEditedImage: MyImage? = null

    override fun onViewStart() {
        loadImages(mNoteId)
    }

    private fun loadImages(noteId: String) {
        addDisposable(dataManager.getImagesForNote(noteId)
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

    override fun onButtonEditClick(image: MyImage) {
        mEditedImage = image
        view?.showEditImageView(image)
    }

    override fun onImageEdited() {
        mEditedImage?.let { image ->
            addDisposable(dataManager.getDbProfile()
                    .firstOrError()
                    .flatMapCompletable {
                        dataManager.updateImage(image.apply {
                            fileSyncWith.clear()
                            editedTime = DateTime.now().millis
                        })
                    }
                    .subscribe())
        }
    }
}