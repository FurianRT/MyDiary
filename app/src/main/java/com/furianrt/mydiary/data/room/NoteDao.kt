package com.furianrt.mydiary.data.room

import android.arch.persistence.room.*
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteWithProp
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface NoteDao {

    @Insert
    fun insert(note: MyNote): Long

    @Update
    fun update(note: MyNote)

    @Delete
    fun delete(note: MyNote)

    @Query("SELECT * FROM Notes ORDER BY time DESC")
    fun getAllNotes(): Flowable<List<MyNote>>

    @Query("SELECT * FROM Notes WHERE id_note =:noteId")
    fun findNote(noteId: Long): Maybe<MyNote>

    @Transaction
    @Query("SELECT * FROM Notes INNER JOIN Moods ON mood = id_mood INNER JOIN Locations ON location = id_location INNER JOIN Categories ON category = id_category")
    fun getNotesWithProp(): Flowable<List<MyNoteWithProp>>
}