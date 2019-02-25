package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

/*@Entity(
        tableName = NoteTag.TABLE_NAME,
        primaryKeys = [NoteTag.FIELD_TAG_ID, NoteTag.FIELD_NOTE_ID],
        foreignKeys = [
            ForeignKey(
                    entity = MyTag::class,
                    parentColumns = [MyTag.FIELD_ID],
                    childColumns = [NoteTag.FIELD_TAG_ID],
                    onDelete = ForeignKey.CASCADE
            ),
            ForeignKey(
                    entity = MyNote::class,
                    parentColumns = [MyNote.FIELD_ID],
                    childColumns = [NoteTag.FIELD_NOTE_ID],
                    onDelete = ForeignKey.CASCADE
            )
        ]
)*/
@Parcelize
@Entity(
        tableName = NoteTag.TABLE_NAME,
        primaryKeys = [NoteTag.FIELD_TAG_ID, NoteTag.FIELD_NOTE_ID]
)
data class NoteTag(
        @ColumnInfo(name = FIELD_NOTE_ID, index = true) var noteId: String = "",
        @ColumnInfo(name = FIELD_TAG_ID, index = true) var tagId: String = "",
        @ColumnInfo(name = FIELD_IS_SYNC) var isSync: Boolean = false,
        @ColumnInfo(name = FIELD_IS_DELETED) var isDeleted: Boolean = false
) : Parcelable {

    companion object {
        const val TABLE_NAME = "NoteTag"
        const val FIELD_NOTE_ID = "id_note"
        const val FIELD_TAG_ID = "id_tag"
        const val FIELD_IS_SYNC = "is_notetag_sync"
        const val FIELD_IS_DELETED = "is_notetag_deleted"
    }
}