package com.furianrt.mydiary.authentication

import com.furianrt.mydiary.data.DataManager

class AuthPresenter(
        private val mDataManager: DataManager
) : AuthContract.Presenter() {

    override fun onButtonCloseClick() {
        view?.closeSheet()
    }

    override fun onButtonCreateAccountClick() {
        view?.showRegistrationView()
    }
}