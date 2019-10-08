/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main.fragments.profile.password

import com.furianrt.mydiary.domain.auth.ChangePasswordUseCase
import com.furianrt.mydiary.domain.auth.SignOutUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class PasswordPresenter @Inject constructor(
        private val changePassword: ChangePasswordUseCase,
        private val signOut: SignOutUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : PasswordContract.Presenter() {

    override fun onButtonCancelClick() {
        view?.returnToMenuView()
    }

    override fun onButtonSaveClick(oldPassword: String, newPassword: String, repeatPassword: String) {
        view?.clearErrorMessage()
        changePassword(oldPassword, newPassword, repeatPassword)

    }

    private fun changePassword(oldPassword: String, newPassword: String, repeatPassword: String) {
        view?.showLoading()
        addDisposable(changePassword.invoke(oldPassword, newPassword, repeatPassword)
                .observeOn(scheduler.ui())
                .subscribe({
                    view?.hideLoading()
                    view?.showSuccessPasswordChange()
                }, { error ->
                    view?.hideLoading()
                    when (error) {
                        is ChangePasswordUseCase.EmptyNewPasswordRepeatException ->
                            view?.showErrorEmptyNewPassword()
                        is ChangePasswordUseCase.EmptyOldPasswordException ->
                            view?.showErrorEmptyOldPassword()
                        is ChangePasswordUseCase.EmptyRepeatPasswordRepeatException ->
                            view?.showErrorEmptyRepeatPassword()
                        is ChangePasswordUseCase.ShortNewPasswordException ->
                            view?.showErrorShortNewPassword()
                        is ChangePasswordUseCase.WrongPasswordRepeatException ->
                            view?.showErrorWrongPasswordRepeat()
                        is ChangePasswordUseCase.WrongOldPasswordException ->
                            view?.showErrorWrongOldPassword()
                        is ChangePasswordUseCase.InvalidUserExceptionException ->
                            signOut()
                        else -> {
                            error.printStackTrace()
                            view?.showErrorNetworkConnection()
                        }
                    }
                }))
    }

    private fun signOut() {
        addDisposable(signOut.invoke()
                .observeOn(scheduler.ui())
                .subscribe {
                    view?.close()
                })
    }
}