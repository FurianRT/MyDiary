package com.furianrt.mydiary.view.screens.settings.global

import com.furianrt.mydiary.usecase.delete.RemovePinEmailUseCase
import com.furianrt.mydiary.usecase.get.GetPinEmailUseCase
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
}