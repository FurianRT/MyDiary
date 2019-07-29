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

import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.domain.delete.RemovePinEmailUseCase
import com.furianrt.mydiary.domain.get.GetPinEmailUseCase
import javax.inject.Inject

class GlobalSettingsPresenter @Inject constructor(
        private val getPinEmail: GetPinEmailUseCase,
        private val removePinEmail: RemovePinEmailUseCase
) : GlobalSettingsContract.Presenter() {

    override fun onViewCreate() {
        getPinEmail.invoke()?.let { view?.showBackupEmail(it) }
    }

    override fun onPrefSecurityKeyClick() {
        if (getPinEmail.invoke() != null) {
            view?.showCreatePasswordView()
        } else {
            view?.showRemovePasswordView()
        }
    }

    override fun onPasswordCreated() {
        view?.showBackupEmail(getPinEmail.invoke()!!)
    }

    override fun onPasswordRemoved() {
        removePinEmail.invoke()
    }

    override fun onPrefRateAppClick() {
        view?.openAppPage()
    }

    override fun onPrefReportProblemClick() {
        view?.sendEmailToSupport(BuildConfig.SUPPORT_EMAIL)
    }
}