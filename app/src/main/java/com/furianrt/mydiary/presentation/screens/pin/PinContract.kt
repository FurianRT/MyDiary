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
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter

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