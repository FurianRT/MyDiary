/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.source.cloud

import android.net.Uri
import android.util.Log
import com.furianrt.mydiary.model.entity.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import durdinapps.rxfirebase2.RxFirebaseStorage
import durdinapps.rxfirebase2.RxFirestore
import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class CloudSourceImp @Inject constructor(
        private val firestore: FirebaseFirestore,
        private val firebaseStorage: FirebaseStorage
) : CloudSource {

    companion object {
        private const val TAG = "CloudHelperImp"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_NOTES = "notes"
        private const val COLLECTION_CATEGORIES = "categories"
        private const val COLLECTION_NOTE_TAGS = "note_tags"
        private const val COLLECTION_NOTE_LOCATIONS = "note_locations"
        private const val COLLECTION_TAGS = "tags"
        private const val COLLECTION_APPEARANCES = "appearances"
        private const val COLLECTION_LOCATIONS = "locations"
        private const val COLLECTION_FORECASTS = "forecasts"
        private const val COLLECTION_IMAGES = "images"
        private const val COLLECTION_SPANS = "spans"
    }

    override fun saveProfile(profile: MyProfile): Completable =
            RxFirestore.setDocument(
                    firestore.collection(COLLECTION_USERS).document(profile.id),
                    profile
            )
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun saveNotes(notes: List<MyNote>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                notes.forEach { note ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_NOTES)
                            .document(note.id), note)
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun saveCategories(categories: List<MyCategory>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                categories.forEach { category ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_CATEGORIES)
                            .document(category.id), category)
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun saveNoteTags(noteTags: List<NoteTag>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                noteTags.forEach { noteTag ->
                    Log.e(TAG, "saving noteTag in cloud Id: " + noteTag.noteId + noteTag.tagId)
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_NOTE_TAGS)
                            .document(noteTag.noteId + noteTag.tagId), noteTag)
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun saveNoteLocations(noteLocations: List<NoteLocation>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                noteLocations.forEach { noteLocation ->
                    Log.e(TAG, "saving noteLocation in cloud Id: " + noteLocation.noteId + noteLocation.locationId)
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_NOTE_LOCATIONS)
                            .document(noteLocation.noteId + noteLocation.locationId), noteLocation)
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun saveTags(tags: List<MyTag>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                tags.forEach { tag ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_TAGS)
                            .document(tag.id), tag)
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun saveAppearances(appearances: List<MyNoteAppearance>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                appearances.forEach { appearance ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_APPEARANCES)
                            .document(appearance.appearanceId), appearance)
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun saveLocations(locations: List<MyLocation>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                locations.forEach { location ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_LOCATIONS)
                            .document(location.id), location)
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun saveForecasts(forecasts: List<MyForecast>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                forecasts.forEach { forecast ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_FORECASTS)
                            .document(forecast.noteId), forecast)
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun saveImages(images: List<MyImage>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                images.forEach { image ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_IMAGES)
                            .document(image.name), image)
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun saveImagesFiles(images: List<MyImage>, userId: String): Completable =
            Observable.fromIterable(images)
                    .flatMapSingle { image ->
                        RxFirebaseStorage.putFile(firebaseStorage.reference
                                .child(COLLECTION_USERS)
                                .child(userId)
                                .child(COLLECTION_NOTES)
                                .child(image.noteId)
                                .child(COLLECTION_IMAGES)
                                .child(image.name), Uri.parse(image.path))
                                .`as` { RxJavaBridge.toV3Single(it) }
                    }
                    .collectInto(mutableListOf<UploadTask.TaskSnapshot>()) { l, i -> l.add(i) }
                    .ignoreElement()

    override fun saveTextSpans(textSpans: List<MyTextSpan>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                textSpans.forEach { textSpan ->
                    transaction.set(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_SPANS)
                            .document(textSpan.id), textSpan)
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun deleteNotes(notes: List<MyNote>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                notes.forEach { note ->
                    Log.e(TAG, "deleting note from cloud id: ${note.id}")
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_NOTES)
                            .document(note.id))
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun deleteCategories(categories: List<MyCategory>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                categories.forEach { category ->
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_CATEGORIES)
                            .document(category.id))
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun deleteNoteTags(noteTags: List<NoteTag>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                noteTags.forEach { noteTag ->
                    Log.e(TAG, "deleting noteTag from cloud id: " + noteTag.noteId + noteTag.tagId)
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_NOTE_TAGS)
                            .document(noteTag.noteId + noteTag.tagId))
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun deleteNoteLocations(noteLocations: List<NoteLocation>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                noteLocations.forEach { noteLocation ->
                    Log.e(TAG, "deleting NoteLocation from cloud id: " + noteLocation.noteId + noteLocation.locationId)
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_NOTE_LOCATIONS)
                            .document(noteLocation.noteId + noteLocation.locationId))
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun deleteLocations(locations: List<MyLocation>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                locations.forEach { location ->
                    Log.e(TAG, "deleting location from cloud name: " + location.name)
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_LOCATIONS)
                            .document(location.id))
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun deleteForecasts(forecasts: List<MyForecast>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                forecasts.forEach { forecast ->
                    Log.e(TAG, "deleting forecast from cloud id: " + forecast.noteId)
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_FORECASTS)
                            .document(forecast.noteId))
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun deleteTags(tags: List<MyTag>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                tags.forEach { tag ->
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_TAGS)
                            .document(tag.id))
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun deleteAppearances(appearances: List<MyNoteAppearance>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                appearances.forEach { appearance ->
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_APPEARANCES)
                            .document(appearance.appearanceId))
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun deleteImages(images: List<MyImage>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                images.forEach { images ->
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_IMAGES)
                            .document(images.name))
                }
            }
                    .onErrorComplete()
                    .`as` { RxJavaBridge.toV3Completable(it) }
                    .andThen(Observable.fromIterable(images))
                    .flatMapSingle { image ->
                        RxFirebaseStorage.delete(firebaseStorage.reference
                                .child(COLLECTION_USERS)
                                .child(userId)
                                .child(COLLECTION_NOTES)
                                .child(image.noteId)
                                .child(COLLECTION_IMAGES)
                                .child(image.name))
                                .`as` { RxJavaBridge.toV3Completable(it) }
                                .toSingleDefault(true)
                                .onErrorReturn { false }
                    }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()

    override fun deleteTextSpans(textSpans: List<MyTextSpan>, userId: String): Completable =
            RxFirestore.runTransaction(firestore) { transaction ->
                textSpans.forEach { textSpan ->
                    transaction.delete(firestore.collection(COLLECTION_USERS)
                            .document(userId)
                            .collection(COLLECTION_SPANS)
                            .document(textSpan.id))
                }
            }
                    .`as` { RxJavaBridge.toV3Completable(it) }

    override fun getProfile(userId: String): Maybe<MyProfile> =
            RxFirestore.getDocument(firestore.collection(COLLECTION_USERS)
                    .document(userId), MyProfile::class.java)
                    .`as` { RxJavaBridge.toV3Maybe(it) }

    override fun getAllNotes(userId: String): Single<List<MyNote>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_NOTES), MyNote::class.java)
                    .`as` { RxJavaBridge.toV3Maybe(it) }
                    .defaultIfEmpty(emptyList())

    override fun getAllCategories(userId: String): Single<List<MyCategory>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_CATEGORIES), MyCategory::class.java)
                    .`as` { RxJavaBridge.toV3Maybe(it) }
                    .defaultIfEmpty(emptyList())

    override fun getAllTags(userId: String): Single<List<MyTag>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_TAGS), MyTag::class.java)
                    .`as` { RxJavaBridge.toV3Maybe(it) }
                    .defaultIfEmpty(emptyList())

    override fun getAllAppearances(userId: String): Single<List<MyNoteAppearance>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_APPEARANCES), MyNoteAppearance::class.java)
                    .`as` { RxJavaBridge.toV3Maybe(it) }
                    .defaultIfEmpty(emptyList())

    override fun getAllNoteTags(userId: String): Single<List<NoteTag>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_NOTE_TAGS), NoteTag::class.java)
                    .`as` { RxJavaBridge.toV3Maybe(it) }
                    .defaultIfEmpty(emptyList())

    override fun getAllNoteLocations(userId: String): Single<List<NoteLocation>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_NOTE_LOCATIONS), NoteLocation::class.java)
                    .`as` { RxJavaBridge.toV3Maybe(it) }
                    .defaultIfEmpty(emptyList())

    override fun getAllLocations(userId: String): Single<List<MyLocation>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_LOCATIONS), MyLocation::class.java)
                    .map { locations -> locations.distinctBy { it.id } }
                    .`as` { RxJavaBridge.toV3Maybe(it) }
                    .defaultIfEmpty(emptyList())

    override fun getAllForecasts(userId: String): Single<List<MyForecast>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_FORECASTS), MyForecast::class.java)
                    .`as` { RxJavaBridge.toV3Maybe(it) }
                    .defaultIfEmpty(emptyList())

    override fun getAllImages(userId: String): Single<List<MyImage>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_IMAGES), MyImage::class.java)
                    .`as` { RxJavaBridge.toV3Maybe(it) }
                    .defaultIfEmpty(emptyList())

    override fun loadImageFiles(images: List<MyImage>, userId: String): Completable =
            Observable.fromIterable(images)
                    .flatMapSingle { image ->
                        RxFirebaseStorage.getFile(firebaseStorage.reference
                                .child(COLLECTION_USERS)
                                .child(userId)
                                .child(COLLECTION_NOTES)
                                .child(image.noteId)
                                .child(COLLECTION_IMAGES)
                                .child(image.name), Uri.parse(image.path))
                                .`as` { RxJavaBridge.toV3Single(it) }
                    }
                    .collectInto(mutableListOf<FileDownloadTask.TaskSnapshot>()) { l, i -> l.add(i) }
                    .ignoreElement()

    override fun getAllTextSpans(userId: String): Single<List<MyTextSpan>> =
            RxFirestore.getCollection(firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_SPANS), MyTextSpan::class.java)
                    .`as` { RxJavaBridge.toV3Maybe(it) }
                    .defaultIfEmpty(emptyList())
}
