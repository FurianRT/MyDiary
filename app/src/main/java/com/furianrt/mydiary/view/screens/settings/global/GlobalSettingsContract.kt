/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.settings.global

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface GlobalSettingsContract {

    interface MvpView : BaseMvpView {
        fun showCreatePasswordView()
        fun showRemovePasswordView()
        fun showBackupEmail(email: String)
        fun openAppPage()
        fun sendEmailToSupport(supportEmail: String)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onPrefSecurityKeyClick()
        abstract fun onPasswordCreated()
        abstract fun onPasswordRemoved()
        abstract fun onViewCreate()
        abstract fun onPrefRateAppClick()
        abstract fun onPrefReportProblemClick()
    }
}