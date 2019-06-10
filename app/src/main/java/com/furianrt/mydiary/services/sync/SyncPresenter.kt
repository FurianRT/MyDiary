package com.furianrt.mydiary.services.sync

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.data.model.MyProfile
import com.furianrt.mydiary.data.model.SyncProgressMessage
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.joda.time.DateTime

class SyncPresenter(
        private val dataManager: DataManager
) : SyncContract.Presenter() {

    companion object {
        private const val PROGRESS_NOTES = 5
        private const val PROGRESS_APPEARANCE = 20
        private const val PROGRESS_CATEGORIES = 30
        private const val PROGRESS_TAGS = 40
        private const val PROGRESS_NOTE_TAGS = 50
        private const val PROGRESS_LOCATION = 60
        private const val PROGRESS_NOTE_LOCATIONS = 65
        private const val PROGRESS_FORECAST = 70
        private const val PROGRESS_IMAGES = 80
        private const val PROGRESS_CLEANUP = 95
        private const val PROGRESS_FINISHED = 100
    }

    private class SyncGoneWrongException(val taskIndex: Int) : Throwable()

    private lateinit var mProfile: MyProfile

    override fun onStartCommand() {
        view?.sendProgressUpdate(SyncProgressMessage(SyncProgressMessage.PROFILE_CHECK))
        addDisposable(Observable.concat(listOf(
                getProfile(),
                syncNotes(),
                syncAppearance(),
                syncCategories(),
                syncTags(),
                syncNoteTags(),
                syncLocations(),
                syncNoteLocations(),
                syncForecast(),
                syncImages(),
                cleanup()
        )).doOnComplete {
            view?.close()
        }.subscribe({
            view?.sendProgressUpdate(it)
            dataManager.setLastSyncMessage(it)
        }, {
            it.printStackTrace()
            view?.sendProgressUpdate(if (it is SyncGoneWrongException) {
                SyncProgressMessage(taskIndex = it.taskIndex, hasError = true)
            } else {
                SyncProgressMessage(taskIndex = SyncProgressMessage.UNKNOWN, hasError = true)
            })
            view?.close()
        }))
    }

    override fun detachView() {
        super.detachView()
        dataManager.setLastSyncMessage(SyncProgressMessage(SyncProgressMessage.SYNC_FINISHED))
    }

    private fun getProfile(): Observable<SyncProgressMessage> =
            dataManager.getDbProfile()
                    .firstOrError()
                    .flatMapObservable { profile ->
                        mProfile = profile
                        Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_NOTES, PROGRESS_NOTES))
                    }
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.PROFILE_CHECK)))

    private fun syncNotes(): Observable<SyncProgressMessage> =
            dataManager.getAllNotes()
                    .first(emptyList())
                    .map { notes -> notes.filter { !it.isSync(mProfile.email) } }
                    .map { notes -> notes.apply { forEach { it.syncWith.add(mProfile.email) } } }
                    .flatMapCompletable {
                        Completable.concat(listOf(
                                dataManager.saveNotesInCloud(it),
                                dataManager.updateNotesSync(it)
                        ))
                    }
                    .andThen(dataManager.getDeletedNotes().first(emptyList()))
                    .flatMapCompletable { dataManager.deleteNotesFromCloud(it) }
                    .andThen(dataManager.getAllNotesFromCloud())
                    .flatMapCompletable { dataManager.insertNote(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_APPEARANCE, PROGRESS_APPEARANCE)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_NOTES)))

    private fun syncAppearance(): Observable<SyncProgressMessage> =
            dataManager.getAllNoteAppearances()
                    .first(emptyList())
                    .map { appearances -> appearances.filter { !it.isSync(mProfile.email) } }
                    .map { appearances -> appearances.apply { forEach { it.syncWith.add(mProfile.email) } } }
                    .flatMapCompletable {
                        Completable.concat(listOf(
                                dataManager.saveAppearancesInCloud(it),
                                dataManager.updateAppearancesSync(it)
                        ))
                    }
                    .andThen(dataManager.getDeletedAppearances().first(emptyList()))
                    .flatMapCompletable { dataManager.deleteAppearancesFromCloud(it) }
                    .andThen(dataManager.getAllAppearancesFromCloud())
                    .flatMapCompletable { dataManager.insertAppearance(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_CATEGORIES, PROGRESS_CATEGORIES)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_APPEARANCE)))

    private fun syncCategories(): Observable<SyncProgressMessage> =
            dataManager.getAllCategories()
                    .first(emptyList())
                    .map { categories -> categories.filter { !it.isSync(mProfile.email) } }
                    .map { categories -> categories.apply { forEach { it.syncWith.add(mProfile.email) } } }
                    .flatMapCompletable {
                        Completable.concat(listOf(
                                dataManager.saveCategoriesInCloud(it),
                                dataManager.updateCategoriesSync(it)
                        ))
                    }
                    .andThen(dataManager.getDeletedCategories().first(emptyList()))
                    .flatMapCompletable { dataManager.deleteCategoriesFromCloud(it) }
                    .andThen(dataManager.getAllCategoriesFromCloud())
                    .flatMapCompletable { dataManager.insertCategory(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_TAGS, PROGRESS_TAGS)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_CATEGORIES)))

    private fun syncTags(): Observable<SyncProgressMessage> =
            dataManager.getAllTags()
                    .first(emptyList())
                    .map { tags -> tags.filter { !it.isSync(mProfile.email) } }
                    .map { tags -> tags.apply { forEach { it.syncWith.add(mProfile.email) } } }
                    .flatMapCompletable {
                        Completable.concat(listOf(
                                dataManager.saveTagsInCloud(it),
                                dataManager.updateTagsSync(it)
                        ))
                    }
                    .andThen(dataManager.getDeletedTags().first(emptyList()))
                    .flatMapCompletable { dataManager.deleteTagsFromCloud(it) }
                    .andThen(dataManager.getAllTagsFromCloud())
                    .flatMapCompletable { dataManager.insertTag(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_NOTE_TAGS, PROGRESS_NOTE_TAGS)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_TAGS)))

    private fun syncNoteTags(): Observable<SyncProgressMessage> =
            dataManager.getAllNoteTags()
                    .first(emptyList())
                    .map { noteTags -> noteTags.filter { !it.isSync(mProfile.email) } }
                    .map { noteTags -> noteTags.apply { forEach { it.syncWith.add(mProfile.email) } } }
                    .flatMapCompletable {
                        Completable.concat(listOf(
                                dataManager.saveNoteTagsInCloud(it),
                                dataManager.updateNoteTagsSync(it)
                        ))
                    }
                    .andThen(dataManager.getDeletedNoteTags().first(emptyList()))
                    .flatMapCompletable { dataManager.deleteNoteTagsFromCloud(it) }
                    .andThen(dataManager.getAllNoteTagsFromCloud())
                    .flatMapCompletable { dataManager.insertNoteTag(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_LOCATION, PROGRESS_LOCATION)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_NOTE_TAGS)))

    private fun syncLocations(): Observable<SyncProgressMessage> =
            dataManager.getAllDbLocations()
                    .first(emptyList())
                    .map { locations -> locations.filter { !it.isSync(mProfile.email) } }
                    .map { locations -> locations.apply { forEach { it.syncWith.add(mProfile.email) } } }
                    .flatMapCompletable {
                        Completable.concat(listOf(
                                dataManager.saveLocationsInCloud(it),
                                dataManager.updateLocationsSync(it)
                        ))
                    }
                    .andThen(dataManager.getDeletedLocations().first(emptyList()))
                    .flatMapCompletable { dataManager.deleteLocationsFromCloud(it) }
                    .andThen(dataManager.getAllLocationsFromCloud())
                    .flatMapCompletable { dataManager.insertLocation(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_NOTE_LOCATIONS, PROGRESS_NOTE_LOCATIONS)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_LOCATION)))

    private fun syncNoteLocations(): Observable<SyncProgressMessage> =
            dataManager.getAllNoteLocations()
                    .first(emptyList())
                    .map { noteLocations -> noteLocations.filter { !it.isSync(mProfile.email) } }
                    .map { noteLocations -> noteLocations.apply { forEach { it.syncWith.add(mProfile.email) } } }
                    .flatMapCompletable {
                        Completable.concat(listOf(
                                dataManager.saveNoteLocationsInCloud(it),
                                dataManager.updateNoteLocationsSync(it)
                        ))
                    }
                    .andThen(dataManager.getDeletedNoteLocations().first(emptyList()))
                    .flatMapCompletable { dataManager.deleteNoteLocationsFromCloud(it) }
                    .andThen(dataManager.getAllNoteLocationsFromCloud())
                    .flatMapCompletable { dataManager.insertNoteLocation(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_FORECAST, PROGRESS_FORECAST)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_NOTE_LOCATIONS)))

    private fun syncForecast(): Observable<SyncProgressMessage> =
            dataManager.getAllDbForecasts()
                    .map { forecasts -> forecasts.filter { !it.isSync(mProfile.email) } }
                    .map { forecasts -> forecasts.apply { forEach { it.syncWith.add(mProfile.email) } } }
                    .flatMapCompletable {
                        Completable.concat(listOf(
                                dataManager.saveForecastsInCloud(it),
                                dataManager.updateForecastsSync(it)
                        ))
                    }
                    .andThen(dataManager.getDeletedForecasts().first(emptyList()))
                    .flatMapCompletable { dataManager.deleteForecastsFromCloud(it) }
                    .andThen(dataManager.getAllForecastsFromCloud())
                    .flatMapCompletable { dataManager.insertForecast(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_IMAGES, PROGRESS_IMAGES)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_FORECAST)))

    private fun syncImages(): Observable<SyncProgressMessage> =
            dataManager.getAllImages()
                    .first(emptyList())
                    .map { images -> images.filter { !it.isSync(mProfile.email) } }
                    .map { notSyncImages -> notSyncImages.apply { forEach { it.syncWith.add(mProfile.email) } } }
                    .flatMapCompletable { images ->
                        val notSyncFiles = images.filter { !it.isFileSync(mProfile.email) }
                        notSyncFiles.forEach { it.fileSyncWith.add(mProfile.email) }
                        return@flatMapCompletable Completable.concat(listOf(
                                dataManager.saveImagesFilesInCloud(notSyncFiles),
                                dataManager.saveImagesInCloud(images),
                                dataManager.updateImageSync(images)
                        ))
                    }
                    .andThen(dataManager.getDeletedImages().first(emptyList()))
                    .flatMapCompletable { dataManager.deleteImagesFromCloud(it) }
                    .andThen(Single.zip(
                            dataManager.getAllImagesFromCloud(),
                            dataManager.getAllImages().firstOrError(),
                            BiFunction<List<MyImage>, List<MyImage>, List<MyImage>> { cloudImages, dbImages ->
                                cloudImages.toMutableList().apply {
                                    dbImages.forEach { image -> removeAll { it.name == image.name } }
                                }
                            }
                    ))
                    .flatMapCompletable {
                        Completable.concat(listOf(
                                dataManager.loadImageFiles(it),
                                dataManager.insertImages(it)
                        ))
                    }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.CLEANUP, PROGRESS_CLEANUP)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_IMAGES)))

    private fun cleanup(): Observable<SyncProgressMessage> =
            dataManager.cleanupNotes()
                    .andThen(dataManager.cleanupAppearances())
                    .andThen(dataManager.cleanupCategories())
                    .andThen(dataManager.cleanupNoteTags())
                    .andThen(dataManager.cleanupTags())
                    .andThen(dataManager.cleanupImages())
                    .andThen(dataManager.cleanupNoteLocations())
                    .andThen(dataManager.cleanupLocations())
                    .andThen(dataManager.cleanupForecasts())
                    .andThen(dataManager.getDbProfile().firstOrError())
                    .flatMapCompletable { dataManager.updateProfile(it.apply { lastSyncTime = DateTime.now().millis }) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_FINISHED, PROGRESS_FINISHED)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.CLEANUP)))
}