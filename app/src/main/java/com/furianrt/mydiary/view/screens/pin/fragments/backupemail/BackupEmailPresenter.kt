/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.pin.fragments.backupemail

import com.furianrt.mydiary.domain.check.CheckEmailUseCase
import com.furianrt.mydiary.domain.get.GetProfileUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class BackupEmailPresenter @Inject constructor(
        private val checkEmail: CheckEmailUseCase,
        private val getProfile: GetProfileUseCase
) : BackupEmailContract.Presenter() {

    override fun onViewCreated(email: String, firstLaunch: Boolean) {
        if (email.isEmpty() && firstLaunch) {
            addDisposable(getProfile.invoke()
                    .firstElement()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { view?.showEmail(it.email) })
        }
    }

    override fun onButtonDoneClick(email: String) {
        if (checkEmail.invoke(email)) {
            view?.showEmailIsCorrect(email)
        } else {
            view?.showErrorEmailFormat()
        }
    }
}