package com.furianrt.mydiary.profile

import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers

class ProfilePresenter(
        private val mDataManager: DataManager
) : ProfileContract.Presenter() {

    override fun onButtonSignOutClick() {
        addDisposable(mDataManager.deleteProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showSignOut() })
    }
}