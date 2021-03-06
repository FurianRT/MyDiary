/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.pin.fragments.sendemail

import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.domain.send.SendPinResetEmailUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class SendEmailPresenter @Inject constructor(
        private val sendPinResetEmailUseCase: SendPinResetEmailUseCase,
        private val analytics: MyAnalytics,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : SendEmailContract.Presenter() {

    override fun onButtonSendClick() {
        view?.showLoading()
        addDisposable(sendPinResetEmailUseCase()
                .observeOn(scheduler.ui())
                .subscribe({
                    view?.hideLoading()
                    view?.showDoneView()
                }, { error ->
                    view?.hideLoading()
                    if (error is SendPinResetEmailUseCase.NetworkNotAvailableException) {
                        view?.showErrorNetworkConnection()
                    } else {
                        error.printStackTrace()
                        analytics.logExceptionEvent(error)
                        view?.showErrorMessageSend()
                    }
                }))
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}