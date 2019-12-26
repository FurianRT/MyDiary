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

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter

interface SendEmailContract {

    interface View : BaseView {
        fun closeView()
        fun showLoading()
        fun hideLoading()
        fun showDoneView()
        fun showErrorMessageSend()
        fun showErrorNetworkConnection()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonSendClick()
        abstract fun onButtonCancelClick()
    }
}