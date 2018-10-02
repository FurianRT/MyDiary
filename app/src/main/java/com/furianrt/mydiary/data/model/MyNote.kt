package com.furianrt.mydiary.data.model

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.furianrt.mydiary.data.api.Forecast

@Entity(tableName = "Notes")
data class MyNote(
        @ColumnInfo(name = "id_note") @PrimaryKey(autoGenerate = false) var id: String,
        @ColumnInfo(name = "title") var title: String,
        @ColumnInfo(name = "content") var content: String,
        @ColumnInfo(name = "time") var time: Long,
        @ColumnInfo(name = "background_color") var background: Int = Color.LTGRAY,
        @ColumnInfo(name = "text_color") var textColor: Int = Color.BLACK,
        @ColumnInfo(name = "text_size") var textSize: Float = 16f
) : Parcelable {

    @ColumnInfo(name = "mood")
    var moodId: Int = 0

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
            parcel.readLong(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readFloat()) {
        moodId = parcel.readInt()
        locationId = parcel.readLong()
        categoryId = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeLong(time)
        parcel.writeInt(background)
        parcel.writeInt(textColor)
        parcel.writeFloat(textSize)
        parcel.writeInt(moodId)
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