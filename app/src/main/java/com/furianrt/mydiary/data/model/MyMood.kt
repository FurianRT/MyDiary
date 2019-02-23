package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = MyMood.TABLE_NAME)
data class MyMood(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = FIELD_ID) var id: Int = 0,
        @ColumnInfo(name = FIELD_NAME) var name: String,
        @ColumnInfo(name = FIELD_ICON) var iconName: String
) : Parcelable {

    companion object {
        const val TABLE_NAME = "Moods"
        const val FIELD_ID = "id_mood"
        const val FIELD_NAME = "name_mood"
        const val FIELD_ICON = "icon_mood"
    }
}