package com.furianrt.mydiary.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "Tags")
data class MyTag(@ColumnInfo(name = "name_tag") var name: String) : Parcelable {

    @ColumnInfo(name = "id_tag")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @Ignore
    var isChecked: Boolean = false

    constructor(parcel: Parcel) : this(parcel.readString()!!) {
        id = parcel.readLong()
        isChecked = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeLong(id)
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