/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.pin.fragments.sendemail

import com.furianrt.mydiary.domain.send.SendPinResetEmailUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class SendEmailPresenter @Inject constructor(
        private val sendPinResetEmail: SendPinResetEmailUseCase
) : SendEmailContract.Presenter() {

    override fun onButtonSendClick() {
        view?.showLoading()
        addDisposable(sendPinResetEmail.invoke()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.hideLoading()
                    view?.showDoneView()
                }, { error ->
                    view?.hideLoading()
                    if (error is SendPinResetEmailUseCase.NetworkNotAvailableException) {
                        view?.showErrorNetworkConnection()
                    } else {
                        error.printStackTrace()
                        view?.showErrorMessageSend()
                    }
                }))
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}