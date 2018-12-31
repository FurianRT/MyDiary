package com.furianrt.mydiary.gallery

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface GalleryActivityContract {

    interface View : BaseView {

    }

    abstract class Presenter : BasePresenter<View>() {

    }
}