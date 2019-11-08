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
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface LocationGateway {
    fun insertNoteLocation(noteLocation: NoteLocation): Completable
    fun insertNoteLocation(noteLocation: List<NoteLocation>): Completable
    fun insertLocation(locations: List<MyLocation>): Completable
    fun updateNoteLocationsSync(noteLocations: List<NoteLocation>): Completable
    fun updateLocationsSync(locations: List<MyLocation>): Completable
    fun deleteLocations(locationIds: List<String>): Completable
    fun deleteNoteLocationsFromCloud(noteLocations: List<NoteLocation>): Completable
    fun deleteLocationsFromCloud(locations: List<MyLocation>): Completable
    fun cleanupLocations(): Completable
    fun cleanupNoteLocations(): Completable
    fun getDeletedNoteLocations(): Flowable<List<NoteLocation>>
    fun getDeletedLocations(): Flowable<List<MyLocation>>
    fun getLocationsForNote(noteId: String): Flowable<List<MyLocation>>
    fun getAllNoteLocations(): Flowable<List<NoteLocation>>
    fun getAllNoteLocationsFromCloud(): Single<List<NoteLocation>>
    fun getAllLocationsFromCloud(): Single<List<MyLocation>>
    fun getAllDbLocations(): Flowable<List<MyLocation>>
    fun insertLocation(location: MyLocation): Completable
    fun saveNoteLocationsInCloud(noteLocations: List<NoteLocation>): Completable
    fun saveLocationsInCloud(locations: List<MyLocation>): Completable
    fun isLocationEnabled(): Boolean
}