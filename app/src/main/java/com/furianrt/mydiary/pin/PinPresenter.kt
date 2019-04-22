package com.furianrt.mydiary.pin

import android.os.Bundle
import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers

class PinPresenter(
        private val mDataManager: DataManager
) : PinContract.Presenter() {

    companion object {
        private const val BUNDLE_PASSWORD = "password"
        private const val BUNDLE_PREVIOUS_PASSWORD = "prev_password"
    }

    private var mPassword = ""
    private var mPrevPassword = ""
    private var mMode: Int = PinActivity.MODE_CREATE

    override fun onSaveInstanceState(bundle: Bundle?) {
        bundle?.putString(BUNDLE_PASSWORD, mPassword)
        bundle?.putString(BUNDLE_PREVIOUS_PASSWORD, mPrevPassword)
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        mPassword = bundle?.getString(BUNDLE_PASSWORD) ?: ""
        mPrevPassword = bundle?.getString(BUNDLE_PREVIOUS_PASSWORD) ?: ""
    }

    override fun onViewResumed() {
        view?.showPassword(mPassword)
        when {
            mPrevPassword.isNotEmpty() -> view?.showMessageRepeatPassword()
            mMode == PinActivity.MODE_CREATE -> view?.showMessageCreatePassword()
            mMode == PinActivity.MODE_REMOVE -> view?.showMessageCurrentPassword()
            mMode == PinActivity.MODE_LOCK -> view?.showMessageEnterPassword()
        }
    }

    override fun onValueEntered(value: Int) {
        if (mPassword.length != 4) {
            mPassword += value
            view?.showPassword(mPassword)
            if (mPassword.length == 4) {
                when (mMode) {
                    PinActivity.MODE_CREATE -> checkPinCreateMode()
                    PinActivity.MODE_REMOVE -> checkPinRemoveMode()
                    else -> checkPinEnterMode()
                }
            }
        }
    }

    private fun checkPinEnterMode() {
        addDisposable(mDataManager.getPin()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { savedPin ->
                    if (mPassword == savedPin) {
                        mDataManager.setAuthorized(true)
                        view?.showMessagePinCorrect()
                    } else {
                        mPassword = ""
                        view?.showPassword(mPassword)
                        view?.showErrorWrongPin()
                    }
                })
    }

    private fun checkPinCreateMode() {
        when {
            mPrevPassword.isEmpty() -> {
                mPrevPassword = mPassword
                mPassword = ""
                view?.showPassword(mPassword)
                view?.showMessageRepeatPassword()
            }
            mPrevPassword == mPassword -> {
                view?.showEnterEmailView()
            }
            else -> {
                mPrevPassword = ""
                mPassword = ""
                view?.showPassword(mPassword)
                view?.showErrorPinsDoNotMatch()
                view?.showMessageCreatePassword()
            }
        }
    }

    private fun checkPinRemoveMode() {
        addDisposable(mDataManager.getPin()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { savedPin ->
                    if (mPassword == savedPin) {
                        mDataManager.setAuthorized(true)
                        view?.showMessagePinCorrect()
                    } else {
                        mPassword = ""
                        view?.showPassword(mPassword)
                        view?.showErrorWrongPin()
                    }
                })
    }

    override fun onEmailEntered(email: String) {
        mDataManager.setBackupEmail(email)
        addDisposable(mDataManager.setPin(mPassword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mDataManager.setAuthorized(true)
                    view?.showMessagePasswordCreated()
                })
    }

    override fun setMode(mode: Int) {
        mMode = mode
    }

    override fun onButtonCloseClick() {
        view?.close()
    }

    override fun onButtonBackspaceClick() {
        if (mPassword.isNotEmpty()) {
            mPassword = mPassword.substring(0, mPassword.length - 1)
            view?.showPassword(mPassword)
        }
    }

    override fun onButtonForgotPasswordClick() {
        view?.showForgotPinView()
    }
}