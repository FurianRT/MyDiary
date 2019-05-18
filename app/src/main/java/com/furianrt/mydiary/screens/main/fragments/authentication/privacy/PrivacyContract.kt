package com.furianrt.mydiary.screens.main.fragments.authentication.privacy

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface PrivacyContract {

    interface View : BaseView {
        fun showLoading()
        fun hideLoading()
        fun showMessageSuccessRegistration()
        fun showErrorNetworkConnection()
        fun close()
        fun openLink(link: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonAcceptClick(email: String, password: String)
        abstract fun onButtonCancelClick()
        abstract fun onPrivacyLinkClick()
    }
}