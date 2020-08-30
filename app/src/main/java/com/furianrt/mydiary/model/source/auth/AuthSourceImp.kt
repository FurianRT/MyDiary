/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.source.auth

import android.content.Context
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.R
import com.furianrt.mydiary.model.source.auth.entity.MyUser
import com.furianrt.mydiary.di.application.modules.app.AppContext
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import durdinapps.rxfirebase2.RxFirebaseAuth
import durdinapps.rxfirebase2.RxFirebaseUser
import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class AuthSourceImp @Inject constructor(
        @AppContext private val context: Context,
        private val firebaseAuth: FirebaseAuth
) : AuthSource {

    private class UserSignedOutException : Throwable()

    override fun getUserId(): String =
            firebaseAuth.currentUser?.uid ?: throw UserSignedOutException()

    override fun signUp(email: String, password: String): Single<MyUser> =
            RxFirebaseAuth.createUserWithEmailAndPassword(firebaseAuth, email, password)
                    .`as` { RxJavaBridge.toV3Maybe(it) }
                    .toSingle()
                    .map { it.user!! }
                    .map { MyUser(it.uid, it.email!!, it.photoUrl?.toString()) }

    override fun signIn(email: String, password: String): Single<String> =
            RxFirebaseAuth.signInWithEmailAndPassword(firebaseAuth, email, password)
                    .`as` { RxJavaBridge.toV3Maybe(it) }
                    .toSingle()
                    .map { it.user!!.uid }

    override fun signOut(): Completable = Completable.fromAction { firebaseAuth.signOut() }

    override fun observeAuthState(): Observable<Int> =
            RxFirebaseAuth.observeAuthState(firebaseAuth)
                    .`as` { RxJavaBridge.toV3Observable(it) }
                    .map { auth ->
                        if (auth.currentUser == null) {
                            AuthSource.STATE_SIGN_OUT
                        } else {
                            AuthSource.STATE_SIGN_IN
                        }
                    }

    override fun isSignedIn(): Boolean = firebaseAuth.currentUser != null

    override fun updatePassword(oldPassword: String, newPassword: String): Completable {
        val currentUser = firebaseAuth.currentUser ?: throw UserSignedOutException()
        val email = currentUser.email ?: throw UserSignedOutException()
        val userCredential = EmailAuthProvider.getCredential(email, oldPassword)
        return RxFirebaseUser.reauthenticateAndRetrieveData(currentUser, userCredential)
                .`as` { RxJavaBridge.toV3Maybe(it) }
                .flatMapCompletable { result ->
                    val user = result.user ?: throw UserSignedOutException()
                    RxFirebaseUser.updatePassword(user, newPassword)
                            .`as` { RxJavaBridge.toV3Completable(it) }
                }
    }

    override fun isProfileExists(email: String): Single<Boolean> =
            RxFirebaseAuth.fetchSignInMethodsForEmail(firebaseAuth, email)
                    .`as` { RxJavaBridge.toV3Maybe(it) }
                    .toSingle()
                    .map { !it.signInMethods.isNullOrEmpty() }

    override fun sendPasswordResetEmail(email: String): Completable =
            RxFirebaseAuth.sendPasswordResetEmail(firebaseAuth, email)
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun sendPinResetEmail(email: String, pin: String) {
        val props = Properties()
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.socketFactory.port"] = "465"
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.port"] = "465"

        val session = Session.getDefaultInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication() = PasswordAuthentication(
                    BuildConfig.SUPPORT_EMAIL,
                    BuildConfig.SUPPORT_EMAIL_PASSWORD
            )
        })

        val message = MimeMessage(session)
        message.setFrom(InternetAddress(BuildConfig.SUPPORT_EMAIL))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
        message.subject = context.getString(R.string.pin_recovery_email_subject)
        message.setText(context.getString(R.string.pin_recovery_email_content, pin))
        Transport.send(message)
    }
}