package com.furianrt.mydiary.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity(tableName = "Locations")
data class MyLocation(@ColumnInfo(name = "name_location") var name: String,
                      @ColumnInfo(name = "lat") var lat: Double,
                      @ColumnInfo(name = "lon") var lon: Double): Parcelable {

    @ColumnInfo(name = "id_location")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readDouble(),
            parcel.readDouble()) {
        id = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(lat)
        parcel.writeDouble(lon)
        parcel.writeLong(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyLocation> {
        override fun createFromParcel(parcel: Parcel): MyLocation {
            return MyLocation(parcel)
        }

        override fun newArray(size: Int): Array<MyLocation?> {
            return arrayOfNulls(size)
        }
    }
}