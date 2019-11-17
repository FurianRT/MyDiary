/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.authentication.forgot

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter

interface ForgotPassContract {

    interface View : BaseView {
        fun showErrorEmptyEmail()
        fun showErrorEmailFormat()
        fun close()
        fun showLoading()
        fun hideLoading()
        fun showEmailSent()
        fun showErrorNetworkConnection()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonCancelClick()
        abstract fun onButtonSendClick(email: String)
    }
}