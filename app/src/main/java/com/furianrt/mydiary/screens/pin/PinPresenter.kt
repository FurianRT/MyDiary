package com.furianrt.mydiary.screens.pin

import android.os.Bundle
import android.os.Handler
import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers

class PinPresenter(
        private val mDataManager: DataManager
) : PinContract.Presenter() {

    companion object {
        private const val BUNDLE_PASSWORD = "password"
        private const val BUNDLE_PREVIOUS_PASSWORD = "prev_password"
        private const val CREATE_PASSWORD_DELAY = 200L
    }

    private var mPassword = ""
    private var mPrevPassword = ""
    private val mHandler = Handler()
    private val mCreatePasswordRunnable = Runnable { checkPinCreateMode() }

    override fun onSaveInstanceState(bundle: Bundle?) {
        bundle?.putString(BUNDLE_PASSWORD, mPassword)
        bundle?.putString(BUNDLE_PREVIOUS_PASSWORD, mPrevPassword)
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        mPassword = bundle?.getString(BUNDLE_PASSWORD) ?: ""
        mPrevPassword = bundle?.getString(BUNDLE_PREVIOUS_PASSWORD) ?: ""
    }

    override fun onViewResumedModeCreate() {
        view?.showPassword(mPassword)
        if (mPrevPassword.isNotEmpty()) {
            view?.showMessageRepeatPassword()
        } else {
            view?.showMessageCreatePassword()
        }
    }

    override fun onViewResumedModeRemove() {
        view?.showPassword(mPassword)
        view?.showMessageCurrentPassword()
    }

    override fun onViewResumedModeLock() {
        view?.showPassword(mPassword)
        view?.showMessageEnterPassword()
    }

    override fun onValueEnteredModeCreate(value: Int) {
        if (mPassword.length != 4) {
            mPassword += value
            view?.showPassword(mPassword)
            if (mPassword.length == 4) {
                mHandler.postDelayed(mCreatePasswordRunnable, CREATE_PASSWORD_DELAY)
            }
        }
    }

    override fun onValueEnteredModeRemove(value: Int) {
        if (mPassword.length != 4) {
            mPassword += value
            view?.showPassword(mPassword)
            if (mPassword.length == 4) {
                checkPinRemoveMode()
            }
        }
    }

    override fun onValueEnteredModeLock(value: Int) {
        if (mPassword.length != 4) {
            mPassword += value
            view?.showPassword(mPassword)
            if (mPassword.length == 4) {
                checkPinLockMode()
            }
        }
    }

    private fun checkPinLockMode() {
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

    override fun detachView() {
        super.detachView()
        mHandler.removeCallbacks(mCreatePasswordRunnable)
    }
}