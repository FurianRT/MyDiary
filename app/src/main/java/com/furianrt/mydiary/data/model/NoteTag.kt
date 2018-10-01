package com.furianrt.mydiary.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

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
data class NoteTag(
        @ColumnInfo(name = "id_note", index = true) var noteId: String,
        @ColumnInfo(name = "id_tag", index = true) var tagId: Long
) : Parcelable {

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readLong())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(noteId)
        parcel.writeLong(tagId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NoteTag> {
        override fun createFromParcel(parcel: Parcel): NoteTag {
            return NoteTag(parcel)
        }

        override fun newArray(size: Int): Array<NoteTag?> {
            return arrayOfNulls(size)
        }
    }
}