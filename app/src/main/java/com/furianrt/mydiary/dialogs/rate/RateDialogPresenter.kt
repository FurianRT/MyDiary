package com.furianrt.mydiary.dialogs.rate

import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.domain.save.SetNeedRateOfferUseCase
import javax.inject.Inject

class RateDialogPresenter @Inject constructor(
        private val setNeedRateOffer: SetNeedRateOfferUseCase
) : RateDialogContract.Presenter() {

    override fun onButtonRateClick(rating: Int) {
        if (rating > 3) {
            view?.openAppPage()
        } else {
            view?.sendEmailToSupport(BuildConfig.SUPPORT_EMAIL)
        }
        setNeedRateOffer.invoke(false)
        view?.close()
    }

    override fun onButtonLaterClick() {
        setNeedRateOffer.invoke(true)
        view?.close()
    }

    override fun onButtonNeverClick() {
        setNeedRateOffer.invoke(false)
        view?.close()
    }
}