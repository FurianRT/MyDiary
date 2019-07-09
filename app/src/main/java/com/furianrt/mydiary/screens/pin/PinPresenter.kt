package com.furianrt.mydiary.screens.pin

import android.os.Bundle
import android.os.Handler
import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class PinPresenter @Inject constructor(
        private val dataManager: DataManager
) : PinContract.Presenter() {

    companion object {
        private const val BUNDLE_PIN = "password"
        private const val BUNDLE_PREVIOUS_PIN = "prev_password"
        private const val CREATE_PIN_DELAY = 200L
    }

    private var mPin = ""
    private var mPrevPin = ""
    private val mHandler = Handler()
    private val mCreatePinRunnable = Runnable { checkPinCreateMode() }

    override fun onSaveInstanceState(bundle: Bundle) {
        bundle.putString(BUNDLE_PIN, mPin)
        bundle.putString(BUNDLE_PREVIOUS_PIN, mPrevPin)
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        mPin = bundle.getString(BUNDLE_PIN, "")
        mPrevPin = bundle.getString(BUNDLE_PREVIOUS_PIN, "")
    }

    override fun onViewStartedModeCreate() {
        view?.hideFingerprintButton()
        view?.showPin(mPin)
        if (mPrevPin.isNotEmpty()) {
            view?.showMessageRepeatPin()
        } else {
            view?.showMessageCreatePin()
        }
    }

    override fun onViewStartedModeRemove() {
        view?.hideFingerprintButton()
        view?.showPin(mPin)
        view?.showMessageCurrentPin()
    }

    override fun onViewStartedModeLock() {
        view?.showPin(mPin)
        view?.showMessageEnterPin()
        if (view?.isFingerprintSupported() == true && dataManager.isFingerprintEnabled()) {
            view?.showFingerprintButton()
            view?.showFingerprintScanner()
        } else {
            view?.hideFingerprintButton()
        }
    }

    override fun onValueEnteredModeCreate(value: Int) {
        if (mPin.length != 4) {
            mPin += value
            view?.showPin(mPin)
            if (mPin.length == 4) {
                mHandler.postDelayed(mCreatePinRunnable, CREATE_PIN_DELAY)
            }
        }
    }

    override fun onValueEnteredModeRemove(value: Int) {
        if (mPin.length != 4) {
            mPin += value
            view?.showPin(mPin)
            if (mPin.length == 4) {
                checkPinRemoveMode()
            }
        }
    }

    override fun onValueEnteredModeLock(value: Int) {
        if (mPin.length != 4) {
            mPin += value
            view?.showPin(mPin)
            if (mPin.length == 4) {
                checkPinLockMode()
            }
        }
    }

    private fun checkPinLockMode() {
        addDisposable(dataManager.getPin()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { savedPin ->
                    if (mPin == savedPin) {
                        dataManager.setAuthorized(true)
                        view?.showMessagePinCorrect()
                    } else {
                        mPin = ""
                        view?.showPin(mPin)
                        view?.showErrorWrongPin()
                    }
                })
    }

    private fun checkPinCreateMode() {
        when {
            mPrevPin.isEmpty() -> {
                mPrevPin = mPin
                mPin = ""
                view?.showPin(mPin)
                view?.showMessageRepeatPin()
            }
            mPrevPin == mPin -> {
                view?.showEnterEmailView()
            }
            else -> {
                mPrevPin = ""
                mPin = ""
                view?.showPin(mPin)
                view?.showErrorPinsDoNotMatch()
                view?.showMessageCreatePin()
            }
        }
    }

    private fun checkPinRemoveMode() {
        addDisposable(dataManager.getPin()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { savedPin ->
                    if (mPin == savedPin) {
                        view?.showMessagePinCorrect()
                    } else {
                        mPin = ""
                        view?.showPin(mPin)
                        view?.showErrorWrongPin()
                    }
                })
    }

    override fun onEmailEntered(email: String) {
        dataManager.setBackupEmail(email)
        addDisposable(dataManager.setPin(mPin)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    dataManager.setAuthorized(true)
                    view?.showMessagePinCreated()
                })
    }

    override fun onButtonCloseClick() {
        view?.close()
    }

    override fun onButtonBackspaceClick() {
        if (mPin.isNotEmpty()) {
            mPin = mPin.substring(0, mPin.length - 1)
            view?.showPin(mPin)
        }
    }

    override fun onButtonForgotPinClick() {
        view?.showForgotPinView()
    }

    override fun onButtonFingerprintClick() {
        view?.showFingerprintScanner()
    }

    override fun onFingerprintAccepted() {
        dataManager.setAuthorized(true)
        view?.showMessagePinCorrect()
    }

    override fun detachView() {
        super.detachView()
        mHandler.removeCallbacks(mCreatePinRunnable)
    }
}