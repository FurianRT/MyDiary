/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.pin.fragments.backupemail

import com.furianrt.mydiary.domain.check.CheckEmailUseCase
import com.furianrt.mydiary.domain.get.GetProfileUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class BackupEmailPresenter @Inject constructor(
        private val checkEmailUseCase: CheckEmailUseCase,
        private val getProfileUseCase: GetProfileUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : BackupEmailContract.Presenter() {

    override fun onViewCreated(email: String, firstLaunch: Boolean) {
        if (email.isEmpty() && firstLaunch) {
            addDisposable(getProfileUseCase()
                    .firstElement()
                    .observeOn(scheduler.ui())
                    .subscribe { view?.showEmail(it.email) })
        }
    }

    override fun onButtonDoneClick(email: String) {
        if (checkEmailUseCase(email)) {
            view?.showEmailIsCorrect(email)
        } else {
            view?.showErrorEmailFormat()
        }
    }
}