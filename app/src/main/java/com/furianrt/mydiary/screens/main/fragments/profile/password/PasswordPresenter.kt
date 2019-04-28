package com.furianrt.mydiary.screens.main.fragments.profile.password

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyProfile
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import org.mindrot.jbcrypt.BCrypt

class PasswordPresenter(
        private val dataManager: DataManager
) : PasswordContract.Presenter() {

    companion object {
        private const val PASSWORD_MIN_LENGTH = 6
    }

    override fun onButtonCancelClick() {
        view?.returnToMenuView()
    }

    override fun onButtonSaveClick(oldPassword: String, newPassword: String, repeatPassword: String) {
        view?.clearErrorMessage()
        when {
            view?.isNetworkAvailable() != true -> view?.showErrorNetworkConnection()
            oldPassword.isEmpty() -> view?.showErrorEmptyOldPassword()
            newPassword.isEmpty() -> view?.showErrorEmptyNewPassword()
            repeatPassword.isEmpty() -> view?.showErrorEmptyRepeatPassword()
            newPassword.length < PASSWORD_MIN_LENGTH -> view?.showErrorShortNewPassword()
            newPassword != repeatPassword -> view?.showErrorWrongPasswordRepeat()
            else -> validateOldPassword(oldPassword, newPassword)
        }
    }


    private fun validateOldPassword(oldPassword: String, newPassword: String) {
        view?.showLoading()
        addDisposable(dataManager.updatePassword(oldPassword, newPassword)
                .subscribe({
                    view?.hideLoading()
                    view?.showSuccessPasswordChange()
                }, {
                    view?.hideLoading()
                    if (it is FirebaseAuthInvalidCredentialsException) {
                        view?.showErrorWrongOldPassword()
                    } else {
                        it.printStackTrace()
                        view?.showErrorNetworkConnection()
                    }
                }))
    }
}