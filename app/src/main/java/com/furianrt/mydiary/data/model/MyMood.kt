package com.furianrt.mydiary.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Moods")
data class MyMood(
        @ColumnInfo(name = "name_mood") var name: String,
        @ColumnInfo(name = "icon_mood") var iconName: String
) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_mood")
    var id: Int = 0

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "") {
        id = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(iconName)
        parcel.writeInt(id)
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