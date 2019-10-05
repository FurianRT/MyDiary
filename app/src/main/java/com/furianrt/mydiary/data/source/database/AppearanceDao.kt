/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.source.database

import androidx.room.*
import com.furianrt.mydiary.data.entity.MyNoteAppearance
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface AppearanceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(appearance: MyNoteAppearance): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(appearance: List<MyNoteAppearance>): Completable

    @Update
    fun update(appearance: MyNoteAppearance): Completable

    @Update
    fun update(appearance: List<MyNoteAppearance>): Completable

    @Query("UPDATE NoteAppearances " +
            "SET is_appearance_deleted = 1, appearance_sync_with = '[]' " +
            "WHERE id_appearance = :noteId")
    fun delete(noteId: String): Completable

    @Query("DELETE FROM NoteAppearances WHERE is_appearance_deleted = 1")
    fun cleanup(): Completable

    @Query("SELECT * FROM NoteAppearances WHERE id_appearance = :appearanceId")
    fun getNoteAppearance(appearanceId: String): Flowable<MyNoteAppearance>

    @Query("SELECT * FROM NoteAppearances WHERE is_appearance_deleted = 1")
    fun getDeletedAppearances(): Flowable<List<MyNoteAppearance>>

    @Query("SELECT * FROM NoteAppearances")
    fun getAllNoteAppearances(): Flowable<List<MyNoteAppearance>>
}