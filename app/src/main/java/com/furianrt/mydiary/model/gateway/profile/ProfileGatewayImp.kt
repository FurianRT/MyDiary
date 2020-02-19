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
import com.furianrt.mydiary.model.source.auth.AuthHelper
import com.furianrt.mydiary.model.source.cloud.CloudHelper
import com.furianrt.mydiary.model.source.database.dao.ProfileDao
import com.furianrt.mydiary.utils.MyRxUtils
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class ProfileGatewayImp @Inject constructor(
        private val profileDao: ProfileDao,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : ProfileGateway {

    override fun insertProfile(profile: MyProfile): Completable =
            profileDao.insert(profile)
                    .subscribeOn(scheduler.io())

    override fun updateDbProfile(profile: MyProfile): Completable =
            profileDao.update(profile)
                    .subscribeOn(scheduler.io())

    override fun clearDbProfile(): Completable =
            profileDao.clearProfile()
                    .subscribeOn(scheduler.io())

    override fun getDbProfile(): Flowable<MyProfile> =
            profileDao.getProfile()
                    .subscribeOn(scheduler.io())

    override fun isProfileExists(email: String): Single<Boolean> =
            auth.isProfileExists(email)
                    .subscribeOn(scheduler.io())

    override fun getDbProfileCount(): Single<Int> =
            profileDao.getProfileCount()
                    .subscribeOn(scheduler.io())

    override fun updateProfile(profile: MyProfile): Completable =
            cloud.saveProfile(profile)
                    .andThen(profileDao.update(profile).subscribeOn(scheduler.io()))
                    .subscribeOn(scheduler.io())

    override fun observeAuthState(): Observable<Int> =
            auth.observeAuthState()
                    .map {
                        if (it == AuthHelper.STATE_SIGN_OUT) {
                            ProfileGateway.SIGN_STATE_SIGN_OUT
                        } else {
                            ProfileGateway.SIGN_STATE_SIGN_IN
                        }
                    }
                    .subscribeOn(scheduler.io())

    override fun isSignedIn(): Boolean = auth.isSignedIn()

    override fun signUp(email: String, password: String): Completable =
            auth.signUp(email, password)
                    .map { MyProfile(it.id, it.email, it.photoUri) }
                    .flatMapCompletable { profile ->
                        Completable.concat(listOf(
                                cloud.saveProfile(profile).doOnError { auth.signOut() }
                                        .subscribeOn(scheduler.io()),
                                profileDao.insert(profile).doOnError { auth.signOut() }
                                        .subscribeOn(scheduler.io())
                        ))
                    }

    override fun signIn(email: String, password: String): Completable =
            auth.signIn(email, password)
                    .flatMap { userId ->
                        cloud.getProfile(userId)
                                .switchIfEmpty(cloud.saveProfile(MyProfile(userId, email))
                                        .andThen(cloud.getProfile(userId)))
                                .toSingle()
                                .doOnError { auth.signOut() }
                                .subscribeOn(scheduler.io())
                    }
                    .flatMapCompletable { profile ->
                        profileDao.insert(profile)
                                .doOnError { auth.signOut() }
                                .subscribeOn(scheduler.io())
                    }

    override fun signOut(): Completable =
            auth.signOut()
                    .andThen(profileDao.clearProfile())
                    .subscribeOn(scheduler.io())

    override fun updatePassword(oldPassword: String, newPassword: String): Completable =
            auth.updatePassword(oldPassword, newPassword)
                    .subscribeOn(scheduler.io())

    override fun sendPasswordResetEmail(email: String): Completable =
            auth.sendPasswordResetEmail(email)
                    .subscribeOn(scheduler.io())
}