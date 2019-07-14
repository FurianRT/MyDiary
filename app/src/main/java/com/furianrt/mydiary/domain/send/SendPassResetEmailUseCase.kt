package com.furianrt.mydiary.domain.send

import android.util.Patterns
import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import io.reactivex.Completable
import javax.inject.Inject

class SendPassResetEmailUseCase @Inject constructor(
        private val profileRepository: ProfileRepository
) {

    class EmailFormatException : Throwable()
    class EmptyEmailException : Throwable()

    fun invoke(email: String): Completable {
        validateEmail(email)
        return profileRepository.sendPasswordResetEmail(email)
                .onErrorResumeNext { error ->
                    if (error is FirebaseAuthInvalidCredentialsException) {
                        Completable.error(EmailFormatException())
                    } else {
                        Completable.error(error)
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
}