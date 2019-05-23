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
import java.util.concurrent.TimeUnit

class CloudHelperImp(
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
        private const val COLLECTION_LOCATIONS = "locations"
        private const val COLLECTION_FORECASTS = "forecasts"
        private const val COLLECTION_IMAGES = "images"
    }

    override fun saveProfile(profile: MyProfile): Completable =
            RxFirestore.setDocument(
                    firestore.collection(COLLECTION_USERS).document(profile.id),
                    profile
            ).timeout(1, TimeUnit.MINUTES)

    override fun saveNotes(notes: List<MyNote>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                notes.forEach { note ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_NOTES)
                            .document(note.id), note)
                }
            }.timeout(1, TimeUnit.MINUTES)

    override fun saveCategories(categories: List<MyCategory>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                categories.forEach { category ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_CATEGORIES)
                            .document(category.id), category)
                }
            }.timeout(1, TimeUnit.MINUTES)

    override fun saveNoteTags(noteTags: List<NoteTag>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                noteTags.forEach { noteTag ->
                    Log.e(TAG, "saving noteTag in cloud Id: " + noteTag.noteId + noteTag.tagId)
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_NOTE_TAGS)
                            .document(noteTag.noteId + noteTag.tagId), noteTag)
                }
            }.timeout(1, TimeUnit.MINUTES)

    override fun saveTags(tags: List<MyTag>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                tags.forEach { tag ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_TAGS)
                            .document(tag.id), tag)
                }
            }.timeout(1, TimeUnit.MINUTES)

    override fun saveAppearances(appearances: List<MyNoteAppearance>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                appearances.forEach { appearance ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_APPEARANCES)
                            .document(appearance.appearanceId), appearance)
                }
            }.timeout(1, TimeUnit.MINUTES)

    override fun saveLocations(locations: List<MyLocation>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                locations.forEach { location ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_LOCATIONS)
                            .document(location.noteId), location)
                }
            }.timeout(1, TimeUnit.MINUTES)

    override fun saveForecasts(forecasts: List<MyForecast>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                forecasts.forEach { forecast ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_FORECASTS)
                            .document(forecast.noteId), forecast)
                }
            }.timeout(1, TimeUnit.MINUTES)

    override fun saveImages(images: List<MyImage>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                images.forEach { image ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_IMAGES)
                            .document(image.name), image)
                }
            }.timeout(1, TimeUnit.MINUTES)

    override fun saveImagesFiles(images: List<MyImage>, userId: String): Completable =
            Observable.fromIterable(images)
                    .flatMapSingle { image ->
                        RxFirebaseStorage.putFile(firebaseStorage.reference
                                .child(COLLECTION_USERS)
                                .child(userId)
                                .child(COLLECTION_NOTES)
                                .child(image.noteId)
                                .child(COLLECTION_IMAGES)
                                .child(image.name), Uri.parse(image.uri))
                                .timeout(1, TimeUnit.MINUTES)
                    }
                    .collectInto(mutableListOf<UploadTask.TaskSnapshot>()) { l, i -> l.add(i) }
                    .ignoreElement()

    override fun deleteNotes(notes: List<MyNote>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                notes.forEach { note ->
                    Log.e(TAG, "deleting note from cloud id: ${note.id}")
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_NOTES)
                            .document(note.id))
                }
            }.timeout(1, TimeUnit.MINUTES).onErrorComplete()

    override fun deleteCategories(categories: List<MyCategory>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                categories.forEach { category ->
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_CATEGORIES)
                            .document(category.id))
                }
            }.timeout(1, TimeUnit.MINUTES).onErrorComplete()

    override fun deleteNoteTags(noteTags: List<NoteTag>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                noteTags.forEach { noteTag ->
                    Log.e(TAG, "deleting noteTag from cloud id: " + noteTag.noteId + noteTag.tagId)
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_NOTE_TAGS)
                            .document(noteTag.noteId + noteTag.tagId))
                }
            }.timeout(1, TimeUnit.MINUTES).onErrorComplete()

    override fun deleteLocations(locations: List<MyLocation>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                locations.forEach { location ->
                    Log.e(TAG, "deleting location from cloud id: " + location.noteId)
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_LOCATIONS)
                            .document(location.noteId))
                }
            }.timeout(1, TimeUnit.MINUTES).onErrorComplete()

    override fun deleteForecasts(forecasts: List<MyForecast>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                forecasts.forEach { forecast ->
                    Log.e(TAG, "deleting forecast from cloud id: " + forecast.noteId)
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_FORECASTS)
                            .document(forecast.noteId))
                }
            }.timeout(1, TimeUnit.MINUTES).onErrorComplete()

    override fun deleteTags(tags: List<MyTag>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                tags.forEach { tag ->
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_TAGS)
                            .document(tag.id))
                }
            }.timeout(1, TimeUnit.MINUTES).onErrorComplete()

    override fun deleteAppearances(appearances: List<MyNoteAppearance>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                appearances.forEach { appearance ->
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_APPEARANCES)
                            .document(appearance.appearanceId))
                }
            }.timeout(1, TimeUnit.MINUTES).onErrorComplete()

    override fun deleteImages(images: List<MyImage>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                images.forEach { images ->
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_IMAGES)
                            .document(images.name))
                }
            }
                    .timeout(1, TimeUnit.MINUTES)
                    .onErrorComplete()
                    .andThen(Observable.fromIterable(images))
                    .flatMapSingle { image ->
                        RxFirebaseStorage.delete(firebaseStorage.reference
                                .child(COLLECTION_USERS)
                                .child(userId)
                                .child(COLLECTION_NOTES)
                                .child(image.noteId)
                                .child(COLLECTION_IMAGES)
                                .child(image.name))
                                .timeout(1, TimeUnit.MINUTES)
                                .toSingleDefault(true)
                                .onErrorReturn { false }
                    }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()

    override fun getProfile(userId: String): Maybe<MyProfile> =
            RxFirestore.getDocument(firestore.collection(COLLECTION_USERS)
                    .document(userId), MyProfile::class.java)
                    .timeout(1, TimeUnit.MINUTES)

    override fun getAllNotes(userId: String): Single<List<MyNote>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_NOTES), MyNote::class.java)
                    .timeout(1, TimeUnit.MINUTES)
                    .toSingle(emptyList())

    override fun getAllCategories(userId: String): Single<List<MyCategory>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_CATEGORIES), MyCategory::class.java)
                    .timeout(1, TimeUnit.MINUTES)
                    .toSingle(emptyList())

    override fun getAllTags(userId: String): Single<List<MyTag>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_TAGS), MyTag::class.java)
                    .timeout(1, TimeUnit.MINUTES)
                    .toSingle(emptyList())

    override fun getAllAppearances(userId: String): Single<List<MyNoteAppearance>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_APPEARANCES), MyNoteAppearance::class.java)
                    .timeout(1, TimeUnit.MINUTES)
                    .toSingle(emptyList())

    override fun getAllNoteTags(userId: String): Single<List<NoteTag>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_NOTE_TAGS), NoteTag::class.java)
                    .timeout(1, TimeUnit.MINUTES)
                    .toSingle(emptyList())

    override fun getAllLocations(userId: String): Single<List<MyLocation>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_LOCATIONS), MyLocation::class.java)
                    .timeout(1, TimeUnit.MINUTES)
                    .toSingle(emptyList())

    override fun getAllForecasts(userId: String): Single<List<MyForecast>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_FORECASTS), MyForecast::class.java)
                    .timeout(1, TimeUnit.MINUTES)
                    .toSingle(emptyList())

    override fun getAllImages(userId: String): Single<List<MyImage>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_IMAGES), MyImage::class.java)
                    .timeout(1, TimeUnit.MINUTES)
                    .toSingle(emptyList())

    override fun loadImageFiles(images: List<MyImage>, userId: String): Completable =
            Observable.fromIterable(images)
                    .flatMapSingle { image ->
                        RxFirebaseStorage.getFile(firebaseStorage.reference
                                .child(COLLECTION_USERS)
                                .child(userId)
                                .child(COLLECTION_NOTES)
                                .child(image.noteId)
                                .child(COLLECTION_IMAGES)
                                .child(image.name), Uri.parse(image.uri))
                                .timeout(1, TimeUnit.MINUTES)
                    }
                    .collectInto(mutableListOf<FileDownloadTask.TaskSnapshot>()) { l, i -> l.add(i) }
                    .ignoreElement()
}
