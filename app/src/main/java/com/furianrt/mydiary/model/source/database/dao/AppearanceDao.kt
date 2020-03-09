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
import com.furianrt.mydiary.model.entity.MyNoteAppearance
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

    @Query("UPDATE ${MyNoteAppearance.TABLE_NAME} " +
            "SET ${MyNoteAppearance.FIELD_IS_DELETED} = 1, ${MyNoteAppearance.FIELD_SYNC_WITH} = '[]' " +
            "WHERE ${MyNoteAppearance.FIELD_ID} = :noteId")
    fun delete(noteId: String): Completable

    @Query("DELETE FROM ${MyNoteAppearance.TABLE_NAME} WHERE ${MyNoteAppearance.FIELD_IS_DELETED} = 1")
    fun cleanup(): Completable

    @Query("SELECT * FROM ${MyNoteAppearance.TABLE_NAME} WHERE ${MyNoteAppearance.FIELD_ID} = :appearanceId")
    fun getNoteAppearance(appearanceId: String): Flowable<List<MyNoteAppearance>>

    @Query("SELECT * FROM ${MyNoteAppearance.TABLE_NAME} WHERE ${MyNoteAppearance.FIELD_IS_DELETED} = 1")
    fun getDeletedAppearances(): Flowable<List<MyNoteAppearance>>

    @Query("SELECT * FROM ${MyNoteAppearance.TABLE_NAME}")
    fun getAllNoteAppearances(): Flowable<List<MyNoteAppearance>>
}