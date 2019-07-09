package com.furianrt.mydiary.screens.main.fragments.authentication.login

import com.furianrt.mydiary.data.DataManager
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class LoginPresenter @Inject constructor(
        private val dataManager: DataManager
) : LoginContract.Presenter() {

    override fun onButtonForgotClick(email: String) {
        view?.showForgotPassView(email)
    }

    override fun onButtonSignInClick(email: String, password: String) {
        when {
            view?.isNetworkAvailable() != true -> view?.showErrorNetworkConnection()
            email.isEmpty() -> view?.showErrorEmptyEmail()
            password.isEmpty() -> view?.showErrorEmptyPassword()
            else -> signIn(email, password)
        }
    }

    private fun signIn(email: String, password: String) {
        view?.showLoading()
        addDisposable(dataManager.signIn(email, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.hideLoading()
                    view?.showLoginSuccess()
                }, {
                    view?.hideLoading()
                    view?.hideLoading()
                    if (it is FirebaseAuthInvalidUserException || it is FirebaseAuthInvalidCredentialsException) {
                        view?.showErrorWrongCredential()
                    } else {
                        it.printStackTrace()
                        view?.showErrorNetworkConnection()
                    }
                }))
    }
}