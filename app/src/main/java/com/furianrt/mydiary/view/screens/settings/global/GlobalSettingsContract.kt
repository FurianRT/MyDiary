package com.furianrt.mydiary.view.screens.settings.global

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface GlobalSettingsContract {

    interface MvpView : BaseMvpView {
        fun showCreatePasswordView()
        fun showRemovePasswordView()
        fun showBackupEmail(email: String)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onPrefSecurityKeyClick()
        abstract fun onPasswordCreated()
        abstract fun onPasswordRemoved()
        abstract fun onViewCreate()
    }
}