package com.furianrt.mydiary.services.sync

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface SyncContract {

    interface View : BaseView {
        fun close()
        fun sendProgressUpdate(progressMessage: ProgressMessage)

    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onStartCommand()

    }
}