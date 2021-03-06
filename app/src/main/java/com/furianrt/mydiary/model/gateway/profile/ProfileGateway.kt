/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.profile

import com.furianrt.mydiary.model.entity.MyProfile
import com.google.common.base.Optional
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface ProfileGateway {

    companion object {
        const val SIGN_STATE_SIGN_OUT = 0
        const val SIGN_STATE_SIGN_IN = 1
    }

    fun insertProfile(profile: MyProfile): Completable
    fun updateDbProfile(profile: MyProfile): Completable
    fun clearDbProfile(): Completable
    fun getDbProfile(): Flowable<Optional<MyProfile>>
    fun getDbProfileCount(): Single<Int>
    fun isProfileExists(email: String): Single<Boolean>
    fun updateProfile(profile: MyProfile): Completable
    fun isSignedIn(): Boolean
    fun signUp(email: String, password: String): Completable
    fun signIn(email: String, password: String): Completable
    fun signOut(): Completable
    fun observeAuthState(): Observable<Int>
    fun updatePassword(oldPassword: String, newPassword: String): Completable
    fun sendPasswordResetEmail(email: String): Completable
}