package com.furianrt.mydiary.view.screens.main.fragments.profile.about

import com.furianrt.mydiary.domain.get.GetProfileUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class AboutProfilePresenter @Inject constructor(
        private val getProfile: GetProfileUseCase
) : AboutProfileContract.Presenter() {

    override fun onViewStart() {
        addDisposable(getProfile.invoke()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showProfileInfo(it) })
    }

    override fun onButtonBackClick() {
        view?.returnToMenuView()
    }
}