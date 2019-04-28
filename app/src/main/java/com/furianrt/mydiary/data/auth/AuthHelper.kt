package com.furianrt.mydiary.data.auth

import com.furianrt.mydiary.data.model.pojo.MyUser
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface AuthHelper {
    fun getUserId(): String
    fun signUp(email: String, password: String): Single<MyUser>
    fun signIn(email: String, password: String): Single<String>
    fun signOut(): Completable
    fun updatePassword(oldPassword: String, newPassword: String): Completable
    fun isProfileExists(email: String): Single<Boolean>
    fun observeSignOut(): Observable<Boolean>
    fun isSignedIn(): Boolean
}