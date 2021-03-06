/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
        tableName = NoteLocation.TABLE_NAME,
        primaryKeys = [NoteLocation.FIELD_NOTE_ID, NoteLocation.FIELD_LOCATION_ID]
)
data class NoteLocation(
        @ColumnInfo(name = FIELD_NOTE_ID, index = true) var noteId: String = "",
        @ColumnInfo(name = FIELD_LOCATION_ID, index = true) var locationId: String = "",
        @ColumnInfo(name = FIELD_SYNC_WITH) var syncWith: MutableList<String> = mutableListOf(),
        @ColumnInfo(name = FIELD_IS_DELETED) var isDeleted: Boolean = false
) : Parcelable {

    companion object {
        const val TABLE_NAME = "NoteLocation"
        const val FIELD_NOTE_ID = "id_note"
        const val FIELD_LOCATION_ID = "id_location"
        const val FIELD_SYNC_WITH = "notelocation_sync_with"
        const val FIELD_IS_DELETED = "is_notelocation_deleted"
    }

    fun isSync(email: String) = syncWith.contains(email)
}