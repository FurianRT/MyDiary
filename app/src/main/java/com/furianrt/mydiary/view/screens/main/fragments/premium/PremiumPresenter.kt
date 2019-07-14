package com.furianrt.mydiary.view.screens.main.fragments.premium

import javax.inject.Inject

class PremiumPresenter @Inject constructor() : PremiumContract.Presenter() {

    override fun onButtonCloseClick() {
        view?.close()
    }
}