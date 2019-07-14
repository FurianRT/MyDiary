package com.furianrt.mydiary.view.screens.main.fragments.profile.password

import com.furianrt.mydiary.usecase.auth.ChangePasswordUseCase
import com.furianrt.mydiary.usecase.auth.SignOutUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class PasswordPresenter @Inject constructor(
        private val changePassword: ChangePasswordUseCase,
        private val signOut: SignOutUseCase
) : PasswordContract.Presenter() {

    override fun onButtonCancelClick() {
        view?.returnToMenuView()
    }

    override fun onButtonSaveClick(oldPassword: String, newPassword: String, repeatPassword: String) {
        view?.clearErrorMessage()

        if (view?.isNetworkAvailable() != true) {
            view?.showErrorNetworkConnection()
        } else {
            changePassword(oldPassword, newPassword, repeatPassword)
        }
    }

    private fun changePassword(oldPassword: String, newPassword: String, repeatPassword: String) {
        view?.showLoading()
        addDisposable(changePassword.invoke(oldPassword, newPassword, repeatPassword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.hideLoading()
                    view?.showSuccessPasswordChange()
                }, { error ->
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    view?.close()
                })
    }
}