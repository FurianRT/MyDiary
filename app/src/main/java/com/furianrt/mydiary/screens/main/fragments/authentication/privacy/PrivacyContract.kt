package com.furianrt.mydiary.screens.main.fragments.authentication.privacy

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface PrivacyContract {

    interface MvpView : BaseMvpView {
        fun showLoading()
        fun hideLoading()
        fun showMessageSuccessRegistration()
        fun showErrorNetworkConnection()
        fun close()
        fun openLink(link: String)
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonAcceptClick(email: String, password: String)
        abstract fun onButtonCancelClick()
        abstract fun onPrivacyLinkClick()
    }
}