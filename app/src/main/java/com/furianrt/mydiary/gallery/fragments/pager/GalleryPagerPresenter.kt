package com.furianrt.mydiary.gallery.fragments.pager

import com.furianrt.mydiary.data.DataManager

class GalleryPagerPresenter(
        private val mDataManager: DataManager
) : GalleryPagerContract.Presenter() {

    private var mView: GalleryPagerContract.View? = null
    private lateinit var mNoteId: String

    override fun attachView(view: GalleryPagerContract.View) {
        mView = view
    }

    override fun detachView() {
        super.detachView()
        mView = null
    }

    override fun onViewStart() {
        loadImages(mNoteId)
    }

    private fun loadImages(noteId: String) {
        addDisposable(mDataManager.getImagesForNote(noteId)
                .subscribe { images -> mView?.showImages(images) })
    }

    override fun onListModeButtonClick() {
        mView?.showListImagesView(mNoteId)
    }

    override fun setNoteId(noteId: String) {
        mNoteId = noteId
    }
}