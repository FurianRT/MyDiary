package com.furianrt.mydiary.service

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface SyncContract {

    interface View : BaseView {

    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onStartCommand()

    }
}