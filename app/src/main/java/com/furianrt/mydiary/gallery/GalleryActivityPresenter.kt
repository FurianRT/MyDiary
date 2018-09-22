package com.furianrt.mydiary.gallery

import com.furianrt.mydiary.data.DataManager
import io.reactivex.disposables.CompositeDisposable

class GalleryActivityPresenter(private val mDataManager: DataManager)
    : GalleryActivityContract.Presenter {

    private var mView: GalleryActivityContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    override fun attachView(view: GalleryActivityContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }
}