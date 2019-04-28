package com.furianrt.mydiary.screens.main.fragments.profile.password

import com.furianrt.mydiary.data.DataManager
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import io.reactivex.android.schedulers.AndroidSchedulers

class PasswordPresenter(
        private val dataManager: DataManager
) : PasswordContract.Presenter() {

    companion object {
        private const val PASSWORD_MIN_LENGTH = 6
    }

    override fun onButtonCancelClick() {
        view?.returnToMenuView()
    }

    override fun onButtonSaveClick(oldPassword: String, newPassword: String, repeatPassword: String) {
        view?.clearErrorMessage()
        when {
            view?.isNetworkAvailable() != true -> view?.showErrorNetworkConnection()
            oldPassword.isEmpty() -> view?.showErrorEmptyOldPassword()
            newPassword.isEmpty() -> view?.showErrorEmptyNewPassword()
            repeatPassword.isEmpty() -> view?.showErrorEmptyRepeatPassword()
            newPassword.length < PASSWORD_MIN_LENGTH -> view?.showErrorShortNewPassword()
            newPassword != repeatPassword -> view?.showErrorWrongPasswordRepeat()
            else -> validateOldPassword(oldPassword, newPassword)
        }
    }


    private fun validateOldPassword(oldPassword: String, newPassword: String) {
        view?.showLoading()
        addDisposable(dataManager.updatePassword(oldPassword, newPassword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.hideLoading()
                    view?.showSuccessPasswordChange()
                }, {
                    when (it) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            view?.hideLoading()
                            view?.showErrorWrongOldPassword()
                        }
                        is FirebaseAuthInvalidUserException -> {
                            addDisposable(dataManager.signOut() //todo вынужденный Disposable в subscribe.
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        view?.hideLoading()
                                        view?.close()
                                    })
                        }
                        else -> {
                            it.printStackTrace()
                            view?.hideLoading()
                            view?.showErrorNetworkConnection()
                        }
                    }
                }))
    }
}