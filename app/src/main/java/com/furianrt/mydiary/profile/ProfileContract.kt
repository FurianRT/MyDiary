package com.furianrt.mydiary.profile

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface ProfileContract {

    interface View : BaseView {

    }

    abstract class Presenter : BasePresenter<View>() {

    }
}