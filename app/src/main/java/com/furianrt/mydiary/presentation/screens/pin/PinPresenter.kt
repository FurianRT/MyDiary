/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.pin

import android.os.Bundle
import android.os.Handler
import com.furianrt.mydiary.domain.auth.AuthorizeUseCase
import com.furianrt.mydiary.domain.check.IsFingerprintAvailableUseCase
import com.furianrt.mydiary.domain.check.CheckPinUseCase
import com.furianrt.mydiary.domain.save.SavePinUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class PinPresenter @Inject constructor(
        private val savePinUseCase: SavePinUseCase,
        private val authorizeUseCase: AuthorizeUseCase,
        private val checkPinUseCase: CheckPinUseCase,
        private val isFingerprintAvailableUseCase: IsFingerprintAvailableUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
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
        if (isFingerprintAvailableUseCase()) {
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
        addDisposable(checkPinUseCase(mPin)
                .observeOn(scheduler.ui())
                .subscribe { validPin ->
                    if (validPin) {
                        authorizeUseCase(true)
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
        addDisposable(checkPinUseCase(mPin)
                .observeOn(scheduler.ui())
                .subscribe { validPin ->
                    if (validPin) {
                        view?.showMessagePinCorrect()
                    } else {
                        mPin = ""
                        view?.showPin(mPin)
                        view?.showErrorWrongPin()
                    }
                })
    }

    override fun onEmailEntered(email: String) {
        addDisposable(savePinUseCase(mPin, email)
                .observeOn(scheduler.ui())
                .subscribe {
                    authorizeUseCase(true)
                    view?.showMessagePinCreated()
                })
    }

    override fun onButtonCloseClickModeCreate() {
        view?.closeView()
    }

    override fun onButtonCloseClickModeRemove() {
        view?.closeView()
    }

    override fun onButtonCloseClickModeLock() {
        authorizeUseCase(false)
        view?.closeApp()
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
        authorizeUseCase(true)
        view?.showMessagePinCorrect()
    }

    override fun detachView() {
        super.detachView()
        mHandler.removeCallbacks(mCreatePinRunnable)
    }
}