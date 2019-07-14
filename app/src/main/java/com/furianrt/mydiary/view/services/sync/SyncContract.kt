package com.furianrt.mydiary.view.services.sync

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter
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