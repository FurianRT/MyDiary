package com.furianrt.mydiary.screens.main.fragments.profile.menu

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.SyncProgressMessage
import javax.inject.Inject

class MenuProfilePresenter @Inject constructor(
        private val dataManager: DataManager
) : MenuProfileContract.Presenter() {

    override fun attachView(view: MenuProfileContract.MvpView) {
        super.attachView(view)
        val message = dataManager.getLastSyncMessage()
        view.disableSignOut(message != null && message.taskIndex != SyncProgressMessage.SYNC_FINISHED)
    }

    override fun onButtonSignOutClick() {
        view?.showSignOutView()
    }

    override fun onButtonChangePasswordClick() {
        view?.showPasswordView()
    }

    override fun onButtonAboutClick() {
        view?.showAboutView()
    }
}