package com.furianrt.mydiary.data.cloud

import com.furianrt.mydiary.data.model.MyProfile
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface CloudHelper {

    fun isProfileExists(email: String): Single<Boolean>
    fun getProfile(email: String): Maybe<MyProfile>
    fun createProfile(profile: MyProfile): Completable
}