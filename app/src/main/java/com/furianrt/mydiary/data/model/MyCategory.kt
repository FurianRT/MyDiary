package com.furianrt.mydiary.data.model

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Categories")
data class MyCategory(
        @ColumnInfo(name = "name_category") var name: String = "",
        @ColumnInfo(name = "color") var color: Int = DEFAULT_COLOR
) : Parcelable {

    @ColumnInfo(name = "id_category")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt()) {
        id = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(color)
        parcel.writeLong(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyCategory> {

        const val DEFAULT_COLOR = Color.GREEN

        override fun createFromParcel(parcel: Parcel): MyCategory {
            return MyCategory(parcel)
        }

        override fun newArray(size: Int): Array<MyCategory?> {
            return arrayOfNulls(size)
        }
    }
}