package com.furianrt.mydiary.services.sync

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.model.SyncProgressMessage

interface SyncContract {

    interface MvpView : BaseMvpView {
        fun close()
        fun sendProgressUpdate(progressMessage: SyncProgressMessage)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onStartCommand()
    }
}