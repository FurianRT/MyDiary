/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.authentication.privacy

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter

interface PrivacyContract {

    interface View : BaseView {
        fun showLoading()
        fun hideLoading()
        fun showMessageSuccessRegistration()
        fun showErrorNetworkConnection()
        fun close()
        fun openLink(link: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonAcceptClick(email: String, password: String)
        abstract fun onButtonCancelClick()
        abstract fun onPrivacyLinkClick()
    }
}