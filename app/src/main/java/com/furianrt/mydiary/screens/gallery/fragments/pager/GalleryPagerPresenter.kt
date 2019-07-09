package com.furianrt.mydiary.screens.gallery.fragments.pager

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import io.reactivex.android.schedulers.AndroidSchedulers
import org.joda.time.DateTime
import javax.inject.Inject

class GalleryPagerPresenter @Inject constructor(
        private val dataManager: DataManager
) : GalleryPagerContract.Presenter() {

    private var mEditedImage: MyImage? = null

    override fun onViewResume(noteId: String) {
        loadImages(noteId)
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

    override fun onButtonListModeClick(noteId: String) {
        view?.showListImagesView(noteId)
    }

    override fun onButtonDeleteClick(image: MyImage) {
        view?.showDeleteConfirmationDialog(image)
    }

    override fun onButtonEditClick(image: MyImage) {
        mEditedImage = image
        view?.showEditImageView(image)
    }

    override fun onImageEdited() {
        mEditedImage?.let {
            val image = it.copy()
            image.fileSyncWith.clear()
            image.editedTime = DateTime.now().millis
            addDisposable(dataManager.updateImage(image)
                    .subscribe())
        }
    }
}