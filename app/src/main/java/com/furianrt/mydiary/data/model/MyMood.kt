package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Moods")
data class MyMood(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_mood") var id: Int = 0,
        @ColumnInfo(name = "name_mood") var name: String,
        @ColumnInfo(name = "icon_mood") var iconName: String
) : Parcelable