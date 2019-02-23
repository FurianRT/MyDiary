package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(
        tableName = MyImage.TABLE_NAME,
        foreignKeys = [
            ForeignKey(
                    entity = MyNote::class,
                    parentColumns = [MyNote.FIELD_ID],
                    childColumns = [MyImage.FIELD_ID_NOTE],
                    onDelete = ForeignKey.CASCADE
            )
        ]
)
@Parcelize
data class MyImage(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = FIELD_NAME) var name: String = "",
        @ColumnInfo(name = FIELD_URI) var uri: String = "",
        @ColumnInfo(name = FIELD_ID_NOTE, index = true) var noteId: String = "",
        @ColumnInfo(name = FIELD_ADDED_TIME, index = true) var addedTime: Long = 0L,
        @ColumnInfo(name = FIELD_ORDER) var order: Int = 0,
        @ColumnInfo(name = FIELD_IS_SYNC) var isSync: Boolean = false,
        @ColumnInfo(name = FIELD_IS_EDITED) var isEdited: Boolean = false,
        @ColumnInfo(name = FIELD_IS_DELETED) var isDeleted: Boolean = false
) : Parcelable {

    companion object {
        const val TABLE_NAME = "Images"
        const val FIELD_NAME = "name"
        const val FIELD_URI = "uri"
        const val FIELD_ID_NOTE = "id_note_image"
        const val FIELD_ADDED_TIME = "time_added"
        const val FIELD_ORDER = "order"
        const val FIELD_IS_SYNC = "is_image_sync"
        const val FIELD_IS_EDITED = "is_image_edited"
        const val FIELD_IS_DELETED = "is_image_deleted"
    }
}