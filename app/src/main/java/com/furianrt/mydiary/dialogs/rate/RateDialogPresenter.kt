package com.furianrt.mydiary.dialogs.rate

import com.furianrt.mydiary.data.DataManager
import javax.inject.Inject

class RateDialogPresenter @Inject constructor(
        private val dataManager: DataManager
) : RateDialogContract.Presenter() {

    override fun onButtonRateClick(rating: Int) {
        if (rating > 3) {
            view?.openAppPage()
        } else {
            view?.sendEmailToSupport()
        }
        dataManager.setNumberOfLaunches(10)
        view?.close()
    }

    override fun onButtonLaterClick() {
        dataManager.setNumberOfLaunches(0)
        view?.close()
    }

    override fun onButtonNeverClick() {
        dataManager.setNumberOfLaunches(10)
        view?.close()
    }
}