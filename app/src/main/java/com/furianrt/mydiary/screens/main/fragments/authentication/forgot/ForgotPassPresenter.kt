package com.furianrt.mydiary.screens.main.fragments.authentication.forgot

import android.util.Patterns
import com.furianrt.mydiary.data.DataManager
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import io.reactivex.android.schedulers.AndroidSchedulers

class ForgotPassPresenter(
        private val dataManager: DataManager
) : ForgotPassContract.Presenter() {

    override fun onButtonCancelClick() {
        view?.close()
    }

    override fun onButtonSendClick(email: String) {
        if (validateEmail(email)) {
            view?.showLoading()
            addDisposable(dataManager.sendPasswordResetEmail(email)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view?.hideLoading()
                        view?.showEmailSent()
                    }, {
                        view?.hideLoading()
                        if (it is FirebaseAuthInvalidCredentialsException) {
                            view?.showErrorEmailFormat()
                        } else {
                            it.printStackTrace()
                            view?.showErrorNetworkConnection()
                        }
                    }))
        }
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
}