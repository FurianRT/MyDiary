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
import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter

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
        fun showFingerprintScanner()
        fun hideFingerprintButton()
        fun showFingerprintButton()
        fun closeView()
        fun closeApp()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonForgotPinClick()
        abstract fun onRestoreInstanceState(bundle: Bundle)
        abstract fun onSaveInstanceState(bundle: Bundle)
        abstract fun onButtonBackspaceClick()
        abstract fun onEmailEntered(email: String)
        abstract fun onButtonCloseClickModeCreate()
        abstract fun onButtonCloseClickModeRemove()
        abstract fun onButtonCloseClickModeLock()
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