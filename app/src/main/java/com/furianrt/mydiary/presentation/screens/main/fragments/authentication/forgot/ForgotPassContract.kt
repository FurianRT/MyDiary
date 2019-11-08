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

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter

interface ForgotPassContract {

    interface MvpView : BaseMvpView {
        fun showErrorEmptyEmail()
        fun showErrorEmailFormat()
        fun close()
        fun showLoading()
        fun hideLoading()
        fun showEmailSent()
        fun showErrorNetworkConnection()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonCancelClick()
        abstract fun onButtonSendClick(email: String)
    }
}