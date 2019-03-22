package com.furianrt.mydiary.main.fragments.profile.about

import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers

class AboutProfilePresenter(
        private val mDataManager: DataManager
) : AboutProfileContract.Presenter() {

    override fun onViewResume() {
        addDisposable(mDataManager.getDbProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showProfileInfo(it) })
    }

    override fun onButtonBackClick() {
        view?.returnToMenuView()
    }
}