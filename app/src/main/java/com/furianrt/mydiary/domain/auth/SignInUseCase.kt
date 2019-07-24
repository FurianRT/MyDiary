package com.furianrt.mydiary.domain.auth

import com.furianrt.mydiary.data.repository.device.DeviceRepository
import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class SignInUseCase @Inject constructor(
        private val profileRepository: ProfileRepository,
        private val deviceRepository: DeviceRepository
) {

    class EmptyEmailException : Throwable()
    class EmptyPasswordException : Throwable()
    class InvalidCredentialsException : Throwable()
    class NetworkNotAvailableException : Throwable()

    fun invoke(email: String, password: String): Completable =
            Single.fromCallable { deviceRepository.isNetworkAvailable() }
                    .flatMap { networkAvailable ->
                        if (networkAvailable) {
                            Single.fromCallable { validateCredentials(email, password) }
                        } else {
                            throw NetworkNotAvailableException()
                        }
                    }
                    .flatMapCompletable { profileRepository.signIn(email, password) }
                    .onErrorResumeNext { error ->
                        if (error is FirebaseAuthInvalidUserException || error is FirebaseAuthInvalidCredentialsException) {
                            Completable.error(InvalidCredentialsException())
                        } else {
                            Completable.error(error)
                        }
                    }

    private fun validateCredentials(email: String, password: String) =
            when {
                email.isBlank() -> throw EmptyEmailException()
                password.isBlank() -> throw EmptyPasswordException()
                else -> true
            }
}