package com.furianrt.mydiary.screens.main.fragments.authentication.login

import com.furianrt.mydiary.data.DataManager
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import org.mindrot.jbcrypt.BCrypt

class LoginPresenter(
        private val mDataManager: DataManager
) : LoginContract.Presenter() {

    private class WrongPasswordException : Throwable()

    override fun onButtonForgotClick() {

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
        addDisposable(mDataManager.getCloudProfile(email)
                .flatMapCompletable { profile ->
                    Completable.concat(listOf(
                            validatePassword(password, profile.passwordHash),
                            mDataManager.newProfile(profile)
                    ))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.hideLoading()
                    view?.showLoginSuccess()
                }, { error ->
                    view?.hideLoading()
                    if (error is NoSuchElementException || error is WrongPasswordException) {
                        view?.showErrorWrongCredential()
                    } else {
                        error.printStackTrace()
                        view?.showErrorNetworkConnection()
                    }
                }))
    }

    private fun validatePassword(password: String, passwordHash: String): Completable =
            Single.fromCallable { BCrypt.checkpw(password, passwordHash) }
                    .flatMapCompletable { passwordValid ->
                        if (passwordValid) {
                            return@flatMapCompletable Completable.complete()
                        } else {
                            throw WrongPasswordException()
                        }
                    }
}