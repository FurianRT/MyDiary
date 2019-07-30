/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.database

import androidx.room.*
import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.model.NoteLocation
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface NoteLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(noteLocation: NoteLocation): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(noteLocations: List<NoteLocation>): Completable

    @Update
    fun update(noteLocations: List<NoteLocation>): Completable

    @Query("UPDATE NoteLocation SET is_notelocation_deleted = 1, notelocation_sync_with = '[]' WHERE id_location IN (:locationIds)")
    fun deleteWithLocationId(locationIds: List<String>): Completable

    @Query("UPDATE NoteLocation SET is_notelocation_deleted = 1, notelocation_sync_with = '[]' WHERE id_note = :noteId")
    fun deleteWithNoteId(noteId: String): Completable

    @Query("SELECT * FROM NoteLocation WHERE is_notelocation_deleted = 0")
    fun getAllNoteLocations(): Flowable<List<NoteLocation>>

    @Query("SELECT * FROM NoteLocation WHERE is_notelocation_deleted = 1")
    fun getDeletedNoteLocations(): Flowable<List<NoteLocation>>

    @Query("SELECT Locations.* FROM Locations INNER JOIN NoteLocation ON Locations.id_location = NoteLocation.id_location AND is_notelocation_deleted = 0 WHERE id_note = :noteId AND is_location_deleted = 0")
    fun getLocationsForNote(noteId: String): Flowable<List<MyLocation>>

    @Query("DELETE FROM NoteLocation WHERE is_notelocation_deleted = 1")
    fun cleanup(): Completable
}