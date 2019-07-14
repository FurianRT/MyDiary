package com.furianrt.mydiary.usecase.auth

import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import io.reactivex.Completable
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
        private val profileRepository: ProfileRepository
) {

    class EmptyOldPasswordException : Throwable()
    class EmptyNewPasswordRepeatException : Throwable()
    class EmptyRepeatPasswordRepeatException : Throwable()
    class ShortNewPasswordException : Throwable()
    class WrongPasswordRepeatException : Throwable()
    class WrongOldPasswordException : Throwable()
    class InvalidUserExceptionException : Throwable()

    companion object {
        private const val PASSWORD_MIN_LENGTH = 6
    }

    fun invoke(oldPassword: String, newPassword: String, repeatPassword: String): Completable =
            Completable.fromAction { validateCredentials(oldPassword, newPassword, repeatPassword) }
                    .andThen(profileRepository.updatePassword(oldPassword, newPassword))
                    .onErrorResumeNext { error ->
                        when (error) {
                            is FirebaseAuthInvalidCredentialsException -> throw WrongOldPasswordException()
                            is FirebaseAuthInvalidUserException -> throw InvalidUserExceptionException()
                            else -> Completable.error(error)
                        }
                    }

    private fun validateCredentials(oldPassword: String, newPassword: String, repeatPassword: String) {
        when {
            oldPassword.isEmpty() -> throw EmptyOldPasswordException()
            newPassword.isEmpty() -> throw EmptyNewPasswordRepeatException()
            repeatPassword.isEmpty() -> throw EmptyRepeatPasswordRepeatException()
            newPassword.length < PASSWORD_MIN_LENGTH -> throw ShortNewPasswordException()
            newPassword != repeatPassword -> throw WrongPasswordRepeatException()
        }
    }
}