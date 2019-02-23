package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = MyHeaderImage.TABLE_NAME)
data class MyHeaderImage(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = FIELD_ID) var id: Int,
        @ColumnInfo(name = FIELD_URL) var url: String,
        @ColumnInfo(name = FIELD_ADDED_TIME) var addedTime: Long
): Parcelable {

    companion object {
        const val TABLE_NAME = "HeaderImages"
        const val FIELD_ID= "id"
        const val FIELD_URL = "url"
        const val FIELD_ADDED_TIME = "added_time"
    }
}