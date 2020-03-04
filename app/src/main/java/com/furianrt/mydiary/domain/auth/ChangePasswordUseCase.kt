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

import com.furianrt.mydiary.model.gateway.device.DeviceGateway
import com.furianrt.mydiary.model.gateway.profile.ProfileGateway
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
        private val profileGateway: ProfileGateway,
        private val deviceGateway: DeviceGateway
) {

    class EmptyOldPasswordException : Throwable()
    class EmptyNewPasswordRepeatException : Throwable()
    class EmptyRepeatPasswordRepeatException : Throwable()
    class ShortNewPasswordException : Throwable()
    class WrongPasswordRepeatException : Throwable()
    class WrongOldPasswordException : Throwable()
    class InvalidUserExceptionException : Throwable()
    class NetworkNotAvailableException : Throwable()

    companion object {
        private const val PASSWORD_MIN_LENGTH = 6
    }

    operator fun invoke(oldPassword: String, newPassword: String, repeatPassword: String): Completable =
            Single.fromCallable { deviceGateway.isNetworkAvailable() }
                    .flatMap { networkAvailable ->
                        if (networkAvailable) {
                            Single.fromCallable { validateCredentials(oldPassword, newPassword, repeatPassword) }
                        } else {
                            throw NetworkNotAvailableException()
                        }
                    }
                    .flatMapCompletable { profileGateway.updatePassword(oldPassword, newPassword) }
                    .onErrorResumeNext { error ->
                        when (error) {
                            is FirebaseAuthInvalidCredentialsException -> throw WrongOldPasswordException()
                            is FirebaseAuthInvalidUserException -> throw InvalidUserExceptionException()
                            else -> Completable.error(error)
                        }
                    }

    private fun validateCredentials(oldPassword: String, newPassword: String, repeatPassword: String) =
            when {
                oldPassword.isBlank() -> throw EmptyOldPasswordException()
                newPassword.isBlank() -> throw EmptyNewPasswordRepeatException()
                repeatPassword.isBlank() -> throw EmptyRepeatPasswordRepeatException()
                newPassword.length < PASSWORD_MIN_LENGTH -> throw ShortNewPasswordException()
                newPassword != repeatPassword -> throw WrongPasswordRepeatException()
                else -> true
            }
}