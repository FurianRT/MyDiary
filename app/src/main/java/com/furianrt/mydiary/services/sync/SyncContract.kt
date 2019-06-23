package com.furianrt.mydiary.services.sync

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.data.model.SyncProgressMessage

interface SyncContract {

    interface MvpView : BaseMvpView {
        fun close()
        fun sendProgressUpdate(progressMessage: SyncProgressMessage)
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onStartCommand()
    }
}