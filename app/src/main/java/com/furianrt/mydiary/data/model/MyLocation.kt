package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = MyLocation.TABLE_NAME)
data class MyLocation(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = FIELD_NAME) var name: String,
        @ColumnInfo(name = FIELD_LAT) var lat: Double,
        @ColumnInfo(name = FIELD_LON) var lon: Double,
        @ColumnInfo(name = FIELD_IS_SYNC) var syncWith: MutableList<String> = mutableListOf(),
        @ColumnInfo(name = FIELD_IS_DELETED) var idDeleted: Boolean = false
) : Parcelable {

    companion object {
        const val TABLE_NAME = "Locations"
        const val FIELD_NAME = "name_location"
        const val FIELD_LAT = "lat"
        const val FIELD_LON = "lon"
        const val FIELD_IS_SYNC = "location_sync_with"
        const val FIELD_IS_DELETED = "is_location_deleted"
    }

    fun isSync(email: String) = syncWith.contains(email)
}