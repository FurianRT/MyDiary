/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.authentication.privacy

import com.furianrt.mydiary.domain.auth.SignUpUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class PrivacyPresenter @Inject constructor(
        private val signUpUseCase: SignUpUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : PrivacyContract.Presenter() {

    companion object {
        private const val PRIVACY_LINK = "https://docs.google.com/document/d/1jW6ik5s4digfAURnKo48kx_oXUryP7QOc-mFNBstxtA/edit?usp=sharing/"
    }

    override fun onButtonAcceptClick(email: String, password: String) {
        view?.showLoading()
        addDisposable(signUpUseCase(email, password)
                .observeOn(scheduler.ui())
                .subscribe({
                    view?.hideLoading()
                    view?.showMessageSuccessRegistration()
                }, {
                    view?.hideLoading()
                    it.printStackTrace()
                    view?.showErrorNetworkConnection()
                }))
    }

    override fun onButtonCancelClick() {
        view?.close()
    }

    override fun onPrivacyLinkClick() {
        view?.openLink(PRIVACY_LINK)
    }
}