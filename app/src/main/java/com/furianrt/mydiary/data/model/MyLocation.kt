package com.furianrt.mydiary.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Locations")
data class MyLocation(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "name_location") var name: String,
        @ColumnInfo(name = "lat") var lat: Double,
        @ColumnInfo(name = "lon") var lon: Double
) : Parcelable {

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(lat)
        parcel.writeDouble(lon)
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