package com.furianrt.mydiary.screens.gallery.fragments.pager

import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.domain.get.GetImagesUseCase
import com.furianrt.mydiary.domain.update.UpdateImageUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import org.joda.time.DateTime
import javax.inject.Inject

class GalleryPagerPresenter @Inject constructor(
        private val getImages: GetImagesUseCase,
        private val updateImage: UpdateImageUseCase
) : GalleryPagerContract.Presenter() {

    private var mEditedImage: MyImage? = null
    private lateinit var mNoteId: String

    override fun init(noteId: String) {
        mNoteId = noteId
    }

    override fun attachView(view: GalleryPagerContract.MvpView) {
        super.attachView(view)
        loadImages(mNoteId)
    }

    private fun loadImages(noteId: String) {
        addDisposable(getImages.invoke(noteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { images ->
                    if (images.isEmpty()) {
                        view?.showListImagesView(noteId)
                    } else {
                        view?.showImages(images)
                    }
                })
    }

    override fun onButtonListModeClick() {
        view?.showListImagesView(mNoteId)
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
            addDisposable(updateImage.invoke(image)
                    .subscribe())
        }
    }
}