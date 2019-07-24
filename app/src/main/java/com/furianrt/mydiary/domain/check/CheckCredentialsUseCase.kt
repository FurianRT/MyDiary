package com.furianrt.mydiary.domain.check

import android.util.Patterns
import com.furianrt.mydiary.data.repository.device.DeviceRepository
import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class CheckCredentialsUseCase @Inject constructor(
        private val profileRepository: ProfileRepository,
        private val deviceRepository: DeviceRepository
) {

    class EmailFormatException : Throwable()
    class EmptyEmailException : Throwable()
    class EmailExistException : Throwable()
    class ShortPasswordException : Throwable()
    class EmptyPasswordException : Throwable()
    class EmptyPasswordRepeatException : Throwable()
    class PasswordNotMatchException : Throwable()
    class NetworkNotAvailableException : Throwable()

    companion object {
        private const val PASSWORD_MIN_LENGTH = 6
    }

    fun invoke(email: String, password: String, passwordRepeat: String): Completable =
            Single.fromCallable { deviceRepository.isNetworkAvailable() }
                    .flatMap { networkAvailable ->
                        if (networkAvailable) {
                            Single.fromCallable { validateEmail(email) }
                        } else {
                            throw NetworkNotAvailableException()
                        }
                    }
                    .flatMap { Single.fromCallable { validatePassword(password, passwordRepeat) } }
                    .flatMap { profileRepository.isProfileExists(email) }
                    .flatMapCompletable { exist ->
                        if (exist) {
                            throw EmailExistException()
                        } else {
                            Completable.complete()
                        }
                    }

    fun invoke(email: String): Completable =
            Single.fromCallable { validateEmail(email) }
                    .flatMap { profileRepository.isProfileExists(email) }
                    .flatMapCompletable { exist ->
                        if (exist) {
                            throw EmailExistException()
                        } else {
                            Completable.complete()
                        }
                    }

    private fun validateEmail(email: String) = when {
        email.isEmpty() ->
            throw EmptyEmailException()
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            throw EmailFormatException()
        email.substring(email.lastIndexOf(".") + 1, email.length).length < 2 ->
            throw EmailFormatException()
        else -> true
    }

    private fun validatePassword(password: String, passwordRepeat: String) = when {
        password.isEmpty() -> throw EmptyPasswordException()
        passwordRepeat.isEmpty() -> throw EmptyPasswordRepeatException()
        password.length < PASSWORD_MIN_LENGTH -> throw ShortPasswordException()
        password != passwordRepeat -> throw PasswordNotMatchException()
        else -> true
    }
}