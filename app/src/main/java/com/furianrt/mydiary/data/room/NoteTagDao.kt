package com.furianrt.mydiary.data.room

import android.arch.persistence.room.*
import com.furianrt.mydiary.data.model.NoteTag

@Dao
interface NoteTagDao {

    @Insert
    fun insert(noteTag: NoteTag)

    @Delete
    fun delete(noteTag: NoteTag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(noteTags: List<NoteTag>)

    @Query("DELETE FROM NoteTag WHERE id_note =:noteId")
    fun deleteAll(noteId: Long)
}