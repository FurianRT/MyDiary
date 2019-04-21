package com.furianrt.mydiary.settings.global

import com.furianrt.mydiary.data.DataManager

class GlobalSettingsPresenter(
        private val mDataManager: DataManager
) : GlobalSettingsContract.Presenter() {

    override fun onPrefSecurityKeyChanged() {
        if (mDataManager.isPasswordEnabled()) {
            view?.showCreatePasswordView()
        } else {
            view?.showRemovePasswordView()
        }
    }

    override fun onPasswordCreated() {
        view?.showBackupEmail(mDataManager.getBackupEmail())
    }

    override fun onPasswordRemoved() {
        mDataManager.setBackupEmail("")
    }
}