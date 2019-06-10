package com.furianrt.mydiary.data.database

import androidx.room.*
import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.model.NoteLocation
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface NoteLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(noteLocation: NoteLocation): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(noteLocations: List<NoteLocation>): Completable

    @Update
    fun update(noteLocations: List<NoteLocation>): Completable

    @Query("UPDATE NoteLocation SET is_notelocation_deleted = 1, notelocation_sync_with = '[]' WHERE id_note = :noteId AND name_location = :locationName")
    fun delete(noteId: String, locationName: String): Completable

    @Query("UPDATE NoteLocation SET is_notelocation_deleted = 1, notelocation_sync_with = '[]' WHERE id_note = :noteId")
    fun deleteWithNoteId(noteId: String): Completable

    @Query("UPDATE NoteLocation SET is_notelocation_deleted = 1, notelocation_sync_with = '[]' WHERE name_location = :locationName")
    fun deleteWithLocationName(locationName: String): Completable

    @Query("SELECT * FROM NoteLocation WHERE is_notelocation_deleted = 0")
    fun getAllNoteLocations(): Flowable<List<NoteLocation>>

    @Query("SELECT * FROM NoteLocation WHERE is_notelocation_deleted = 1")
    fun getDeletedNoteLocations(): Flowable<List<NoteLocation>>

    @Query("SELECT Locations.* FROM Locations INNER JOIN NoteLocation ON Locations.name_location = NoteLocation.name_location AND is_notelocation_deleted = 0 WHERE id_note = :noteId AND is_location_deleted = 0")
    fun getLocationsForNote(noteId: String): Flowable<List<MyLocation>>

    @Query("DELETE FROM NoteLocation WHERE is_notelocation_deleted = 1")
    fun cleanup(): Completable
}