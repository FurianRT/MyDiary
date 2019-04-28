package com.furianrt.mydiary.screens.main.fragments.premium

import com.furianrt.mydiary.data.DataManager

class PremiumPresenter(
        private val dataManager: DataManager
) : PremiumContract.Presenter() {

    override fun onButtonCloseClick() {
        view?.close()
    }
}