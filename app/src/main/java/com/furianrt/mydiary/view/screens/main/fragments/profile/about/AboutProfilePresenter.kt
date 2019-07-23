package com.furianrt.mydiary.view.screens.main.fragments.profile.about

import com.furianrt.mydiary.domain.get.GetProfileUseCase
import com.furianrt.mydiary.domain.get.GetTimeFormatUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class AboutProfilePresenter @Inject constructor(
        private val getProfile: GetProfileUseCase,
        private val getTimeFormat: GetTimeFormatUseCase
) : AboutProfileContract.Presenter() {

    override fun onViewStart() {
        addDisposable(getProfile.invoke()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { profile ->
                    view?.showProfileInfo(profile, getTimeFormat.invoke() == GetTimeFormatUseCase.TIME_FORMAT_24)
                })
    }

    override fun onButtonBackClick() {
        view?.returnToMenuView()
    }
}