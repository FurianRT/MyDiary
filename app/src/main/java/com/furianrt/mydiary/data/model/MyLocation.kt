package com.furianrt.mydiary.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Locations")
data class MyLocation(@ColumnInfo(name = "name_location") var name: String,
                      @ColumnInfo(name = "lat") var lat: Double,
                      @ColumnInfo(name = "lon") var lon: Double) {

    @ColumnInfo(name = "id_location")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}