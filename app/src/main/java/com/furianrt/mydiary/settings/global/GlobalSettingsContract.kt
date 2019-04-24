package com.furianrt.mydiary.settings.global

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface GlobalSettingsContract {

    interface View : BaseView {
        fun showCreatePasswordView()
        fun showRemovePasswordView()
        fun showBackupEmail(email: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onPrefSecurityKeyClick()
        abstract fun onPasswordCreated()
        abstract fun onPasswordRemoved()
        abstract fun onViewCreate()
    }
}