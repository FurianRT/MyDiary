/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.send

import android.util.Patterns
import com.furianrt.mydiary.model.gateway.profile.ProfileGateway
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class SendPassResetEmailUseCase @Inject constructor(
        private val profileGateway: ProfileGateway
) {

    class EmailFormatException : Throwable()
    class EmptyEmailException : Throwable()

    fun invoke(email: String): Completable =
            Single.fromCallable { validateEmail(email) }
                    .flatMapCompletable { profileGateway.sendPasswordResetEmail(email) }
                    .onErrorResumeNext { error ->
                        if (error is FirebaseAuthInvalidCredentialsException) {
                            Completable.error(EmailFormatException())
                        } else {
                            Completable.error(error)
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
}