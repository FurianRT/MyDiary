package com.furianrt.mydiary.data.cloud

import android.net.Uri
import android.util.Log
import com.furianrt.mydiary.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import durdinapps.rxfirebase2.RxFirebaseStorage
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class CloudHelperImp(
        private val mFirestore: FirebaseFirestore,
        private val mFirebaseStorage: FirebaseStorage
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

    override fun saveProfile(profile: MyProfile): Completable =
            RxFirestore.setDocument(
                    mFirestore.collection(COLLECTION_USERS).document(profile.email),
                    profile
            )

    override fun saveNotes(notes: List<MyNote>, profile: MyProfile): Completable =
            RxFirestore.runTransaction(mFirestore) { transaction ->
                notes.forEach { note ->
                    transaction.set(mFirestore.collection(COLLECTION_USERS)
                            .document(profile.email)
                            .collection(COLLECTION_NOTES)
                            .document(note.id), note)
                }
            }

    override fun saveCategories(categories: List<MyCategory>, profile: MyProfile): Completable =
            RxFirestore.runTransaction(mFirestore) { transaction ->
                categories.forEach { category ->
                    transaction.set(mFirestore.collection(COLLECTION_USERS)
                            .document(profile.email)
                            .collection(COLLECTION_CATEGORIES)
                            .document(category.id), category)
                }
            }

    override fun saveNoteTags(noteTags: List<NoteTag>, profile: MyProfile): Completable =
            RxFirestore.runTransaction(mFirestore) { transaction ->
                noteTags.forEach { noteTag ->
                    Log.e(TAG, "saving noteTag in cloud Id: " + noteTag.noteId + noteTag.tagId)
                    transaction.set(mFirestore.collection(COLLECTION_USERS)
                            .document(profile.email)
                            .collection(COLLECTION_NOTE_TAGS)
                            .document(noteTag.noteId + noteTag.tagId), noteTag)
                }
            }

    override fun saveTags(tags: List<MyTag>, profile: MyProfile): Completable =
            RxFirestore.runTransaction(mFirestore) { transaction ->
                tags.forEach { tag ->
                    transaction.set(mFirestore.collection(COLLECTION_USERS)
                            .document(profile.email)
                            .collection(COLLECTION_TAGS)
                            .document(tag.id), tag)
                }
            }

    override fun saveAppearances(appearances: List<MyNoteAppearance>, profile: MyProfile): Completable =
            RxFirestore.runTransaction(mFirestore) { transaction ->
                appearances.forEach { appearance ->
                    transaction.set(mFirestore.collection(COLLECTION_USERS)
                            .document(profile.email)
                            .collection(COLLECTION_APPEARANCES)
                            .document(appearance.appearanceId), appearance)
                }
            }

    override fun saveImages(images: List<MyImage>, profile: MyProfile): Completable =
            RxFirestore.runTransaction(mFirestore) { transaction ->
                images.forEach { image ->
                    transaction.set(mFirestore.collection(COLLECTION_USERS)
                            .document(profile.email)
                            .collection(COLLECTION_IMAGES)
                            .document(image.name), image)
                }
            }

    override fun saveImagesFiles(images: List<MyImage>, profile: MyProfile): Completable =
            Observable.fromIterable(images)
                    .flatMapSingle { image ->
                        RxFirebaseStorage.putFile(mFirebaseStorage.reference
                                .child(COLLECTION_USERS)
                                .child(profile.email)
                                .child(COLLECTION_NOTES)
                                .child(image.noteId)
                                .child(COLLECTION_IMAGES)
                                .child(image.name), Uri.parse(image.uri))
                                .retry(3L)
                    }
                    .collectInto(mutableListOf<UploadTask.TaskSnapshot>()) { l, i -> l.add(i) }
                    .ignoreElement()

    override fun deleteNotes(notes: List<MyNote>, profile: MyProfile): Completable =
            RxFirestore.runTransaction(mFirestore) { transaction ->
                notes.forEach { note ->
                    Log.e(TAG, "deleting note from cloud id: ${note.id}")
                    transaction.delete(mFirestore.collection(COLLECTION_USERS)
                            .document(profile.email)
                            .collection(COLLECTION_NOTES)
                            .document(note.id))
                }
            }
                    .retry(3L)
                    .onErrorComplete()

    override fun deleteCategories(categories: List<MyCategory>, profile: MyProfile): Completable =
            RxFirestore.runTransaction(mFirestore) { transaction ->
                categories.forEach { category ->
                    transaction.delete(mFirestore.collection(COLLECTION_USERS)
                            .document(profile.email)
                            .collection(COLLECTION_CATEGORIES)
                            .document(category.id))
                }
            }
                    .retry(3L)
                    .onErrorComplete()

    override fun deleteNoteTags(noteTags: List<NoteTag>, profile: MyProfile): Completable =
            RxFirestore.runTransaction(mFirestore) { transaction ->
                noteTags.forEach { noteTag ->
                    Log.e(TAG, "deleting noteTag from cloud id: " + noteTag.noteId + noteTag.tagId)
                    transaction.delete(mFirestore.collection(COLLECTION_USERS)
                            .document(profile.email)
                            .collection(COLLECTION_NOTE_TAGS)
                            .document(noteTag.noteId + noteTag.tagId))
                }
            }
                    .retry(3L)
                    .onErrorComplete()

    override fun deleteTags(tags: List<MyTag>, profile: MyProfile): Completable =
            RxFirestore.runTransaction(mFirestore) { transaction ->
                tags.forEach { tag ->
                    transaction.delete(mFirestore.collection(COLLECTION_USERS)
                            .document(profile.email)
                            .collection(COLLECTION_TAGS)
                            .document(tag.id))
                }
            }
                    .retry(3L)
                    .onErrorComplete()

    override fun deleteAppearances(appearances: List<MyNoteAppearance>, profile: MyProfile): Completable =
            RxFirestore.runTransaction(mFirestore) { transaction ->
                appearances.forEach { appearance ->
                    transaction.delete(mFirestore.collection(COLLECTION_USERS)
                            .document(profile.email)
                            .collection(COLLECTION_APPEARANCES)
                            .document(appearance.appearanceId))
                }
            }
                    .retry(3L)
                    .onErrorComplete()

    override fun deleteImages(images: List<MyImage>, profile: MyProfile): Completable =
            RxFirestore.runTransaction(mFirestore) { transaction ->
                images.forEach { images ->
                    transaction.delete(mFirestore.collection(COLLECTION_USERS)
                            .document(profile.email)
                            .collection(COLLECTION_IMAGES)
                            .document(images.name))
                }
            }
                    .retry(3L)
                    .onErrorComplete()
                    .andThen(Observable.fromIterable(images))
                    .flatMapSingle { image ->
                        RxFirebaseStorage.delete(mFirebaseStorage.reference
                                .child(COLLECTION_USERS)
                                .child(profile.email)
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

    override fun getAllNotes(profile: MyProfile): Single<List<MyNote>> =
            RxFirestore.getCollection(mFirestore.collection(COLLECTION_USERS)
                    .document(profile.email)
                    .collection(COLLECTION_NOTES), MyNote::class.java)
                    .toSingle(emptyList())

    override fun getAllCategories(profile: MyProfile): Single<List<MyCategory>> =
            RxFirestore.getCollection(mFirestore.collection(COLLECTION_USERS)
                    .document(profile.email)
                    .collection(COLLECTION_CATEGORIES), MyCategory::class.java)
                    .toSingle(emptyList())

    override fun getAllTags(profile: MyProfile): Single<List<MyTag>> =
            RxFirestore.getCollection(mFirestore.collection(COLLECTION_USERS)
                    .document(profile.email)
                    .collection(COLLECTION_TAGS), MyTag::class.java)
                    .toSingle(emptyList())

    override fun getAllAppearances(profile: MyProfile): Single<List<MyNoteAppearance>> =
            RxFirestore.getCollection(mFirestore.collection(COLLECTION_USERS)
                    .document(profile.email)
                    .collection(COLLECTION_APPEARANCES), MyNoteAppearance::class.java)
                    .toSingle(emptyList())

    override fun getAllNoteTags(profile: MyProfile): Single<List<NoteTag>> =
            RxFirestore.getCollection(mFirestore.collection(COLLECTION_USERS)
                    .document(profile.email)
                    .collection(COLLECTION_NOTE_TAGS), NoteTag::class.java)
                    .toSingle(emptyList())

    override fun getAllImages(profile: MyProfile): Single<List<MyImage>> =
            RxFirestore.getCollection(mFirestore.collection(COLLECTION_USERS)
                    .document(profile.email)
                    .collection(COLLECTION_IMAGES), MyImage::class.java)
                    .toSingle(emptyList())

    override fun loadImageFiles(profile: MyProfile, images: List<MyImage>): Completable =
            Observable.fromIterable(images)
                    .flatMapSingle { image ->
                        RxFirebaseStorage.getFile(mFirebaseStorage.reference
                                .child(COLLECTION_USERS)
                                .child(profile.email)
                                .child(COLLECTION_NOTES)
                                .child(image.noteId)
                                .child(COLLECTION_IMAGES)
                                .child(image.name), Uri.parse(image.uri))
                                .retry(3L)
                    }
                    .collectInto(mutableListOf<FileDownloadTask.TaskSnapshot>()) { l, i -> l.add(i) }
                    .ignoreElement()
}
