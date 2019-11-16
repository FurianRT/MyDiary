/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.pin.fragments.sendemail

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter

interface SendEmailContract {

    interface View : BaseMvpView {
        fun closeView()
        fun showLoading()
        fun hideLoading()
        fun showDoneView()
        fun showErrorMessageSend()
        fun showErrorNetworkConnection()
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun onButtonSendClick()
        abstract fun onButtonCancelClick()
    }
}