package com.furianrt.mydiary.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Categories")
data class MyCategory(
        @ColumnInfo(name = "id_category") @PrimaryKey(autoGenerate = true) var id: Long,
        @ColumnInfo(name = "name_category") var name: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyCategory> {
        override fun createFromParcel(parcel: Parcel): MyCategory {
            return MyCategory(parcel)
        }

        override fun newArray(size: Int): Array<MyCategory?> {
            return arrayOfNulls(size)
        }
    }
}