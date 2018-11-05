package com.furianrt.mydiary.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.furianrt.mydiary.data.api.Forecast
import java.util.*

@Entity(tableName = "Notes")
data class MyNote(
        @ColumnInfo(name = "id_note") @PrimaryKey(autoGenerate = false) var id: String,
        @ColumnInfo(name = "title") var title: String,
        @ColumnInfo(name = "content") var content: String,
        @ColumnInfo(name = "time") var time: Long = Date().time,
        @ColumnInfo(name = "mood") var moodId: Int = 0,
        @ColumnInfo(name = "category")  var categoryId: Long = 0
) : Parcelable {

    @ColumnInfo(name = "location")
    var locationName: String? = null

    @ColumnInfo(name = "forecast")
    var forecast: Forecast? = null

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readLong()) {
        locationName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeLong(time)
        parcel.writeInt(moodId)
        parcel.writeLong(categoryId)
        parcel.writeString(locationName)
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