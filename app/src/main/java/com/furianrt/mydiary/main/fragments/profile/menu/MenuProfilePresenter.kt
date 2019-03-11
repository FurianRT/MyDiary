package com.furianrt.mydiary.main.fragments.profile.menu

import com.furianrt.mydiary.data.DataManager

class MenuProfilePresenter(
        private val mDataManager: DataManager
) : MenuProfileContract.Presenter() {

    override fun onButtonSignOutClick() {
        view?.showSignOutView()
    }

    override fun onButtonChangePasswordClick() {
        view?.showPasswordView()
    }

    override fun onButtonAboutClick() {
        view?.showAboutView()
    }
}