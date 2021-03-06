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
import com.furianrt.mydiary.model.entity.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(note: MyNote): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(notes: List<MyNote>): Completable

    @Update
    fun update(note: MyNote): Completable

    @Update
    fun update(notes: List<MyNote>): Completable

    @Query("UPDATE ${MyNote.TABLE_NAME} SET ${MyNote.FIELD_TITLE} = :title, ${MyNote.FIELD_CONTENT} = :content, ${MyNote.FIELD_SYNC_WITH} = '[]' WHERE ${MyNote.FIELD_ID} = :noteId")
    fun updateNoteText(noteId: String, title: String, content: String): Completable

    @Query("UPDATE ${MyNote.TABLE_NAME} SET ${MyNote.FIELD_IS_DELETED} = 1, ${MyNote.FIELD_SYNC_WITH} = '[]' WHERE ${MyNote.FIELD_ID} = :noteId")
    fun delete(noteId: String): Completable

    @Query("DELETE FROM ${MyNote.TABLE_NAME} WHERE ${MyNote.FIELD_IS_DELETED} = 1")
    fun cleanup(): Completable

    @Query("SELECT * FROM ${MyNote.TABLE_NAME} WHERE ${MyNote.FIELD_IS_DELETED} = 1")
    fun getDeletedNotes(): Flowable<List<MyNote>>

    @Query("SELECT * FROM ${MyNote.TABLE_NAME} WHERE ${MyNote.FIELD_IS_DELETED} = 0")
    fun getAllNotes(): Flowable<List<MyNote>>

    @Query("SELECT * FROM ${MyNote.TABLE_NAME} WHERE id_note =:noteId AND ${MyNote.FIELD_IS_DELETED} = 0")
    fun getNoteAsList(noteId: String): Flowable<List<MyNote>>

    @Transaction
    @Query("SELECT * FROM ${MyNote.TABLE_NAME} " +
            "LEFT JOIN ${MyMood.TABLE_NAME} " +
            "ON ${MyNote.FIELD_MOOD} = ${MyMood.FIELD_ID} " +
            "LEFT JOIN ${MyCategory.TABLE_NAME} " +
            "ON ${MyNote.FIELD_CATEGORY} = ${MyCategory.FIELD_ID} " +
            "AND ${MyCategory.FIELD_IS_DELETED} = 0 " +
            "LEFT JOIN ${MyNoteAppearance.TABLE_NAME} " +
            "ON ${MyNote.FIELD_ID} = ${MyNoteAppearance.FIELD_ID} " +
            "AND ${MyNoteAppearance.FIELD_IS_DELETED} = 0 " +
            "WHERE ${MyNote.FIELD_IS_DELETED} = 0 " +
            "ORDER BY time DESC")
    fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>>

    @Transaction
    @Query("SELECT * FROM ${MyNote.TABLE_NAME} " +
            "LEFT JOIN ${MyMood.TABLE_NAME} " +
            "ON ${MyNote.FIELD_MOOD} = ${MyMood.FIELD_ID} " +
            "LEFT JOIN ${MyCategory.TABLE_NAME} " +
            "ON ${MyNote.FIELD_CATEGORY} = ${MyCategory.FIELD_ID} " +
            "AND ${MyCategory.FIELD_IS_DELETED} = 0 " +
            "LEFT JOIN ${MyNoteAppearance.TABLE_NAME} " +
            "ON ${MyNote.FIELD_ID} = ${MyNoteAppearance.FIELD_ID} " +
            "AND ${MyNoteAppearance.FIELD_IS_DELETED} = 0 " +
            "WHERE ${MyNote.FIELD_IS_DELETED} = 0 " +
            "AND ${MyNote.FIELD_ID} = :noteId " +
            "ORDER BY time DESC")
    fun getNoteWithProp(noteId: String): Flowable<List<MyNoteWithProp>>
}