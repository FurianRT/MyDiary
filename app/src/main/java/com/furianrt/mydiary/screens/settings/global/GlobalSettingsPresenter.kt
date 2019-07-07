package com.furianrt.mydiary.screens.settings.global

import com.furianrt.mydiary.data.DataManager
import javax.inject.Inject

class GlobalSettingsPresenter @Inject constructor(
        private val dataManager: DataManager
) : GlobalSettingsContract.Presenter() {

    override fun onViewCreate() {
        if (dataManager.isPinEnabled()) {
            view?.showBackupEmail(dataManager.getBackupEmail())
        }
    }

    override fun onPrefSecurityKeyClick() {
        if (dataManager.isPinEnabled()) {
            view?.showCreatePasswordView()
        } else {
            view?.showRemovePasswordView()
        }
    }

    override fun onPasswordCreated() {
        view?.showBackupEmail(dataManager.getBackupEmail())
    }

    override fun onPasswordRemoved() {
        dataManager.setBackupEmail("")
    }
}