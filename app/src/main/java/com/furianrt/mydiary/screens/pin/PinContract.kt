package com.furianrt.mydiary.screens.pin

import android.os.Bundle
import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

interface PinContract {

    interface MvpView : BaseMvpView {
        fun showPin(pin: String)
        fun showMessageRepeatPin()
        fun showMessagePinCreated()
        fun showMessageEnterPin()
        fun showMessagePinCorrect()
        fun showMessageCreatePin()
        fun showMessageCurrentPin()
        fun showErrorWrongPin()
        fun showErrorPinsDoNotMatch()
        fun showForgotPinView()
        fun showEnterEmailView()
        fun close()
        fun showFingerprintScanner()
        fun isFingerprintSupported(): Boolean
        fun hideFingerprintButton()
        fun showFingerprintButton()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonForgotPinClick()
        abstract fun onRestoreInstanceState(bundle: Bundle)
        abstract fun onSaveInstanceState(bundle: Bundle)
        abstract fun onButtonBackspaceClick()
        abstract fun onEmailEntered(email: String)
        abstract fun onButtonCloseClick()
        abstract fun onViewStartedModeCreate()
        abstract fun onViewStartedModeRemove()
        abstract fun onViewStartedModeLock()
        abstract fun onValueEnteredModeCreate(value: Int)
        abstract fun onValueEnteredModeRemove(value: Int)
        abstract fun onValueEnteredModeLock(value: Int)
        abstract fun onButtonFingerprintClick()
        abstract fun onFingerprintAccepted()
    }
}