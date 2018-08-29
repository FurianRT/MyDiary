package com.furianrt.mydiary.data.room

import android.arch.persistence.room.*
import com.furianrt.mydiary.data.model.MyNote
import io.reactivex.Flowable
import io.reactivex.Single

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

    @Query("SELECT COUNT(*) FROM Notes WHERE id =:noteId")
    fun contains(noteId: Long): Single<Boolean>
}