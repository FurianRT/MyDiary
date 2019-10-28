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
@Entity(tableName = MyForecast.TABLE_NAME)
data class MyForecast(
        @ColumnInfo(name = FIELD_NOTE_ID) @PrimaryKey(autoGenerate = false) var noteId: String = "",
        @ColumnInfo(name = FIELD_TEMP) var temp: Double = 0.0,
        @ColumnInfo(name = FIELD_ICON) var icon: String = "",
        @ColumnInfo(name = FIELD_SYNC_WITH) var syncWith: MutableList<String> = mutableListOf(),
        @ColumnInfo(name = FIELD_IS_DELETED) var isDeleted: Boolean = false
) : Parcelable {

    companion object {
        const val TABLE_NAME = "Forecasts"
        const val FIELD_NOTE_ID = "note_id"
        const val FIELD_TEMP = "temp"
        const val FIELD_ICON = "icon"
        const val FIELD_SYNC_WITH = "forecast_sync_with"
        const val FIELD_IS_DELETED = "is_forecast_deleted"
    }

    fun isSync(email: String) = syncWith.contains(email)
}