package com.furianrt.mydiary.screens.main.fragments.profile

import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class ProfilePresenter @Inject constructor(
        private val dataManager: DataManager
) : ProfileContract.Presenter() {

    override fun attachView(view: ProfileContract.MvpView) {
        super.attachView(view)
        addDisposable(dataManager.getDbProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.showProfile(it) })
    }

    override fun onButtonCloseClick() {
        view?.close()
    }
}