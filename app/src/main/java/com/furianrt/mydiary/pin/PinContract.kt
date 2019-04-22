package com.furianrt.mydiary.pin

import android.os.Bundle
import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface PinContract {

    interface View : BaseView {
        fun showPassword(password: String)
        fun showMessageRepeatPassword()
        fun showMessagePasswordCreated()
        fun showMessageEnterPassword()
        fun showMessagePinCorrect()
        fun showMessageCreatePassword()
        fun showMessageCurrentPassword()
        fun showErrorWrongPin()
        fun showErrorPinsDoNotMatch()
        fun showForgotPinView()
        fun showEnterEmailView()
        fun close()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onValueEntered(value: Int)
        abstract fun onButtonForgotPasswordClick()
        abstract fun onViewResumed()
        abstract fun onRestoreInstanceState(bundle: Bundle?)
        abstract fun onSaveInstanceState(bundle: Bundle?)
        abstract fun onButtonBackspaceClick()
        abstract fun setMode(mode: Int)
        abstract fun onEmailEntered(email: String)
        abstract fun onButtonCloseClick()
    }
}