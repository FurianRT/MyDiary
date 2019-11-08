/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.authentication.registration

import com.furianrt.mydiary.domain.check.CheckCredentialsUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class RegistrationPresenter @Inject constructor(
        private val checkCredentials: CheckCredentialsUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : RegistrationContract.Presenter() {

    private var mPrevEmail = ""

    override fun onButtonCancelClick() {
        view?.close()
    }

    override fun onButtonSignUpClick(email: String, password: String, passwordRepeat: String) {
        view?.clearEmailMessages()
        view?.showLoading()
        addDisposable(checkCredentials.invoke(email, password, passwordRepeat)
                .observeOn(scheduler.ui())
                .subscribe({
                    view?.hideLoading()
                    view?.showPrivacyView(email, password)
                }, { error ->
                    view?.hideLoading()
                    when (error) {
                        is CheckCredentialsUseCase.EmailFormatException ->
                            view?.showErrorEmailFormat()
                        is CheckCredentialsUseCase.EmptyEmailException ->
                            view?.showErrorEmptyEmail()
                        is CheckCredentialsUseCase.EmailExistException ->
                            view?.showErrorEmailExists()
                        is CheckCredentialsUseCase.ShortPasswordException ->
                            view?.showErrorShortPassword()
                        is CheckCredentialsUseCase.EmptyPasswordException ->
                            view?.showErrorEmptyPassword()
                        is CheckCredentialsUseCase.EmptyPasswordRepeatException ->
                            view?.showErrorEmptyPasswordRepeat()
                        is CheckCredentialsUseCase.PasswordNotMatchException ->
                            view?.showErrorPassword()
                        else -> {
                            error.printStackTrace()
                            view?.showErrorNetworkConnection()
                        }
                    }
                }))
    }

    override fun onEmailFocusChange(email: String, hasFocus: Boolean) {
        if (!hasFocus && mPrevEmail != email) {
            mPrevEmail = email
            view?.showLoadingEmail()
            addDisposable(checkCredentials.invoke(email)
                    .observeOn(scheduler.ui())
                    .subscribe({
                        view?.hideLoadingEmail()
                        view?.showMessageCorrectEmail()
                    }, { error ->
                        view?.hideLoadingEmail()
                        when (error) {
                            is CheckCredentialsUseCase.EmailFormatException ->
                                view?.showErrorEmailFormat()
                            is CheckCredentialsUseCase.EmptyEmailException ->
                                view?.showErrorEmptyEmail()
                            is CheckCredentialsUseCase.EmailExistException ->
                                view?.showErrorEmailExists()
                            else -> {
                                error.printStackTrace()
                                view?.showErrorNetworkConnection()
                            }
                        }

                    }))
        }
    }
}