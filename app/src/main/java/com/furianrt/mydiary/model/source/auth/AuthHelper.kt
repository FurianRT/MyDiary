/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

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

import com.furianrt.mydiary.model.source.auth.entity.MyUser
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface AuthHelper {
    companion object {
        const val STATE_SIGN_OUT = 0
        const val STATE_SIGN_IN = 1
    }
    fun getUserId(): String
    fun signUp(email: String, password: String): Single<MyUser>
    fun signIn(email: String, password: String): Single<String>
    fun signOut(): Completable
    fun updatePassword(oldPassword: String, newPassword: String): Completable
    fun isProfileExists(email: String): Single<Boolean>
    fun observeAuthState(): Observable<Int>
    fun isSignedIn(): Boolean
    fun sendPasswordResetEmail(email: String): Completable
    fun sendPinResetEmail(email: String, pin: String)
}