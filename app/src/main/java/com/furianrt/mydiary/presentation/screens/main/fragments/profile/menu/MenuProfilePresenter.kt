/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.profile.menu

import com.furianrt.mydiary.model.entity.SyncProgressMessage
import com.furianrt.mydiary.domain.get.GetLastSyncMessageUseCase
import javax.inject.Inject

class MenuProfilePresenter @Inject constructor(
        private val getLastSyncMessage: GetLastSyncMessageUseCase
) : MenuProfileContract.Presenter() {

    override fun attachView(view: MenuProfileContract.View) {
        super.attachView(view)
        val message = getLastSyncMessage.invoke()
        view.disableSignOut(message != null && message.task != SyncProgressMessage.SYNC_FINISHED)
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