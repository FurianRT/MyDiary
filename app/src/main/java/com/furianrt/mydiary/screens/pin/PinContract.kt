package com.furianrt.mydiary.screens.pin

import android.os.Bundle
import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface PinContract {

    interface View : BaseView {
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
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonForgotPinClick()
        abstract fun onRestoreInstanceState(bundle: Bundle?)
        abstract fun onSaveInstanceState(bundle: Bundle?)
        abstract fun onButtonBackspaceClick()
        abstract fun onEmailEntered(email: String)
        abstract fun onButtonCloseClick()
        abstract fun onViewResumedModeCreate()
        abstract fun onViewResumedModeRemove()
        abstract fun onViewResumedModeLock()
        abstract fun onValueEnteredModeCreate(value: Int)
        abstract fun onValueEnteredModeRemove(value: Int)
        abstract fun onValueEnteredModeLock(value: Int)
    }
}