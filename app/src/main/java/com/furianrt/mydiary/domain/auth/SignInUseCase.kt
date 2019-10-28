/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.auth

import com.furianrt.mydiary.model.repository.device.DeviceRepository
import com.furianrt.mydiary.model.repository.profile.ProfileRepository
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