/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.authentication.login

import com.furianrt.mydiary.domain.auth.SignInUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class LoginPresenter @Inject constructor(
        private val signIn: SignInUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : LoginContract.Presenter() {

    override fun onButtonForgotClick(email: String) {
        view?.showForgotPassView(email)
    }

    override fun onButtonSignInClick(email: String, password: String) {
        signIn(email, password)
    }

    private fun signIn(email: String, password: String) {
        view?.showLoading()
        addDisposable(signIn.invoke(email, password)
                .observeOn(scheduler.ui())
                .subscribe({
                    view?.hideLoading()
                    view?.showLoginSuccess()
                }, { error ->
                    view?.hideLoading()
                    when (error) {
                        is SignInUseCase.EmptyEmailException -> view?.showErrorEmptyEmail()
                        is SignInUseCase.EmptyPasswordException -> view?.showErrorEmptyPassword()
                        is SignInUseCase.InvalidCredentialsException -> view?.showErrorWrongCredential()
                        else -> {
                            error.printStackTrace()
                            view?.showErrorNetworkConnection()
                        }
                    }
                }))
    }
}