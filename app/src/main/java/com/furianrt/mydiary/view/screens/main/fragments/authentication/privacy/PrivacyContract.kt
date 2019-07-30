/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main.fragments.authentication.privacy

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface PrivacyContract {

    interface MvpView : BaseMvpView {
        fun showLoading()
        fun hideLoading()
        fun showMessageSuccessRegistration()
        fun showErrorNetworkConnection()
        fun close()
        fun openLink(link: String)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonAcceptClick(email: String, password: String)
        abstract fun onButtonCancelClick()
        abstract fun onPrivacyLinkClick()
    }
}