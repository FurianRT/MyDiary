package com.furianrt.mydiary.main.fragments.premium

import com.furianrt.mydiary.data.DataManager

class PremiumPresenter(
        private val mDataManager: DataManager
) : PremiumContract.Presenter() {

    override fun onButtonCloseClick() {
        view?.close()
    }
}