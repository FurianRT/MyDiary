package com.furianrt.mydiary.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity(tableName = "Moods")
data class MyMood(@ColumnInfo(name = "id_mood") @PrimaryKey(autoGenerate = true) var id: Long,
                  @ColumnInfo(name = "name_mood") var name: String): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString()!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyMood> {
        override fun createFromParcel(parcel: Parcel): MyMood {
            return MyMood(parcel)
        }

        override fun newArray(size: Int): Array<MyMood?> {
            return arrayOfNulls(size)
        }
    }

}