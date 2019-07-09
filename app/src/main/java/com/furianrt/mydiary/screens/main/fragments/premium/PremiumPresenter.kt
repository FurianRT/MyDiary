package com.furianrt.mydiary.screens.main.fragments.premium

import com.furianrt.mydiary.data.DataManager
import javax.inject.Inject

class PremiumPresenter @Inject constructor(
        private val dataManager: DataManager
) : PremiumContract.Presenter() {

    override fun onButtonCloseClick() {
        view?.close()
    }
}