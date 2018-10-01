package com.furianrt.mydiary.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
        tableName = "Images",
        foreignKeys = [
            ForeignKey(
                    entity = MyNote::class,
                    parentColumns = ["id_note"],
                    childColumns = ["id_note"],
                    onDelete = ForeignKey.CASCADE
            )
        ]
)
data class MyImage(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "url") var url: String,
        @ColumnInfo(name = "id_note", index = true) var noteId: String,
        @ColumnInfo(name = "time_added", index = true) var addedTime: Long
) : Parcelable {

    @ColumnInfo(name = "order")
    var order: Int = 0

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong()) {
        order = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(url)
        parcel.writeString(noteId)
        parcel.writeLong(addedTime)
        parcel.writeInt(order)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyImage> {
        override fun createFromParcel(parcel: Parcel): MyImage {
            return MyImage(parcel)
        }

        override fun newArray(size: Int): Array<MyImage?> {
            return arrayOfNulls(size)
        }
    }
}