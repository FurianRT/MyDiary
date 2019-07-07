package com.furianrt.mydiary.screens.main.fragments.profile.about

import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class AboutProfilePresenter @Inject constructor(
        private val dataManager: DataManager
) : AboutProfileContract.Presenter() {

    override fun onViewStart() {
        addDisposable(dataManager.getDbProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showProfileInfo(it) })
    }

    override fun onButtonBackClick() {
        view?.returnToMenuView()
    }
}