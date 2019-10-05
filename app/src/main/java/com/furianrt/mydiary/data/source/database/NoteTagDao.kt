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
import com.furianrt.mydiary.data.entity.MyTag
import com.furianrt.mydiary.data.entity.NoteTag
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

    @Query("UPDATE NoteTag SET is_notetag_deleted = 1, notetag_sync_with = '[]' WHERE id_note = :noteId AND id_tag = :tagId")
    fun delete(noteId: String, tagId: String): Completable

    @Query("UPDATE NoteTag SET is_notetag_deleted = 1, notetag_sync_with = '[]' WHERE id_note = :noteId")
    fun deleteWithNoteId(noteId: String): Completable

    @Query("UPDATE NoteTag SET is_notetag_deleted = 1, notetag_sync_with = '[]' WHERE id_tag = :tagId")
    fun deleteWithTagId(tagId: String): Completable

    @Query("DELETE FROM NoteTag WHERE is_notetag_deleted = 1")
    fun cleanup(): Completable

    @Query("SELECT * FROM NoteTag WHERE is_notetag_deleted = 0")
    fun getAllNoteTags(): Flowable<List<NoteTag>>

    @Query("SELECT * FROM NoteTag WHERE is_notetag_deleted = 1")
    fun getDeletedNoteTags(): Flowable<List<NoteTag>>

    @Query("SELECT Tags.* FROM Tags " +
            "INNER JOIN NoteTag ON Tags.id_tag = NoteTag.id_tag AND is_notetag_deleted = 0 " +
            "WHERE id_note = :noteId AND is_tag_deleted = 0")
    fun getTagsForNote(noteId: String): Flowable<List<MyTag>>
}