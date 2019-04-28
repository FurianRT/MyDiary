package com.furianrt.mydiary.screens.main.fragments.profile.about

import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers

class AboutProfilePresenter(
        private val dataManager: DataManager
) : AboutProfileContract.Presenter() {

    override fun onViewResume() {
        addDisposable(dataManager.getDbProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showProfileInfo(it) })
    }

    override fun onButtonBackClick() {
        view?.returnToMenuView()
    }
}