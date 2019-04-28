package com.furianrt.mydiary.screens.main.fragments.profile.password

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyProfile
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.mindrot.jbcrypt.BCrypt

class PasswordPresenter(
        private val mDataManager: DataManager
) : PasswordContract.Presenter() {

    companion object {
        private const val PASSWORD_MIN_LENGTH = 4
    }

    private class WrongPasswordException : Throwable()

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
        addDisposable(mDataManager.getDbProfile()
                .firstOrError()
                .flatMapCompletable { profile ->
                    Completable.concat(listOf(
                            checkPassword(oldPassword, profile),
                            updateProfile(newPassword, profile)
                    ))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.hideLoading()
                    view?.showSuccessPasswordChange()
                }, {
                    view?.hideLoading()
                    if (it is WrongPasswordException) {
                        view?.showErrorWrongOldPassword()
                    } else {
                        view?.showErrorNetworkConnection()
                    }
                }))
    }

    private fun checkPassword(oldPassword: String, profile: MyProfile): Completable =
            Single.fromCallable { BCrypt.checkpw(oldPassword, profile.passwordHash) }
                    .flatMapCompletable { passwordValid ->
                        if (passwordValid) {
                            return@flatMapCompletable Completable.complete()
                        } else {
                            throw WrongPasswordException()
                        }
                    }

    private fun updateProfile(newPassword: String, profile: MyProfile) =
            hashPassword(newPassword)
                    .flatMapCompletable { passwordHash ->
                        profile.passwordHash = passwordHash
                        val newProfile = MyProfile(
                                profile.email,
                                passwordHash,
                                profile.photoUrl,
                                profile.registrationTime,
                                profile.lastSyncTime,
                                profile.hasPremium
                        )
                        return@flatMapCompletable Completable.concat(listOf(
                                mDataManager.saveProfile(newProfile),
                                mDataManager.updateDbProfile(newProfile)
                        ))
                    }

    private fun hashPassword(password: String): Single<String> =
            Single.fromCallable { BCrypt.hashpw(password, BCrypt.gensalt()) }
                    .subscribeOn(Schedulers.computation())
}