package com.furianrt.mydiary.main.fragments.authentication.login

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyProfile
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import org.mindrot.jbcrypt.BCrypt

class LoginPresenter(
        private val mDataManager: DataManager
) : LoginContract.Presenter() {

    override fun onButtonForgotClick() {

    }

    override fun onButtonSignInClick(email: String, password: String) {
        view?.let { v ->
            if (!v.isNetworkAvailable()) {
                v.showErrorNetworkConnection()
                return
            }
            if (email.isEmpty()) {
                view?.showErrorEmptyEmail()
                return
            }
            if (password.isEmpty()) {
                view?.showErrorEmptyPassword()
                return
            }
            v.showLoading()
            var profile: MyProfile? = null   //todo убрать костыль
            addDisposable(mDataManager.getCloudProfile(email)
                    .flatMapSingle {
                        profile = it
                        return@flatMapSingle validatePassword(password, it.passwordHash)
                    }
                    .flatMapCompletable { isPasswordCorrect ->
                        return@flatMapCompletable if (isPasswordCorrect) {
                            mDataManager.newProfileProfile(profile!!)
                        } else {
                            throw NoSuchElementException()
                        }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        v.hideLoading()
                        v.showLoginSuccess()
                    }, { error ->
                        v.hideLoading()
                        if (error is NoSuchElementException) {
                            v.showErrorWrongCredential()
                        } else {
                            error.printStackTrace()
                            v.showErrorNetworkConnection()
                        }
                    }))
        }
    }

    private fun validatePassword(password: String, passwordHash: String): Single<Boolean> =
            Single.fromCallable { BCrypt.checkpw(password, passwordHash) }
}