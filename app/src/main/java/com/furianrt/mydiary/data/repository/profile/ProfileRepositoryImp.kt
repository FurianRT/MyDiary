package com.furianrt.mydiary.data.repository.profile

import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.database.ProfileDao
import com.furianrt.mydiary.data.model.MyProfile
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

class ProfileRepositoryImp @Inject constructor(
        private val profileDao: ProfileDao,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val rxScheduler: Scheduler
) : ProfileRepository {

    override fun insertProfile(profile: MyProfile): Completable =
            profileDao.insert(profile)
                    .subscribeOn(rxScheduler)

    override fun updateDbProfile(profile: MyProfile): Completable =
            profileDao.update(profile)
                    .subscribeOn(rxScheduler)

    override fun clearDbProfile(): Completable =
            profileDao.clearProfile()
                    .subscribeOn(rxScheduler)

    override fun getDbProfile(): Observable<MyProfile> =
            profileDao.getProfile()
                    .subscribeOn(rxScheduler)

    override fun isProfileExists(email: String): Single<Boolean> =
            auth.isProfileExists(email)
                    .subscribeOn(rxScheduler)

    override fun getDbProfileCount(): Single<Int> =
            profileDao.getProfileCount()
                    .subscribeOn(rxScheduler)

    override fun updateProfile(profile: MyProfile): Completable =
            cloud.saveProfile(profile)
                    .andThen(profileDao.update(profile).subscribeOn(rxScheduler))
                    .subscribeOn(rxScheduler)

    override fun observeAuthState(): Observable<Int> =
            auth.observeAuthState()
                    .map {
                        if (it == AuthHelper.STATE_SIGN_OUT) {
                            ProfileRepository.SIGN_STATE_SIGN_OUT
                        } else {
                            ProfileRepository.SIGN_STATE_SIGN_IN
                        }
                    }
                    .subscribeOn(rxScheduler)

    override fun isSignedIn(): Boolean = auth.isSignedIn()

    override fun signUp(email: String, password: String): Completable =
            auth.signUp(email, password)
                    .map { MyProfile(it.id, it.email, it.photoUri) }
                    .flatMapCompletable { profile ->
                        Completable.concat(listOf(
                                cloud.saveProfile(profile).doOnError { auth.signOut() }
                                        .subscribeOn(rxScheduler),
                                profileDao.insert(profile).doOnError { auth.signOut() }
                                        .subscribeOn(rxScheduler)
                        ))
                    }
                    .subscribeOn(rxScheduler)

    override fun signIn(email: String, password: String): Completable =
            auth.signIn(email, password)
                    .flatMap { userId ->
                        cloud.getProfile(userId)
                                .switchIfEmpty(cloud.saveProfile(MyProfile(userId, email))
                                        .andThen(cloud.getProfile(userId)))
                                .toSingle()
                                .doOnError { auth.signOut() }
                    }
                    .flatMapCompletable { profile ->
                        profileDao.insert(profile)
                                .doOnError { auth.signOut() }
                                .subscribeOn(rxScheduler)
                    }
                    .subscribeOn(rxScheduler)

    override fun signOut(): Completable =
            auth.signOut()
                    .andThen(profileDao.clearProfile())
                    .subscribeOn(rxScheduler)

    override fun updatePassword(oldPassword: String, newPassword: String): Completable =
            auth.updatePassword(oldPassword, newPassword)
                    .subscribeOn(rxScheduler)

    override fun sendPasswordResetEmail(email: String): Completable =
            auth.sendPasswordResetEmail(email)
                    .subscribeOn(rxScheduler)
}