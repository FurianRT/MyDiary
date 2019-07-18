package com.furianrt.mydiary.domain.auth

import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import io.reactivex.Completable
import javax.inject.Inject

class SignInUseCase @Inject constructor(
        private val profileRepository: ProfileRepository
) {

    class EmptyEmailException : Throwable()
    class EmptyPasswordException : Throwable()
    class InvalidCredentialsException : Throwable()

    fun invoke(email: String, password: String): Completable =
            Completable.fromAction { validateCredentials(email, password) }
                    .andThen(profileRepository.signIn(email, password))
                    .onErrorResumeNext { error ->
                        if (error is FirebaseAuthInvalidUserException || error is FirebaseAuthInvalidCredentialsException) {
                            Completable.error(InvalidCredentialsException())
                        } else {
                            Completable.error(error)
                        }
                    }

    private fun validateCredentials(email: String, password: String) {
        when {
            email.isEmpty() -> throw EmptyEmailException()
            password.isEmpty() -> throw EmptyPasswordException()
        }
    }
}