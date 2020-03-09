/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.profile

import com.furianrt.mydiary.domain.get.GetProfileUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class ProfilePresenter @Inject constructor(
        private val getProfileUseCase: GetProfileUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : ProfileContract.Presenter() {

    override fun attachView(view: ProfileContract.View) {
        super.attachView(view)
        addDisposable(getProfileUseCase()
                .observeOn(scheduler.ui())
                .subscribe { result ->
                    if (result.isPresent) {
                        view.showProfile(result.get())
                    }
                })
    }

    override fun onButtonCloseClick() {
        view?.close()
    }
}