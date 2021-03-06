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
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/*@Entity(
        tableName = MyImage.TABLE_NAME,
        foreignKeys = [
            ForeignKey(
                    entity = MyNote::class,
                    parentColumns = [MyNote.FIELD_ID],
                    childColumns = [MyImage.FIELD_ID_NOTE],
                    onDelete = ForeignKey.CASCADE
            )
        ]
)*/
@Parcelize
@Entity(tableName = MyImage.TABLE_NAME)
data class MyImage(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = FIELD_NAME) var name: String = "",
        @ColumnInfo(name = FIELD_URI) var path: String = "",
        @ColumnInfo(name = FIELD_ID_NOTE, index = true) var noteId: String = "",
        @ColumnInfo(name = FIELD_ADDED_TIME) var addedTime: Long = System.currentTimeMillis(),
        @ColumnInfo(name = FIELD_EDITED_TIME) var editedTime: Long = System.currentTimeMillis(),
        @ColumnInfo(name = FIELD_ORDER) var order: Int = Int.MAX_VALUE,
        @ColumnInfo(name = FIELD_SYNC_WITH) var syncWith: MutableList<String> = mutableListOf(),
        @ColumnInfo(name = FIELD_FILE_SYNC_WITH) var fileSyncWith: MutableList<String> = mutableListOf(),
        @ColumnInfo(name = FIELD_IS_DELETED) var isDeleted: Boolean = false
) : Parcelable {

    companion object {
        const val TABLE_NAME = "Images"
        const val FIELD_NAME = "name"
        const val FIELD_URI = "uri"
        const val FIELD_ID_NOTE = "id_note_image"
        const val FIELD_ADDED_TIME = "time_added"
        const val FIELD_EDITED_TIME = "edited_time"
        const val FIELD_ORDER = "image_order"
        const val FIELD_SYNC_WITH = "image_sync_with"
        const val FIELD_FILE_SYNC_WITH = "image_file_sync_with"
        const val FIELD_IS_DELETED = "is_image_deleted"
        const val DEFAULT_SYNC_EMAIL = "default"
    }

    fun isSync(email: String) = syncWith.contains(DEFAULT_SYNC_EMAIL) || syncWith.contains(email)
    fun isFileSync(email: String) = fileSyncWith.contains(DEFAULT_SYNC_EMAIL) || fileSyncWith.contains(email)
}