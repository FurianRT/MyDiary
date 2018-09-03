package com.furianrt.mydiary.data.room

import android.arch.persistence.room.*
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.NoteWithTags
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

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

    @Transaction
    @Query("SELECT * FROM Notes WHERE id=:noteId")
    fun getNoteWithTags(noteId: Long): Single<NoteWithTags>

    @Query("SELECT * FROM Notes WHERE id =:noteId")
    fun findNote(noteId: Long): Maybe<MyNote>
}