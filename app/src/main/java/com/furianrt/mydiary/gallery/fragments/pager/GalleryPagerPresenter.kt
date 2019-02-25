package com.furianrt.mydiary.gallery.fragments.pager

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import io.reactivex.android.schedulers.AndroidSchedulers

class GalleryPagerPresenter(
        private val mDataManager: DataManager
) : GalleryPagerContract.Presenter() {

    private lateinit var mNoteId: String

    override fun onViewStart() {
        loadImages(mNoteId)
    }

    private fun loadImages(noteId: String) {
        addDisposable(mDataManager.getImagesForNote(noteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { images ->
                    view?.showImages(images.sortedWith(compareBy(MyImage::order, MyImage::addedTime)))
                })
    }

    override fun onListModeButtonClick() {
        view?.showListImagesView(mNoteId)
    }

    override fun setNoteId(noteId: String) {
        mNoteId = noteId
    }
}