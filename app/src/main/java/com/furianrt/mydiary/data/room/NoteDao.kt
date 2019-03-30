package com.furianrt.mydiary.data.room

import androidx.room.*
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteWithProp
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(note: MyNote)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(notes: List<MyNote>)

    @Update
    fun update(note: MyNote)

    @Update
    fun update(notes: List<MyNote>)

    @Query("UPDATE Notes SET title = :title, content = :content, note_sync_with = '[]' WHERE id_note = :noteId")
    fun updateNoteText(noteId: String, title: String, content: String)

    @Query("UPDATE Notes SET is_note_deleted = 1, note_sync_with = '[]' WHERE id_note = :noteId")
    fun delete(noteId: String)

    @Query("DELETE FROM Notes WHERE is_note_deleted = 1")
    fun cleanup()

    @Query("SELECT * FROM Notes WHERE is_note_deleted = 1")
    fun getDeletedNotes(): Flowable<List<MyNote>>

    @Query("SELECT * FROM Notes WHERE is_note_deleted = 0")
    fun getAllNotes(): Flowable<List<MyNote>>

    @Query("SELECT * FROM Notes WHERE id_note =:noteId AND is_note_deleted = 0")
    fun findNote(noteId: String): Maybe<MyNote>

    @Query("SELECT * FROM Notes WHERE id_note =:noteId AND is_note_deleted = 0")
    fun getNote(noteId: String): Flowable<MyNote>

    @Transaction
    @Query("SELECT * FROM Notes " +
            "LEFT JOIN Moods ON mood = id_mood AND is_note_deleted = 0 " +
            "LEFT JOIN Locations ON location = name_location AND is_location_deleted = 0 " +
            "LEFT JOIN NoteAppearances ON id_note = id_appearance " +
            "LEFT JOIN Categories ON category = id_category AND is_category_deleted = 0 " +
            "WHERE is_note_deleted = 0 " +
            "ORDER BY time DESC")
    fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>>
}