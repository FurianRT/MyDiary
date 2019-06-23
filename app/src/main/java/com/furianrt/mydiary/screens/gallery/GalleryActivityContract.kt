package com.furianrt.mydiary.screens.gallery

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface GalleryActivityContract {

    interface MvpView : BaseMvpView {

    }

    abstract class Presenter : BasePresenter<MvpView>() {

    }
}