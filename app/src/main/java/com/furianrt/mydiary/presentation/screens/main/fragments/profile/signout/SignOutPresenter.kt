/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.profile.signout

import com.furianrt.mydiary.domain.auth.SignOutUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class SignOutPresenter @Inject constructor(
        private val signOutUseCase: SignOutUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : SignOutContract.Presenter() {

    override fun onButtonSignOutClick() {
        addDisposable(signOutUseCase()
                .observeOn(scheduler.ui())
                .subscribe { view?.close() })
    }

    override fun onButtonSignOutCancelClick() {
        view?.returnToMenuView()
    }
}