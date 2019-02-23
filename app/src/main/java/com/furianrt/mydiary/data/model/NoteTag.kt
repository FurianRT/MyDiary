package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import kotlinx.android.parcel.Parcelize

@Entity(
        tableName = "NoteTag",
        primaryKeys = ["id_tag", "id_note"],
        foreignKeys = [
            ForeignKey(
                    entity = MyTag::class,
                    parentColumns = ["id_tag"],
                    childColumns = ["id_tag"],
                    onDelete = ForeignKey.CASCADE
            ),
            ForeignKey(
                    entity = MyNote::class,
                    parentColumns = ["id_note"],
                    childColumns = ["id_note"],
                    onDelete = ForeignKey.CASCADE
            )
        ]
)
@Parcelize
data class NoteTag(
        @ColumnInfo(name = "id_note", index = true) var noteId: String = "",
        @ColumnInfo(name = "id_tag", index = true) var tagId: String = "",
        @ColumnInfo(name = "is_notetag_sync") var isSync: Boolean = false,
        @ColumnInfo(name = "is_notetag_deleted") var isDeleted: Boolean = false
) : Parcelable