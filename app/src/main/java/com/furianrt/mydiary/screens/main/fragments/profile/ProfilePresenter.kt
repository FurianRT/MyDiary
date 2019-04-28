package com.furianrt.mydiary.screens.main.fragments.profile

import com.furianrt.mydiary.data.DataManager

class ProfilePresenter(
        private val dataManager: DataManager
) : ProfileContract.Presenter() {

    override fun onButtonCloseClick() {
        view?.close()
    }
}