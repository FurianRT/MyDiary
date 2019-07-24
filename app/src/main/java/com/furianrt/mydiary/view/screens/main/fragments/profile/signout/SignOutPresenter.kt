package com.furianrt.mydiary.view.screens.main.fragments.profile.signout

import com.furianrt.mydiary.domain.auth.SignOutUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class SignOutPresenter @Inject constructor(
        private val signOut: SignOutUseCase
) : SignOutContract.Presenter() {

    override fun onButtonSignOutClick() {
        addDisposable(signOut.invoke()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.close() })
    }

    override fun onButtonSignOutCancelClick() {
        view?.returnToMenuView()
    }
}