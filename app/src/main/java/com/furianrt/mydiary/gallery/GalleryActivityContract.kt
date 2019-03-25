package com.furianrt.mydiary.gallery

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface GalleryActivityContract {

    interface View : BaseView {

    }

    abstract class Presenter : BasePresenter<View>() {

    }
}