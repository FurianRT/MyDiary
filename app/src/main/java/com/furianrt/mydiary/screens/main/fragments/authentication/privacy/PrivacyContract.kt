package com.furianrt.mydiary.screens.main.fragments.authentication.privacy

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

interface PrivacyContract {

    interface MvpView : BaseMvpView {
        fun showLoading()
        fun hideLoading()
        fun showMessageSuccessRegistration()
        fun showErrorNetworkConnection()
        fun close()
        fun openLink(link: String)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonAcceptClick(email: String, password: String)
        abstract fun onButtonCancelClick()
        abstract fun onPrivacyLinkClick()
    }
}