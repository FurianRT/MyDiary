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

import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.domain.check.IsFingerprintAvailableUseCase
import com.furianrt.mydiary.domain.reset.ResetNotesAppearanceSettingsUseCase
import com.furianrt.mydiary.domain.delete.RemovePinEmailUseCase
import com.furianrt.mydiary.domain.get.GetPinEmailUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class GlobalSettingsPresenter @Inject constructor(
        private val getPinEmailUseCase: GetPinEmailUseCase,
        private val removePinEmailUseCase: RemovePinEmailUseCase,
        private val isFingerprintAvailableUseCase: IsFingerprintAvailableUseCase,
        private val resetNotesAppearanceSettingsUseCase: ResetNotesAppearanceSettingsUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : GlobalSettingsContract.Presenter() {

    override fun attachView(view: GlobalSettingsContract.View) {
        super.attachView(view)
        getPinEmailUseCase()?.let { view.showBackupEmail(it) }
        if (isFingerprintAvailableUseCase()) {
            view.showFingerprintOptions()
        } else {
            view.hideFingerprintOptions()
        }
    }

    override fun onPrefSecurityKeyClick() {
        if (getPinEmailUseCase() != null) {
            view?.showCreatePasswordView()
        } else {
            view?.showRemovePasswordView()
        }
    }

    override fun onPasswordCreated() {
        view?.showBackupEmail(getPinEmailUseCase()!!)
    }

    override fun onPasswordRemoved() {
        removePinEmailUseCase()
    }

    override fun onPrefRateAppClick() {
        view?.openAppPage()
    }

    override fun onPrefReportProblemClick() {
        view?.sendEmailToSupport(BuildConfig.SUPPORT_EMAIL)
    }

    override fun onPrefResetNotesColorClick() {
        addDisposable(resetNotesAppearanceSettingsUseCase()
                .observeOn(scheduler.ui())
                .subscribe { view?.onNotesAppearanceReset() })
    }
}