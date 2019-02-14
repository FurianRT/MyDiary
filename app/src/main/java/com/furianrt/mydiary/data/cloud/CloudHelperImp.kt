package com.furianrt.mydiary.data.cloud

import com.furianrt.mydiary.data.model.MyProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

class CloudHelperImp(
        private val mFirestore: FirebaseFirestore,
        private val mFirebaseStorage: FirebaseStorage
) : CloudHelper {

    companion object {
        private const val COLLECTION_USERS = "users"
    }

    override fun isProfileExists(email: String): Single<Boolean> =
            RxFirestore.getDocument(mFirestore.collection(COLLECTION_USERS)
                    .document(email)) { return@getDocument it.exists() }
                    .toSingle()
                    .onErrorReturn {
                        if (it is NoSuchElementException) {
                            return@onErrorReturn false
                        } else {
                            throw it
                        }
                    }

    override fun getProfile(email: String): Maybe<MyProfile> =
            RxFirestore.getDocument(mFirestore.collection(COLLECTION_USERS).document(email)) {
                return@getDocument it.toObject(MyProfile::class.java)
            }

    override fun createProfile(profile: MyProfile): Completable =
            RxFirestore.setDocument(
                    mFirestore.collection(COLLECTION_USERS).document(profile.email),
                    profile
            )
}