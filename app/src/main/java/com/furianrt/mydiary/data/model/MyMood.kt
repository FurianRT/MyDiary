package com.furianrt.mydiary.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Moods")
data class MyMood(
        @ColumnInfo(name = "name_mood") var name: String,
        @ColumnInfo(name = "icon_mood") var iconName: String
) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_mood")
    var id: Int = 0
}