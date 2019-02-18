package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Locations")
data class MyLocation(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "name_location") var name: String,
        @ColumnInfo(name = "lat") var lat: Double,
        @ColumnInfo(name = "lon") var lon: Double
) : Parcelable