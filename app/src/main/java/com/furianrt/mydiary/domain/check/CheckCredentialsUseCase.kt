/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.check

import android.util.Patterns
import com.furianrt.mydiary.model.gateway.device.DeviceGateway
import com.furianrt.mydiary.model.gateway.profile.ProfileGateway
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class CheckCredentialsUseCase @Inject constructor(
        private val profileGateway: ProfileGateway,
        private val deviceGateway: DeviceGateway
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

    operator fun invoke(email: String, password: String, passwordRepeat: String): Completable =
            Single.fromCallable { deviceGateway.isNetworkAvailable() }
                    .flatMap { networkAvailable ->
                        if (networkAvailable) {
                            Single.fromCallable { validateEmail(email) }
                        } else {
                            throw NetworkNotAvailableException()
                        }
                    }
                    .flatMap { Single.fromCallable { validatePassword(password, passwordRepeat) } }
                    .flatMap { profileGateway.isProfileExists(email) }
                    .flatMapCompletable { exist ->
                        if (exist) {
                            throw EmailExistException()
                        } else {
                            Completable.complete()
                        }
                    }

    operator fun invoke(email: String): Completable =
            Single.fromCallable { validateEmail(email) }
                    .flatMap { profileGateway.isProfileExists(email) }
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