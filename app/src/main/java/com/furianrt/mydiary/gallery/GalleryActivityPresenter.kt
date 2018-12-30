package com.furianrt.mydiary.gallery

import com.furianrt.mydiary.data.DataManager

class GalleryActivityPresenter(private val mDataManager: DataManager)
    : GalleryActivityContract.Presenter {

    private var mView: GalleryActivityContract.View? = null

    override fun attachView(view: GalleryActivityContract.View) {
        mView = view
    }

    override fun detachView() {
        super.detachView()
        mView = null
    }
}