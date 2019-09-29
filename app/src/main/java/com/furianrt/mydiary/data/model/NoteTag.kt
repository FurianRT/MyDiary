/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

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
        @ColumnInfo(name = FIELD_NOTE_ID) var noteId: String = "",
        @ColumnInfo(name = FIELD_TAG_ID) var tagId: String = "",
        @ColumnInfo(name = FIELD_SYNC_WITH) var syncWith: MutableList<String> = mutableListOf(),
        @ColumnInfo(name = FIELD_IS_DELETED) var isDeleted: Boolean = false
) : Parcelable {

    companion object {
        const val TABLE_NAME = "NoteTag"
        const val FIELD_NOTE_ID = "id_note"
        const val FIELD_TAG_ID = "id_tag"
        const val FIELD_SYNC_WITH = "notetag_sync_with"
        const val FIELD_IS_DELETED = "is_notetag_deleted"
    }

    fun isSync(email: String) = syncWith.contains(email)
}