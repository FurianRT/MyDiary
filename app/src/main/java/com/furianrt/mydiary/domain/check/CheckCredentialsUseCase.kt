package com.furianrt.mydiary.domain.check

import android.util.Patterns
import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import io.reactivex.Completable
import javax.inject.Inject

class CheckCredentialsUseCase @Inject constructor(
        private val profileRepository: ProfileRepository
) {

    class EmailFormatException : Throwable()
    class EmptyEmailException : Throwable()
    class EmailExistException : Throwable()
    class ShortPasswordException : Throwable()
    class EmptyPasswordException : Throwable()
    class EmptyPasswordRepeatException : Throwable()
    class PasswordNotMatchException : Throwable()

    companion object {
        private const val PASSWORD_MIN_LENGTH = 6
    }

    fun invoke(email: String, password: String, passwordRepeat: String): Completable =
            Completable.fromAction {
                validateEmail(email)
                validatePassword(password, passwordRepeat)
            }.andThen(profileRepository.isProfileExists(email))
                    .flatMapCompletable { exist ->
                        if (exist) {
                            throw EmailExistException()
                        } else {
                            Completable.complete()
                        }
                    }

    fun invoke(email: String): Completable {
        validateEmail(email)
        return profileRepository.isProfileExists(email)
                .flatMapCompletable { exist ->
                    if (exist) {
                        throw EmailExistException()
                    } else {
                        Completable.complete()
                    }
                }
    }

    private fun validateEmail(email: String) {
        when {
            email.isEmpty() -> throw EmptyEmailException()
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> throw EmailFormatException()
            email.substring(email.lastIndexOf(".") + 1, email.length).length < 2 ->
                throw EmailFormatException()
        }
    }

    private fun validatePassword(password: String, passwordRepeat: String) {
        when {
            password.isEmpty() -> throw EmptyPasswordException()
            passwordRepeat.isEmpty() -> throw EmptyPasswordRepeatException()
            password.length < PASSWORD_MIN_LENGTH -> throw ShortPasswordException()
            password != passwordRepeat -> throw PasswordNotMatchException()
        }
    }
}