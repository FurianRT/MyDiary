package com.furianrt.mydiary.view.screens.main.fragments.authentication

import javax.inject.Inject

class AuthPresenter @Inject constructor() : AuthContract.Presenter() {

    override fun onButtonCloseClick() {
        view?.closeSheet()
    }

    override fun onButtonCreateAccountClick() {
        view?.showRegistrationView()
    }
}