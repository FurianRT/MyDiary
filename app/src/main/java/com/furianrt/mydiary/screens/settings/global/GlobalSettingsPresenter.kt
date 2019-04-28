package com.furianrt.mydiary.screens.settings.global

import com.furianrt.mydiary.data.DataManager

class GlobalSettingsPresenter(
        private val dataManager: DataManager
) : GlobalSettingsContract.Presenter() {

    override fun onViewCreate() {
        if (dataManager.isPasswordEnabled()) {
            view?.showBackupEmail(dataManager.getBackupEmail())
        }
    }

    override fun onPrefSecurityKeyClick() {
        if (dataManager.isPasswordEnabled()) {
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