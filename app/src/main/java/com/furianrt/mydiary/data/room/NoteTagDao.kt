package com.furianrt.mydiary.data.room

import android.arch.persistence.room.*
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.model.NoteTag
import io.reactivex.Maybe

@Dao
abstract class NoteTagDao {

    @Transaction
    open fun insertTagsForNote(noteId: Long, tags: List<MyTag>) {
        for (tag in tags) insert(NoteTag(noteId, tag.id))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(noteTag: NoteTag)

    @Delete
    abstract fun delete(noteTag: NoteTag)

    @Query("DELETE FROM NoteTag WHERE id_note = :noteId")
    abstract fun deleteAllTagsForNote(noteId: Long)

    @Query("SELECT * FROM Tags INNER JOIN NoteTag ON Tags.id_tag = NoteTag.id_tag WHERE NoteTag.id_note = :noteId")
    abstract fun getTagsForNote(noteId: Long): Maybe<List<MyTag>>

    @Query("SELECT * FROM Notes INNER JOIN NoteTag ON Notes.id_note = NoteTag.id_note WHERE NoteTag.id_tag = :tagId")
    abstract fun getNotesWithTag(tagId: Long): Maybe<List<MyNote>>
}