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

    @Query("UPDATE Notes SET title = :title, content = :content WHERE id_note = :noteId")
    abstract fun updateNoteText(noteId: String, title: String, content: String)

    @Delete
    abstract fun delete(note: MyNote)

    @Delete
    abstract fun delete(notes: List<MyNote>)

    @Query("SELECT * FROM Notes ORDER BY time DESC")
    abstract fun getAllNotes(): Flowable<List<MyNote>>

    @Query("SELECT * FROM Notes WHERE id_note =:noteId")
    abstract fun findNote(noteId: String): Maybe<MyNote>

    @Query("SELECT * FROM Notes WHERE id_note =:noteId")
    abstract fun getNote(noteId: String): Flowable<MyNote>

    @Transaction
    @Query("SELECT * FROM Notes LEFT JOIN Moods ON mood = id_mood " +
            "LEFT JOIN Locations ON location = name_location " +
            "LEFT JOIN NoteAppearances ON id_note = id_appearance " +
            "LEFT JOIN Categories ON category = id_category WHERE id_note =:noteId")
    abstract fun getNoteWithProp(noteId: String): Flowable<MyNoteWithProp>

    @Transaction
    @Query("SELECT * FROM Notes LEFT JOIN Moods ON mood = id_mood " +
            "LEFT JOIN Locations ON location = name_location " +
            "LEFT JOIN NoteAppearances ON id_note = id_appearance " +
            "LEFT JOIN Categories ON category = id_category ORDER BY time DESC")
    abstract fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>>
}