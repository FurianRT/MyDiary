package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "HeaderImages")
data class MyHeaderImage(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") var id: Int,
        @ColumnInfo(name = "url") var url: String,
        @ColumnInfo(name = "added_time") var addedTime: Long
): Parcelable