package com.furianrt.mydiary.gallery.fragments.list

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import io.reactivex.disposables.CompositeDisposable

class GalleryListPresenter(private val mDataManager: DataManager) : GalleryListContract.Presenter {

    private var mView: GalleryListContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var mNoteId: String

    override fun attachView(view: GalleryListContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }

    override fun onListItemClick(image: MyImage, position: Int) {
        mView?.showViewImagePager(image.noteId, position)
    }

    override fun setNoteId(noteId: String) {
        mNoteId = noteId
    }

    override fun onViewCreate() {
        loadImages(mNoteId)
    }

    private fun loadImages(noteId: String) {
        val disposable = mDataManager.getImagesForNote(noteId)
                .subscribe { images ->
                    var i = 0
                    images.forEach { it.order = i++ }
                    mView?.showImages(images) }

        mCompositeDisposable.add(disposable)
    }

    override fun onStop(images: List<MyImage>) {
        val disposable = mDataManager.updateImages(images)
                .subscribe()

        mCompositeDisposable.add(disposable)
    }
}