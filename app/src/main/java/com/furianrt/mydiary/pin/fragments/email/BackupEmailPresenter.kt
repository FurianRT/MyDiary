package com.furianrt.mydiary.pin.fragments.email

import android.util.Patterns
import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers

class BackupEmailPresenter(
        private val mDataManager: DataManager
) : BackupEmailContract.Presenter() {

    override fun onViewCreated(email: String, firstLaunch: Boolean) {
        if (email.isEmpty() && firstLaunch) {
            addDisposable(mDataManager.getDbProfile()
                    .firstElement()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { view?.showEmail(it.email) })
        }
    }

    override fun onButtonDoneClick(email: String) {
        if (validateEmail(email)) {
            mDataManager.setBackupEmail(email)
            view?.showEmailIsCorrect(email)
        }
    }

    private fun validateEmail(email: String): Boolean {
        return when {
            email.isEmpty() -> {
                view?.showErrorEmptyEmail()
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                view?.showErrorEmailFormat()
                return false
            }
            else -> true
        }
    }
}