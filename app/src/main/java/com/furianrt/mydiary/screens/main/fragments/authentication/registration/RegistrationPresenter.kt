package com.furianrt.mydiary.screens.main.fragments.authentication.registration

import android.util.Patterns
import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers

class RegistrationPresenter(
        private val dataManager: DataManager
) : RegistrationContract.Presenter() {

    companion object {
        private const val PASSWORD_MIN_LENGTH = 6
    }

    private class ProfileExistsException : Throwable()

    private var mPrevEmail = ""

    override fun onButtonCancelClick() {
        view?.close()
    }

    override fun onButtonSignUpClick(email: String, password: String, passwordRepeat: String) {
        view?.let { v ->
            if (!v.isNetworkAvailable()) {
                v.showErrorNetworkConnection()
                return
            }
            if (validateEmail(email) && validatePassword(password, passwordRepeat)) {
                v.clearEmailMessages()
                signUp(email, password)
            }
        }
    }

    override fun onEmailFocusChange(email: String, hasFocus: Boolean) {
        view?.let { v ->
            if (!hasFocus && mPrevEmail != email) {
                mPrevEmail = email
                if (!v.isNetworkAvailable()) {
                    v.showErrorNetworkConnection()
                    return
                }
                if (validateEmail(email)) {
                    v.showLoadingEmail()
                    addDisposable(dataManager.isProfileExists(email)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ exists ->
                                v.hideLoadingEmail()
                                if (exists) {
                                    v.showErrorEmailExists()
                                } else {
                                    v.showMessageCorrectEmail()
                                }
                            }, {
                                it.printStackTrace()
                                v.hideLoadingEmail()
                                v.showErrorNetworkConnection()
                            }))
                }
            }
        }
    }

    private fun signUp(email: String, password: String) {
        view?.showLoading()
        addDisposable(dataManager.isProfileExists(email)
                .flatMapCompletable { exists ->
                    if (exists) {
                        throw ProfileExistsException()
                    } else {
                        return@flatMapCompletable dataManager.signUp(email, password)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.hideLoading()
                    view?.showMessageSuccessRegistration()
                }, {
                    view?.hideLoading()
                    if (it is ProfileExistsException) {
                        view?.showErrorEmailExists()
                    } else {
                        it.printStackTrace()
                        view?.showErrorNetworkConnection()
                    }
                }))
    }

    private fun validateEmail(email: String): Boolean {
        return when {
            email.isEmpty() -> {
                view?.showErrorEmptyEmail()
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                view?.showErrorEmailFormat()
                return false
            }
            else -> true
        }
    }

    private fun validatePassword(password: String, passwordRepeat: String): Boolean {
        return when {
            password.isEmpty() -> {
                view?.showErrorEmptyPassword()
                return false
            }
            passwordRepeat.isEmpty() -> {
                view?.showErrorEmptyPasswordRepeat()
                return false
            }
            password.length < PASSWORD_MIN_LENGTH -> {
                view?.showErrorShortPassword()
                return false
            }
            password != passwordRepeat -> {
                view?.showErrorPassword()
                return false
            }
            else -> true
        }
    }
}