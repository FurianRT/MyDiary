package com.furianrt.mydiary.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.furianrt.mydiary.data.api.Forecast

@Entity(tableName = "Notes")
data class MyNote(
        @ColumnInfo(name = "id_note") @PrimaryKey(autoGenerate = false) var id: String,
        @ColumnInfo(name = "title") var title: String,
        @ColumnInfo(name = "content") var content: String,
        @ColumnInfo(name = "time") var time: Long
) : Parcelable {

    @ColumnInfo(name = "mood")
    var idMood: Long = 0

    @ColumnInfo(name = "location")
    var locationId: Long = 0

    @ColumnInfo(name = "category")
    var categoryId: Long = 0

    @ColumnInfo(name = "forecast")
    var forecast: Forecast? = null

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong()) {
        idMood = parcel.readLong()
        locationId = parcel.readLong()
        categoryId = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeLong(time)
        parcel.writeLong(idMood)
        parcel.writeLong(locationId)
        parcel.writeLong(categoryId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyNote> {
        override fun createFromParcel(parcel: Parcel): MyNote {
            return MyNote(parcel)
        }

        override fun newArray(size: Int): Array<MyNote?> {
            return arrayOfNulls(size)
        }
    }
}