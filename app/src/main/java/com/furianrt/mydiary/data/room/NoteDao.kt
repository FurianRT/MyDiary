package com.furianrt.mydiary.data.room

import android.arch.persistence.room.*
import com.furianrt.mydiary.data.model.MyNote
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: MyNote): Long

    @Update
    fun update(note: MyNote)

    @Delete
    fun delete(note: MyNote)

    @Query("SELECT * FROM Notes ORDER BY time DESC")
    fun getAllNotes(): Flowable<List<MyNote>>

    @Query("SELECT * FROM Notes WHERE id_note =:noteId")
    fun findNote(noteId: Long): Maybe<MyNote>
}