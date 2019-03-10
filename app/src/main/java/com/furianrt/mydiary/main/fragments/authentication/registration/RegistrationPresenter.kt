package com.furianrt.mydiary.main.fragments.authentication.registration

import android.util.Patterns
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyProfile
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.mindrot.jbcrypt.BCrypt

class RegistrationPresenter(
        private val mDataManager: DataManager
) : RegistrationContract.Presenter() {

    companion object {
        private const val PASSWORD_MIN_LENGTH = 4
    }

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
                    addDisposable(mDataManager.isProfileExists(email)
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
        addDisposable(mDataManager.isProfileExists(email)
                .flatMap { exists ->
                    return@flatMap if (exists) {
                        throw ProfileExistsException()
                    } else {
                        hashPassword(password)
                    }
                }
                .flatMapCompletable { passwordHash ->
                    val profile = MyProfile(email, passwordHash)
                    return@flatMapCompletable Completable.concat(listOf(
                            mDataManager.newProfileProfile(profile),
                            mDataManager.saveProfile(profile)
                    ))
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

    private fun hashPassword(password: String): Single<String> =
            Single.fromCallable { BCrypt.hashpw(password, BCrypt.gensalt()) }
                    .subscribeOn(Schedulers.computation())

    class ProfileExistsException : Throwable()
}