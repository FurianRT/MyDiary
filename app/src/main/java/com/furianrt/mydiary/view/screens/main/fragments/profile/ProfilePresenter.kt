/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main.fragments.profile

import com.furianrt.mydiary.domain.get.GetProfileUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class ProfilePresenter @Inject constructor(
        private val getProfile: GetProfileUseCase
) : ProfileContract.Presenter() {

    override fun attachView(view: ProfileContract.MvpView) {
        super.attachView(view)
        addDisposable(getProfile.invoke()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.showProfile(it) })
    }

    override fun onButtonCloseClick() {
        view?.close()
    }
}