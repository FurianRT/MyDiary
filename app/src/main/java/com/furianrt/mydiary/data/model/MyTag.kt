package com.furianrt.mydiary.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "Tags")
data class MyTag(
        @ColumnInfo(name = "id_tag") @PrimaryKey(autoGenerate = false) var id: String,
        @ColumnInfo(name = "name_tag") var name: String
) : Parcelable {

    @Ignore
    var isChecked: Boolean = false

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "") {
        isChecked = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeByte(if (isChecked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyTag> {
        override fun createFromParcel(parcel: Parcel): MyTag {
            return MyTag(parcel)
        }

        override fun newArray(size: Int): Array<MyTag?> {
            return arrayOfNulls(size)
        }
    }
}