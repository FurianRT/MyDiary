package com.furianrt.mydiary.data.room

import androidx.room.*
import com.furianrt.mydiary.data.model.MyNoteAppearance
import io.reactivex.Flowable

@Dao
interface AppearanceDao {

    @Insert
    fun insert(appearance: MyNoteAppearance)

    @Update
    fun update(appearance: MyNoteAppearance)

    @Delete
    fun delete(appearance: MyNoteAppearance)

    @Query("SELECT * FROM NoteAppearances WHERE id_appearance = :appearanceId")
    fun getNoteAppearance(appearanceId: String): Flowable<MyNoteAppearance>
}