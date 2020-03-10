/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.source.database.dao

import androidx.room.*
import com.furianrt.mydiary.model.entity.MyLocation
import com.furianrt.mydiary.model.entity.NoteLocation
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

    @Query("UPDATE ${NoteLocation.TABLE_NAME} SET ${NoteLocation.FIELD_IS_DELETED} = 1, ${NoteLocation.FIELD_SYNC_WITH} = '[]' " +
            "WHERE ${NoteLocation.FIELD_LOCATION_ID} IN (:locationIds)")
    fun deleteWithLocationId(locationIds: List<String>): Completable

    @Query("UPDATE ${NoteLocation.TABLE_NAME} SET ${NoteLocation.FIELD_IS_DELETED} = 1, ${NoteLocation.FIELD_SYNC_WITH} = '[]' " +
            "WHERE ${NoteLocation.FIELD_NOTE_ID} = :noteId")
    fun deleteWithNoteId(noteId: String): Completable

    @Query("SELECT * FROM ${NoteLocation.TABLE_NAME} WHERE ${NoteLocation.FIELD_IS_DELETED} = 0")
    fun getAllNoteLocations(): Flowable<List<NoteLocation>>

    @Query("SELECT * FROM ${NoteLocation.TABLE_NAME} WHERE ${NoteLocation.FIELD_IS_DELETED} = 1")
    fun getDeletedNoteLocations(): Flowable<List<NoteLocation>>

    @Query("SELECT ${MyLocation.TABLE_NAME}.* FROM ${MyLocation.TABLE_NAME} " +
            "INNER JOIN ${NoteLocation.TABLE_NAME} " +
            "ON ${MyLocation.TABLE_NAME}.${MyLocation.FIELD_ID} = ${NoteLocation.TABLE_NAME}.${NoteLocation.FIELD_LOCATION_ID} " +
            "AND ${MyLocation.FIELD_IS_DELETED} = 0 " +
            "WHERE ${NoteLocation.FIELD_NOTE_ID} = :noteId AND ${NoteLocation.FIELD_IS_DELETED} = 0")
    fun getLocationsForNote(noteId: String): Flowable<List<MyLocation>>

    @Query("DELETE FROM ${NoteLocation.TABLE_NAME} WHERE ${NoteLocation.FIELD_IS_DELETED} = 1")
    fun cleanup(): Completable
}