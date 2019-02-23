package com.furianrt.mydiary.data.room

import androidx.room.*
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.model.NoteTag
import io.reactivex.Flowable

@Dao
abstract class NoteTagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(noteTag: NoteTag)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(noteTags: List<NoteTag>)

    @Query("UPDATE NoteTag SET is_notetag_deleted = 1 WHERE id_note = :noteId")
    abstract fun deleteWithNoteId(noteId: String)

    @Query("UPDATE NoteTag SET is_notetag_deleted = 1 WHERE id_tag = :tagId")
    abstract fun deleteWithTagId(tagId: String)

    @Query("UPDATE NoteTag SET is_notetag_deleted = 1 WHERE id_note = :noteId AND id_tag = :tagId")
    abstract fun delete(noteId: String, tagId: String)

    fun delete(noteTag: NoteTag) {
        delete(noteTag.noteId, noteTag.tagId)
    }

    @Query("UPDATE NoteTag SET is_notetag_deleted = 1 WHERE id_note = :noteId")
    abstract fun deleteAllTagsForNote(noteId: String)

    @Query("DELETE FROM NoteTag WHERE is_notetag_deleted = 1")
    abstract fun cleanup()

    @Query("SELECT * FROM NoteTag WHERE is_notetag_deleted = 0")
    abstract fun getAllNoteTags(): Flowable<List<NoteTag>>

    @Query("SELECT * FROM NoteTag WHERE is_notetag_deleted = 1")
    abstract fun getDeletedNoteTags(): Flowable<List<NoteTag>>

    @Query("SELECT Tags.* FROM Tags " +
            "INNER JOIN NoteTag ON Tags.id_tag = NoteTag.id_tag AND is_notetag_deleted = 0 " +
            "WHERE id_note = :noteId AND is_tag_deleted = 0")
    abstract fun getTagsForNote(noteId: String): Flowable<List<MyTag>>

    @Transaction
    open fun replaceNoteTags(noteId: String, tags: List<MyTag>) {
        deleteAllTagsForNote(noteId)
        for (tag in tags) {
            insert(NoteTag(noteId, tag.id))
        }
    }
}