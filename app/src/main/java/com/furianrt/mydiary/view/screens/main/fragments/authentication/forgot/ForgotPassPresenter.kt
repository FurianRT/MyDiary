/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main.fragments.authentication.forgot

import com.furianrt.mydiary.domain.send.SendPassResetEmailUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class ForgotPassPresenter @Inject constructor(
        private val sendPassResetEmail: SendPassResetEmailUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : ForgotPassContract.Presenter() {

    override fun onButtonCancelClick() {
        view?.close()
    }

    override fun onButtonSendClick(email: String) {
        view?.showLoading()
        addDisposable(sendPassResetEmail.invoke(email)
                .observeOn(scheduler.ui())
                .subscribe({
                    view?.hideLoading()
                    view?.showEmailSent()
                }, { error ->
                    view?.hideLoading()
                    when (error) {
                        is SendPassResetEmailUseCase.EmptyEmailException ->
                            view?.showErrorEmptyEmail()
                        is SendPassResetEmailUseCase.EmailFormatException ->
                            view?.showErrorEmailFormat()
                        else -> {
                            error.printStackTrace()
                            view?.showErrorNetworkConnection()
                        }
                    }
                }))
    }
}