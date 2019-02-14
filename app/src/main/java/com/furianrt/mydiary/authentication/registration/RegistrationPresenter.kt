package com.furianrt.mydiary.authentication.registration

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
            if (email.isEmpty()) {
                v.showErrorEmptyEmail()
                return
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                v.showErrorEmailFormat()
                return
            }
            if (password.isEmpty()) {
                v.showErrorEmptyPassword()
                return
            }
            if (passwordRepeat.isEmpty()) {
                v.showErrorEmptyPasswordRepeat()
                return
            }
            if (password.length < PASSWORD_MIN_LENGTH) {
                v.showErrorShortPassword()
                return
            }
            if (password != passwordRepeat) {
                v.showErrorPassword()
                return
            }
            v.clearEmailMessages()
            v.showLoading()
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
                        return@flatMapCompletable Completable.merge(listOf(
                                mDataManager.createProfile(profile),
                                mDataManager.saveProfile(profile)
                        ))
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        v.hideLoading()
                        v.showMessageSuccessRegistration()
                    }, {
                        v.hideLoading()
                        if (it is ProfileExistsException) {
                            v.showErrorEmailExists()
                        } else {
                            it.printStackTrace()
                            v.showErrorNetworkConnection()
                        }
                    }))

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
                if (email.isEmpty()) {
                    v.showErrorEmptyEmail()
                    return
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    v.showErrorEmailFormat()
                    return
                }
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

    private fun hashPassword(password: String): Single<String> =
            Single.fromCallable { BCrypt.hashpw(password, BCrypt.gensalt()) }
                    .subscribeOn(Schedulers.computation())

    class ProfileExistsException : Throwable()
}