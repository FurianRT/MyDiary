/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = MyLocation.TABLE_NAME)
data class MyLocation(
        @ColumnInfo(name = FIELD_ID) @PrimaryKey(autoGenerate = false) var id: String = "",
        @ColumnInfo(name = FIELD_NAME)var name: String = "",
        @ColumnInfo(name = FIELD_LAT) var lat: Double = 0.0,
        @ColumnInfo(name = FIELD_LON) var lon: Double = 0.0,
        @ColumnInfo(name = FIELD_SYNC_WITH) var syncWith: MutableList<String> = mutableListOf(),
        @ColumnInfo(name = FIELD_IS_DELETED) var isDeleted: Boolean = false
) : Parcelable {

    companion object {
        const val TABLE_NAME = "Locations"
        const val FIELD_ID = "id_location"
        const val FIELD_NAME = "name_location"
        const val FIELD_LAT = "lat"
        const val FIELD_LON = "lon"
        const val FIELD_SYNC_WITH = "location_sync_with"
        const val FIELD_IS_DELETED = "is_location_deleted"
    }

    fun isSync(email: String) = syncWith.contains(email)
}