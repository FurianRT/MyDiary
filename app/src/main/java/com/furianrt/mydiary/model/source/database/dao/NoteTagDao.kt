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
import com.furianrt.mydiary.model.entity.MyTag
import com.furianrt.mydiary.model.entity.NoteTag
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface NoteTagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(noteTag: NoteTag): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(noteTags: List<NoteTag>): Completable

    @Update
    fun update(noteTags: List<NoteTag>): Completable

    @Query("UPDATE ${NoteTag.TABLE_NAME} SET ${NoteTag.FIELD_IS_DELETED} = 1, ${NoteTag.FIELD_SYNC_WITH} = '[]' " +
            "WHERE ${NoteTag.FIELD_NOTE_ID} = :noteId AND ${NoteTag.FIELD_TAG_ID} = :tagId")
    fun delete(noteId: String, tagId: String): Completable

    @Query("UPDATE ${NoteTag.TABLE_NAME} SET ${NoteTag.FIELD_IS_DELETED} = 1, ${NoteTag.FIELD_SYNC_WITH} = '[]' " +
            "WHERE ${NoteTag.FIELD_NOTE_ID} = :noteId")
    fun deleteWithNoteId(noteId: String): Completable

    @Query("UPDATE ${NoteTag.TABLE_NAME} SET ${NoteTag.FIELD_IS_DELETED} = 1, ${NoteTag.FIELD_SYNC_WITH} = '[]' " +
            "WHERE ${NoteTag.FIELD_TAG_ID} = :tagId")
    fun deleteWithTagId(tagId: String): Completable

    @Query("DELETE FROM ${NoteTag.TABLE_NAME} WHERE ${NoteTag.FIELD_IS_DELETED} = 1")
    fun cleanup(): Completable

    @Query("SELECT * FROM ${NoteTag.TABLE_NAME} WHERE ${NoteTag.FIELD_IS_DELETED} = 0")
    fun getAllNoteTags(): Flowable<List<NoteTag>>

    @Query("SELECT * FROM ${NoteTag.TABLE_NAME} WHERE ${NoteTag.FIELD_IS_DELETED} = 1")
    fun getDeletedNoteTags(): Flowable<List<NoteTag>>

    @Query("SELECT ${MyTag.TABLE_NAME}.* FROM ${MyTag.TABLE_NAME} " +
            "INNER JOIN ${NoteTag.TABLE_NAME} " +
            "ON ${MyTag.TABLE_NAME}.${MyTag.FIELD_ID} = ${NoteTag.TABLE_NAME}.${NoteTag.FIELD_TAG_ID} " +
            "AND ${NoteTag.TABLE_NAME}.${NoteTag.FIELD_IS_DELETED} = 0 " +
            "AND ${NoteTag.TABLE_NAME}.${NoteTag.FIELD_NOTE_ID} = :noteId " +
            "WHERE ${MyTag.TABLE_NAME}.${MyTag.FIELD_IS_DELETED} = 0 " +
            "ORDER BY ${MyTag.FIELD_NAME}")
    fun getTagsForNote(noteId: String): Flowable<List<MyTag>>
}