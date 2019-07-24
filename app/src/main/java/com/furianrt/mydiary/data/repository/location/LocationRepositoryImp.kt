package com.furianrt.mydiary.data.repository.location

import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.database.NoteDatabase
import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.model.NoteLocation
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

class LocationRepositoryImp @Inject constructor(
        private val database: NoteDatabase,
        private val prefs: PreferencesHelper,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val rxScheduler: Scheduler
) : LocationRepository {

    override fun insertNoteLocation(noteLocation: NoteLocation): Completable =
            database.noteLocationDao().insert(noteLocation)
                    .subscribeOn(rxScheduler)

    override fun insertNoteLocation(noteLocation: List<NoteLocation>): Completable =
            database.noteLocationDao().insert(noteLocation)
                    .subscribeOn(rxScheduler)

    override fun insertLocation(location: MyLocation): Completable =
            database.locationDao().insert(location)
                    .subscribeOn(rxScheduler)

    override fun insertLocation(locations: List<MyLocation>): Completable =
            database.locationDao().insert(locations)
                    .subscribeOn(rxScheduler)

    override fun updateNoteLocationsSync(noteLocations: List<NoteLocation>): Completable =
            database.noteLocationDao().update(noteLocations)
                    .subscribeOn(rxScheduler)

    override fun updateLocationsSync(locations: List<MyLocation>): Completable =
            database.locationDao().update(locations)
                    .subscribeOn(rxScheduler)

    override fun cleanupLocations(): Completable =
            database.locationDao().cleanup()
                    .subscribeOn(rxScheduler)

    override fun cleanupNoteLocations(): Completable =
            database.noteLocationDao().cleanup()
                    .subscribeOn(rxScheduler)

    override fun getDeletedNoteLocations(): Flowable<List<NoteLocation>> =
            database.noteLocationDao()
                    .getDeletedNoteLocations()
                    .subscribeOn(rxScheduler)

    override fun getDeletedLocations(): Flowable<List<MyLocation>> =
            database.locationDao()
                    .getDeletedLocations()
                    .subscribeOn(rxScheduler)

    override fun getLocationsForNote(noteId: String): Flowable<List<MyLocation>> =
            database.noteLocationDao().getLocationsForNote(noteId)

    override fun getAllDbLocations(): Flowable<List<MyLocation>> =
            database.locationDao().getAllLocations()
                    .subscribeOn(rxScheduler)

    override fun getAllNoteLocations(): Flowable<List<NoteLocation>> =
            database.noteLocationDao()
                    .getAllNoteLocations()
                    .subscribeOn(rxScheduler)

    override fun saveNoteLocationsInCloud(noteLocations: List<NoteLocation>): Completable =
            cloud.saveNoteLocations(noteLocations, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun saveLocationsInCloud(locations: List<MyLocation>): Completable =
            cloud.saveLocations(locations, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun deleteNoteLocationsFromCloud(noteLocations: List<NoteLocation>): Completable =
            cloud.deleteNoteLocations(noteLocations, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun deleteLocationsFromCloud(locations: List<MyLocation>): Completable =
            cloud.deleteLocations(locations, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllNoteLocationsFromCloud(): Single<List<NoteLocation>> =
            cloud.getAllNoteLocations(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllLocationsFromCloud(): Single<List<MyLocation>> =
            cloud.getAllLocations(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun isLocationEnabled(): Boolean = prefs.isMapEnabled()
}