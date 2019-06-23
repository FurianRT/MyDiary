package com.furianrt.mydiary.screens.gallery

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

interface GalleryActivityContract {

    interface MvpView : BaseMvpView {

    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {

    }
}