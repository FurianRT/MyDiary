package com.furianrt.mydiary.gallery.fragments.pager

import com.furianrt.mydiary.data.DataManager
import io.reactivex.disposables.CompositeDisposable

class GalleryPagerPresenter(
        private val mDataManager: DataManager
) : GalleryPagerContract.Presenter {

    private var mView: GalleryPagerContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var mNoteId: String

    override fun attachView(view: GalleryPagerContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }

    override fun onViewCreate() {
        loadImages(mNoteId)
    }

    private fun loadImages(noteId: String) {
        val disposable = mDataManager.getImagesForNote(noteId)
                .subscribe { images -> mView?.showImages(images) }

        mCompositeDisposable.add(disposable)
    }

    override fun onListModeButtonClick() {
        mView?.showListImagesView(mNoteId)
    }

    override fun setNoteId(noteId: String) {
        mNoteId = noteId
    }
}