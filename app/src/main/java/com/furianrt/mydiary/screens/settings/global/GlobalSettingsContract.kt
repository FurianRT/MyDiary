package com.furianrt.mydiary.screens.settings.global

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

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