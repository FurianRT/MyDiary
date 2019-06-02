package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
        tableName = NoteLocation.TABLE_NAME,
        primaryKeys = [NoteLocation.FIELD_LOCATION_NAME, NoteLocation.FIELD_NOTE_ID]
)
data class NoteLocation(
        @ColumnInfo(name = FIELD_NOTE_ID, index = true) var noteId: String = "",
        @ColumnInfo(name = FIELD_LOCATION_NAME, index = true) var locationName: String = "",
        @ColumnInfo(name = FIELD_SYNC_WITH) var syncWith: MutableList<String> = mutableListOf(),
        @ColumnInfo(name = FIELD_IS_DELETED) var isDeleted: Boolean = false
) : Parcelable {
    companion object {
        const val TABLE_NAME = "NoteLocation"
        const val FIELD_NOTE_ID = "id_note"
        const val FIELD_LOCATION_NAME = "name_location"
        const val FIELD_SYNC_WITH = "notelocation_sync_with"
        const val FIELD_IS_DELETED = "is_notelocation_deleted"
    }

    fun isSync(email: String) = syncWith.contains(email)
}