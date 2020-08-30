/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.location

import com.furianrt.mydiary.model.entity.MyLocation
import com.furianrt.mydiary.model.entity.NoteLocation
import com.furianrt.mydiary.model.source.auth.AuthSource
import com.furianrt.mydiary.model.source.cloud.CloudSource
import com.furianrt.mydiary.model.source.database.dao.LocationDao
import com.furianrt.mydiary.model.source.database.dao.NoteLocationDao
import com.furianrt.mydiary.model.source.preferences.PreferencesSource
import com.furianrt.mydiary.utils.MyRxUtils
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LocationGatewayImp @Inject constructor(
        private val locationDao: LocationDao,
        private val noteLocationDao: NoteLocationDao,
        private val prefs: PreferencesSource,
        private val cloud: CloudSource,
        private val auth: AuthSource,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : LocationGateway {

    override fun insertNoteLocation(noteLocation: NoteLocation): Completable =
            noteLocationDao.insert(noteLocation)
                    .subscribeOn(scheduler.io())

    override fun insertNoteLocation(noteLocation: List<NoteLocation>): Completable =
            noteLocationDao.insert(noteLocation)
                    .subscribeOn(scheduler.io())

    override fun insertLocation(location: MyLocation): Completable =
            locationDao.insert(location)
                    .subscribeOn(scheduler.io())

    override fun insertLocation(locations: List<MyLocation>): Completable =
            locationDao.insert(locations)
                    .subscribeOn(scheduler.io())

    override fun updateNoteLocationsSync(noteLocations: List<NoteLocation>): Completable =
            noteLocationDao.update(noteLocations)
                    .subscribeOn(scheduler.io())

    override fun updateLocationsSync(locations: List<MyLocation>): Completable =
            locationDao.update(locations)
                    .subscribeOn(scheduler.io())

    override fun deleteLocations(locationIds: List<String>): Completable =
            noteLocationDao.deleteWithLocationId(locationIds)
                    .andThen(locationDao.delete(locationIds))
                    .subscribeOn(scheduler.io())

    override fun cleanupLocations(): Completable =
            locationDao.cleanup()
                    .subscribeOn(scheduler.io())

    override fun cleanupNoteLocations(): Completable =
            noteLocationDao.cleanup()
                    .subscribeOn(scheduler.io())

    override fun getDeletedNoteLocations(): Flowable<List<NoteLocation>> =
            noteLocationDao.getDeletedNoteLocations()
                    .subscribeOn(scheduler.io())

    override fun getDeletedLocations(): Flowable<List<MyLocation>> =
            locationDao.getDeletedLocations()
                    .subscribeOn(scheduler.io())

    override fun getLocationsForNote(noteId: String): Flowable<List<MyLocation>> =
            noteLocationDao.getLocationsForNote(noteId)
                    .subscribeOn(scheduler.io())

    override fun getAllDbLocations(): Flowable<List<MyLocation>> =
            locationDao.getAllLocations()
                    .subscribeOn(scheduler.io())

    override fun getAllNoteLocations(): Flowable<List<NoteLocation>> =
            noteLocationDao.getAllNoteLocations()
                    .subscribeOn(scheduler.io())

    override fun saveNoteLocationsInCloud(noteLocations: List<NoteLocation>): Completable =
            cloud.saveNoteLocations(noteLocations, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun saveLocationsInCloud(locations: List<MyLocation>): Completable =
            cloud.saveLocations(locations, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun deleteNoteLocationsFromCloud(noteLocations: List<NoteLocation>): Completable =
            cloud.deleteNoteLocations(noteLocations, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun deleteLocationsFromCloud(locations: List<MyLocation>): Completable =
            cloud.deleteLocations(locations, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun getAllNoteLocationsFromCloud(): Single<List<NoteLocation>> =
            cloud.getAllNoteLocations(auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun getAllLocationsFromCloud(): Single<List<MyLocation>> =
            cloud.getAllLocations(auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun isLocationEnabled(): Boolean = prefs.isMapEnabled()

    override fun setLocationEnabled(enabled: Boolean) {
        prefs.setMapEnabled(enabled)
    }
}