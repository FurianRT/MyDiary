package com.furianrt.mydiary.services.sync

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.ProgressMessage

interface SyncContract {

    interface View : BaseView {
        fun close()
        fun sendProgressUpdate(progressMessage: ProgressMessage)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onStartCommand()
    }
}