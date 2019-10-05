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
import com.furianrt.mydiary.data.entity.MyNote
import com.furianrt.mydiary.data.entity.MyNoteWithProp
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

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

    @Query("UPDATE Notes SET title = :title, content = :content, note_sync_with = '[]' WHERE id_note = :noteId")
    fun updateNoteText(noteId: String, title: String, content: String): Completable

    @Query("UPDATE Notes SET is_note_deleted = 1, note_sync_with = '[]' WHERE id_note = :noteId")
    fun delete(noteId: String): Completable

    @Query("DELETE FROM Notes WHERE is_note_deleted = 1")
    fun cleanup(): Completable

    @Query("SELECT * FROM Notes WHERE is_note_deleted = 1")
    fun getDeletedNotes(): Flowable<List<MyNote>>

    @Query("SELECT * FROM Notes WHERE is_note_deleted = 0")
    fun getAllNotes(): Flowable<List<MyNote>>

    @Query("SELECT * FROM Notes WHERE id_note =:noteId AND is_note_deleted = 0")
    fun findNote(noteId: String): Maybe<MyNote>

    @Query("SELECT * FROM Notes WHERE id_note =:noteId AND is_note_deleted = 0")
    fun getNote(noteId: String): Single<MyNote>

    @Query("SELECT * FROM Notes WHERE id_note =:noteId AND is_note_deleted = 0")
    fun getNoteAsList(noteId: String): Flowable<List<MyNote>>

    @Transaction
    @Query("SELECT * FROM Notes " +
            "LEFT JOIN Moods ON mood = id_mood AND is_note_deleted = 0 " +
            "LEFT JOIN NoteAppearances ON id_note = id_appearance " +
            "LEFT JOIN Categories ON category = id_category AND is_category_deleted = 0 " +
            "WHERE is_note_deleted = 0 " +
            "ORDER BY time DESC")
    fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>>
}