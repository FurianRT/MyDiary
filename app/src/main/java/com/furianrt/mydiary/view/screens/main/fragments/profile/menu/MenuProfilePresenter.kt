package com.furianrt.mydiary.view.screens.main.fragments.profile.menu

import com.furianrt.mydiary.data.model.SyncProgressMessage
import com.furianrt.mydiary.domain.get.GetLastSyncMessageUseCase
import javax.inject.Inject

class MenuProfilePresenter @Inject constructor(
        private val getLastSyncMessage: GetLastSyncMessageUseCase
) : MenuProfileContract.Presenter() {

    override fun attachView(view: MenuProfileContract.MvpView) {
        super.attachView(view)
        val message = getLastSyncMessage.invoke()
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