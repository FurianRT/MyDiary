package com.furianrt.mydiary.view.screens.pin.fragments.backupemail

import com.furianrt.mydiary.usecase.check.CheckEmailUseCase
import com.furianrt.mydiary.usecase.get.GetProfileUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class BackupEmailPresenter @Inject constructor(
        private val checkEmail: CheckEmailUseCase,
        private val getProfile: GetProfileUseCase
) : BackupEmailContract.Presenter() {

    override fun onViewCreated(email: String, firstLaunch: Boolean) {
        if (email.isEmpty() && firstLaunch) {
            addDisposable(getProfile.invoke()
                    .firstElement()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { view?.showEmail(it.email) })
        }
    }

    override fun onButtonDoneClick(email: String) {
        if (checkEmail.invoke(email)) {
            view?.showEmailIsCorrect(email)
        } else {
            view?.showErrorEmailFormat()
        }
    }
}