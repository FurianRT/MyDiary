package com.furianrt.mydiary.data.cloud

import android.net.Uri
import android.util.Log
import com.furianrt.mydiary.data.model.*
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import durdinapps.rxfirebase2.RxFirebaseAuth
import durdinapps.rxfirebase2.RxFirebaseStorage
import durdinapps.rxfirebase2.RxFirebaseUser
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.joda.time.DateTime

class CloudHelperImp(
        private val firebaseAuth: FirebaseAuth,
        private val firestore: FirebaseFirestore,
        private val firebaseStorage: FirebaseStorage
) : CloudHelper {

    companion object {
        private const val TAG = "CloudHelperImp"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_NOTES = "notes"
        private const val COLLECTION_CATEGORIES = "categories"
        private const val COLLECTION_NOTE_TAGS = "note_tags"
        private const val COLLECTION_TAGS = "tags"
        private const val COLLECTION_APPEARANCES = "appearances"
        private const val COLLECTION_IMAGES = "images"
    }

    private class UserSignedOutException : Throwable()

    private fun AuthResult.toMyProfile(creationTime: Long, lastSyncTime: Long? = null) =
            MyProfile(user.uid, user.email
                    ?: "", user.photoUrl?.toString(), creationTime, lastSyncTime)

    private fun getUserId(): String =
            firebaseAuth.currentUser?.uid ?: throw UserSignedOutException()

    override fun signUp(email: String, password: String): Single<MyProfile> =
            RxFirebaseAuth.createUserWithEmailAndPassword(firebaseAuth, email, password)
                    .toSingle()
                    .map { it.toMyProfile(DateTime.now().millis) }
                    .flatMap { profile ->
                        RxFirestore.setDocument(
                                firestore.collection(COLLECTION_USERS).document(profile.id),
                                profile
                        )
                        return@flatMap Single.just(profile)
                    }

    override fun signIn(email: String, password: String): Single<MyProfile> =
            RxFirebaseAuth.signInWithEmailAndPassword(firebaseAuth, email, password)
                    .toSingle()
                    .map { it.user.uid }
                    .flatMap { userId ->
                        RxFirestore.getDocument(firestore.collection(COLLECTION_USERS)
                                .document(userId)) { return@getDocument it.toObject(MyProfile::class.java)!! }
                                .toSingle()
                    }

    override fun signOut(): Completable = Completable.fromAction { firebaseAuth.signOut() }

    override fun observeSignOut(): Observable<Boolean> =
            RxFirebaseAuth.observeAuthState(firebaseAuth)
                    .filter { it.currentUser == null }
                    .map { it.currentUser == null }

    override fun isSignedIn(): Boolean = firebaseAuth.currentUser != null

    override fun updatePassword(oldPassword: String, newPassword: String): Completable {
        val currentUser = firebaseAuth.currentUser!!
        val userCredential = EmailAuthProvider.getCredential(currentUser.email!!, oldPassword)
        return RxFirebaseUser.reauthenticateAndRetrieveData(currentUser, userCredential)
                .flatMapCompletable { RxFirebaseUser.updatePassword(it.user!!, newPassword) }

    }

    override fun updateProfile(profile: MyProfile): Completable =
            RxFirestore.setDocument(
                    firestore.collection(COLLECTION_USERS).document(profile.id),
                    profile
            )

    override fun isProfileExists(email: String): Single<Boolean> =
            RxFirebaseAuth.fetchSignInMethodsForEmail(firebaseAuth, email)
                    .toSingle()
                    .map {
                        val ii = it
                        !it.signInMethods.isNullOrEmpty()
                    }

    override fun saveNotes(notes: List<MyNote>): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                notes.forEach { note ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(getUserId())
                            .collection(COLLECTION_NOTES)
                            .document(note.id), note)
                }
            }

    override fun saveCategories(categories: List<MyCategory>): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                categories.forEach { category ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(getUserId())
                            .collection(COLLECTION_CATEGORIES)
                            .document(category.id), category)
                }
            }

    override fun saveNoteTags(noteTags: List<NoteTag>): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                noteTags.forEach { noteTag ->
                    Log.e(TAG, "saving noteTag in cloud Id: " + noteTag.noteId + noteTag.tagId)
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(getUserId())
                            .collection(COLLECTION_NOTE_TAGS)
                            .document(noteTag.noteId + noteTag.tagId), noteTag)
                }
            }

    override fun saveTags(tags: List<MyTag>): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                tags.forEach { tag ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(getUserId())
                            .collection(COLLECTION_TAGS)
                            .document(tag.id), tag)
                }
            }

    override fun saveAppearances(appearances: List<MyNoteAppearance>): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                appearances.forEach { appearance ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(getUserId())
                            .collection(COLLECTION_APPEARANCES)
                            .document(appearance.appearanceId), appearance)
                }
            }

    override fun saveImages(images: List<MyImage>): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                images.forEach { image ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(getUserId())
                            .collection(COLLECTION_IMAGES)
                            .document(image.name), image)
                }
            }

    override fun saveImagesFiles(images: List<MyImage>): Completable =
            Observable.fromIterable(images)
                    .flatMapSingle { image ->
                        RxFirebaseStorage.putFile(firebaseStorage.reference
                                .child(COLLECTION_USERS)
                                .child(getUserId())
                                .child(COLLECTION_NOTES)
                                .child(image.noteId)
                                .child(COLLECTION_IMAGES)
                                .child(image.name), Uri.parse(image.uri))
                                .retry(3L)
                    }
                    .collectInto(mutableListOf<UploadTask.TaskSnapshot>()) { l, i -> l.add(i) }
                    .ignoreElement()

    override fun deleteNotes(notes: List<MyNote>): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                notes.forEach { note ->
                    Log.e(TAG, "deleting note from cloud id: ${note.id}")
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(getUserId())
                            .collection(COLLECTION_NOTES)
                            .document(note.id))
                }
            }
                    .retry(3L)
                    .onErrorComplete()

    override fun deleteCategories(categories: List<MyCategory>): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                categories.forEach { category ->
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(getUserId())
                            .collection(COLLECTION_CATEGORIES)
                            .document(category.id))
                }
            }
                    .retry(3L)
                    .onErrorComplete()

    override fun deleteNoteTags(noteTags: List<NoteTag>): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                noteTags.forEach { noteTag ->
                    Log.e(TAG, "deleting noteTag from cloud id: " + noteTag.noteId + noteTag.tagId)
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(getUserId())
                            .collection(COLLECTION_NOTE_TAGS)
                            .document(noteTag.noteId + noteTag.tagId))
                }
            }
                    .retry(3L)
                    .onErrorComplete()

    override fun deleteTags(tags: List<MyTag>): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                tags.forEach { tag ->
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(getUserId())
                            .collection(COLLECTION_TAGS)
                            .document(tag.id))
                }
            }
                    .retry(3L)
                    .onErrorComplete()

    override fun deleteAppearances(appearances: List<MyNoteAppearance>): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                appearances.forEach { appearance ->
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(getUserId())
                            .collection(COLLECTION_APPEARANCES)
                            .document(appearance.appearanceId))
                }
            }
                    .retry(3L)
                    .onErrorComplete()

    override fun deleteImages(images: List<MyImage>): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                images.forEach { images ->
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(getUserId())
                            .collection(COLLECTION_IMAGES)
                            .document(images.name))
                }
            }
                    .retry(3L)
                    .onErrorComplete()
                    .andThen(Observable.fromIterable(images))
                    .flatMapSingle { image ->
                        RxFirebaseStorage.delete(firebaseStorage.reference
                                .child(COLLECTION_USERS)
                                .child(getUserId())
                                .child(COLLECTION_NOTES)
                                .child(image.noteId)
                                .child(COLLECTION_IMAGES)
                                .child(image.name))
                                .retry(3L)
                                .toSingleDefault(true)
                                .onErrorReturn { false }
                    }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()

    override fun getAllNotes(): Single<List<MyNote>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(getUserId())
                    .collection(COLLECTION_NOTES), MyNote::class.java)
                    .toSingle(emptyList())

    override fun getAllCategories(): Single<List<MyCategory>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(getUserId())
                    .collection(COLLECTION_CATEGORIES), MyCategory::class.java)
                    .toSingle(emptyList())

    override fun getAllTags(): Single<List<MyTag>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(getUserId())
                    .collection(COLLECTION_TAGS), MyTag::class.java)
                    .toSingle(emptyList())

    override fun getAllAppearances(): Single<List<MyNoteAppearance>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(getUserId())
                    .collection(COLLECTION_APPEARANCES), MyNoteAppearance::class.java)
                    .toSingle(emptyList())

    override fun getAllNoteTags(): Single<List<NoteTag>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(getUserId())
                    .collection(COLLECTION_NOTE_TAGS), NoteTag::class.java)
                    .toSingle(emptyList())

    override fun getAllImages(): Single<List<MyImage>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(getUserId())
                    .collection(COLLECTION_IMAGES), MyImage::class.java)
                    .toSingle(emptyList())

    override fun loadImageFiles(images: List<MyImage>): Completable =
            Observable.fromIterable(images)
                    .flatMapSingle { image ->
                        RxFirebaseStorage.getFile(firebaseStorage.reference
                                .child(COLLECTION_USERS)
                                .child(getUserId())
                                .child(COLLECTION_NOTES)
                                .child(image.noteId)
                                .child(COLLECTION_IMAGES)
                                .child(image.name), Uri.parse(image.uri))
                                .retry(3L)
                    }
                    .collectInto(mutableListOf<FileDownloadTask.TaskSnapshot>()) { l, i -> l.add(i) }
                    .ignoreElement()
}
