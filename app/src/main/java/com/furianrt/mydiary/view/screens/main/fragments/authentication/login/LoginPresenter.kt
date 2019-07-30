/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main.fragments.authentication.login

import com.furianrt.mydiary.domain.auth.SignInUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class LoginPresenter @Inject constructor(
        private val signIn: SignInUseCase
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
                .observeOn(AndroidSchedulers.mainThread())
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