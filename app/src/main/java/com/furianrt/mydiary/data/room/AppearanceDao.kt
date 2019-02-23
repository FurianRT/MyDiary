package com.furianrt.mydiary.data.room

import androidx.room.*
import com.furianrt.mydiary.data.model.MyNoteAppearance
import io.reactivex.Flowable

@Dao
interface AppearanceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(appearance: MyNoteAppearance)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(appearance: List<MyNoteAppearance>)

    @Update
    fun update(appearance: MyNoteAppearance)

    @Update
    fun updateSync(appearance: List<MyNoteAppearance>)

    @Query("UPDATE NoteAppearances SET is_appearance_deleted = 1 WHERE id_appearance = :noteId")
    fun delete(noteId: String)

    @Query("DELETE FROM NoteAppearances WHERE is_appearance_deleted = 1")
    fun cleanup()

    @Query("SELECT * FROM NoteAppearances WHERE id_appearance = :appearanceId")
    fun getNoteAppearance(appearanceId: String): Flowable<MyNoteAppearance>

    @Query("SELECT * FROM NoteAppearances WHERE is_appearance_deleted = 1")
    fun getDeletedAppearances(): Flowable<List<MyNoteAppearance>>

    @Query("SELECT * FROM NoteAppearances")
    fun getAllNoteAppearances(): Flowable<List<MyNoteAppearance>>
}