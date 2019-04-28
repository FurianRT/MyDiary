package com.furianrt.mydiary.data.auth

import com.furianrt.mydiary.data.model.pojo.MyUser
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import durdinapps.rxfirebase2.RxFirebaseAuth
import durdinapps.rxfirebase2.RxFirebaseUser
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class AuthHelperImp(
        private val firebaseAuth: FirebaseAuth
) : AuthHelper {

    private class UserSignedOutException : Throwable()

    override fun getUserId(): String =
            firebaseAuth.currentUser?.uid ?: throw UserSignedOutException()

    override fun signUp(email: String, password: String): Single<MyUser> =
            RxFirebaseAuth.createUserWithEmailAndPassword(firebaseAuth, email, password)
                    .toSingle()
                    .map { it.user }
                    .map { MyUser(it.uid, it.email ?: "", it.photoUrl?.toString()) }

    override fun signIn(email: String, password: String): Single<String> =
            RxFirebaseAuth.signInWithEmailAndPassword(firebaseAuth, email, password)
                    .toSingle()
                    .map { it.user.uid }

    override fun signOut(): Completable = Completable.fromAction { firebaseAuth.signOut() }

    override fun observeSignOut(): Observable<Boolean> =
            RxFirebaseAuth.observeAuthState(firebaseAuth)
                    .filter { it.currentUser == null }
                    .map { it.currentUser == null }

    override fun isSignedIn(): Boolean = firebaseAuth.currentUser != null

    override fun updatePassword(oldPassword: String, newPassword: String): Completable {
        val currentUser = firebaseAuth.currentUser!!    //todo опасное место
        val userCredential = EmailAuthProvider.getCredential(currentUser.email!!, oldPassword)
        return RxFirebaseUser.reauthenticateAndRetrieveData(currentUser, userCredential)
                .flatMapCompletable { RxFirebaseUser.updatePassword(it.user!!, newPassword) }
    }

    override fun isProfileExists(email: String): Single<Boolean> =
            RxFirebaseAuth.fetchSignInMethodsForEmail(firebaseAuth, email)
                    .toSingle()
                    .map { !it.signInMethods.isNullOrEmpty() }
}