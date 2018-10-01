package com.furianrt.mydiary.data.room

import androidx.room.*
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteWithProp
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
abstract class NoteDao {

    @Insert
    abstract fun insert(note: MyNote)

    @Update
    abstract fun update(note: MyNote)

    @Delete
    abstract fun delete(note: MyNote)

    @Delete
    abstract fun delete(notes: List<MyNote>)

    @Query("SELECT * FROM Notes ORDER BY time DESC")
    abstract fun getAllNotes(): Flowable<List<MyNote>>

    @Query("SELECT * FROM Notes WHERE id_note =:noteId")
    abstract fun findNote(noteId: String): Maybe<MyNote>

    @Transaction
    @Query("SELECT * FROM Notes LEFT JOIN Locations ON location = id_location " +
            "LEFT JOIN Categories ON category = id_category ORDER BY time DESC")
    abstract fun getNotesWithProp(): Flowable<List<MyNoteWithProp>>
}