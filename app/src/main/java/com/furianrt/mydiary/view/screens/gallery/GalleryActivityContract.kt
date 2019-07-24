package com.furianrt.mydiary.view.screens.gallery

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface GalleryActivityContract {

    interface MvpView : BaseMvpView

    abstract class Presenter : BaseMvpPresenter<MvpView>()
}