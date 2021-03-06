/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.settings.global

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter

interface GlobalSettingsContract {

    interface View : BaseView {
        fun showCreatePasswordView()
        fun showRemovePasswordView()
        fun showBackupEmail(email: String)
        fun openAppPage()
        fun sendEmailToSupport(supportEmail: String)
        fun showFingerprintOptions()
        fun hideFingerprintOptions()
        fun onNotesAppearanceReset()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onPrefSecurityKeyClick()
        abstract fun onPasswordCreated()
        abstract fun onPasswordRemoved()
        abstract fun onPrefRateAppClick()
        abstract fun onPrefReportProblemClick()
        abstract fun onPrefResetNotesColorClick()
    }
}