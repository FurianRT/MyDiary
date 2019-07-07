package com.furianrt.mydiary.screens.main.fragments.profile.signout

import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class SignOutPresenter @Inject constructor(
        private val dataManager: DataManager
) : SignOutContract.Presenter() {

    override fun onButtonSignOutClick() {
        addDisposable(dataManager.signOut()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.close() })
    }

    override fun onButtonSignOutCancelClick() {
        view?.returnToMenuView()
    }
}