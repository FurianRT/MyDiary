package com.furianrt.mydiary.data.room

import androidx.room.*
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.model.NoteTag
import io.reactivex.Flowable

@Dao
abstract class NoteTagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(noteTag: NoteTag)

    @Delete
    abstract fun delete(noteTag: NoteTag)

    @Query("DELETE FROM NoteTag WHERE id_note = :noteId")
    abstract fun deleteAllTagsForNote(noteId: String)

    @Query("SELECT Tags.* FROM Tags " +
            "INNER JOIN NoteTag ON Tags.id_tag = NoteTag.id_tag WHERE id_note = :noteId")
    abstract fun getTagsForNote(noteId: String): Flowable<List<MyTag>>

    @Query("SELECT Notes.* FROM Notes " +
            "INNER JOIN NoteTag ON Notes.id_note = NoteTag.id_note WHERE id_tag = :tagId")
    abstract fun getNotesWithTag(tagId: Long): Flowable<List<MyNote>>

    @Transaction
    open fun replaceNoteTags(noteId: String, tags: List<MyTag>) {
        deleteAllTagsForNote(noteId)
        for (tag in tags) {
            insert(NoteTag(noteId, tag.id))
        }
    }
}