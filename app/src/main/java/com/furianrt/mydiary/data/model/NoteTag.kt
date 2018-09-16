package com.furianrt.mydiary.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.os.Parcel
import android.os.Parcelable

@Entity(
        tableName = "NoteTag",
        primaryKeys = ["id_tag", "id_note"],
        indices = [Index("id_tag"), Index("id_note")],
        foreignKeys = [
            ForeignKey(entity = MyTag::class, parentColumns = ["id_tag"],
                    childColumns = ["id_tag"], onDelete = ForeignKey.CASCADE),
            ForeignKey(entity = MyNote::class, parentColumns = ["id_note"],
                    childColumns = ["id_note"], onDelete = ForeignKey.CASCADE)
        ]
)
data class NoteTag(@ColumnInfo(name = "id_note") var noteId: Long,
                   @ColumnInfo(name = "id_tag") var tagId: Long): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(noteId)
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