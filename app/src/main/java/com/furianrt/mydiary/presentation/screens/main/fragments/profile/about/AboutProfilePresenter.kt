/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.profile.about

import com.furianrt.mydiary.domain.get.GetProfileUseCase
import com.furianrt.mydiary.domain.get.GetTimeFormatUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class AboutProfilePresenter @Inject constructor(
        private val getProfile: GetProfileUseCase,
        private val getTimeFormat: GetTimeFormatUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : AboutProfileContract.Presenter() {

    override fun onViewStart() {
        addDisposable(getProfile.invoke()
                .observeOn(scheduler.ui())
                .subscribe { profile ->
                    view?.showProfileInfo(profile, getTimeFormat.invoke() == GetTimeFormatUseCase.TIME_FORMAT_24)
                })
    }

    override fun onButtonBackClick() {
        view?.returnToMenuView()
    }
}