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
import com.furianrt.mydiary.domain.check.IsFingerprintAvailableUseCase
import com.furianrt.mydiary.domain.reset.ResetNotesAppearanceSettingsUseCase
import com.furianrt.mydiary.domain.delete.RemovePinEmailUseCase
import com.furianrt.mydiary.domain.get.GetPinEmailUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class GlobalSettingsPresenter @Inject constructor(
        private val getPinEmail: GetPinEmailUseCase,
        private val removePinEmail: RemovePinEmailUseCase,
        private val isFingerprintAvailable: IsFingerprintAvailableUseCase,
        private val resetNotesAppearanceSettings: ResetNotesAppearanceSettingsUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : GlobalSettingsContract.Presenter() {

    override fun attachView(view: GlobalSettingsContract.MvpView) {
        super.attachView(view)
        getPinEmail.invoke()?.let { view.showBackupEmail(it) }
        if (isFingerprintAvailable.invoke()) {
            view.showFingerprintOptions()
        } else {
            view.hideFingerprintOptions()
        }
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

    override fun onPrefResetNotesColorClick() {
        addDisposable(resetNotesAppearanceSettings.invoke()
                .observeOn(scheduler.ui())
                .subscribe { view?.onNotesAppearanceReset() })
    }
}